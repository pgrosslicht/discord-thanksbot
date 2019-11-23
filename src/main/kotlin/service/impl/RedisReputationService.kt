package com.grosslicht.discord.thanksbot.service.impl

import com.grosslicht.discord.thanksbot.service.api.ReputationContext
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import redis.clients.jedis.JedisPool

class RedisReputationService(private val jedisPool: JedisPool) : ReputationService {
    override fun incrementReputation(guildId: String, memberId: String): Mono<ReputationContext> {
        return Mono.fromCallable {
            ReputationContext(
                guildId,
                memberId,
                jedisPool.resource.use { it.zincrby(guildId, 1.0, memberId) }
            )
        }.subscribeOn(Schedulers.elastic())
    }

    override fun incrementReputation(guildId: String, memberIds: Collection<String>): Flux<ReputationContext> {
        return Flux.fromIterable(memberIds)
            .flatMap { incrementReputation(guildId, it) }
    }

    override fun getReputation(guildId: String, memberId: String): Mono<ReputationContext> {
        return Mono.fromCallable {
            ReputationContext(
                guildId,
                memberId,
                jedisPool.resource.use { it.zscore(guildId, memberId) } ?: 0.toDouble()
            )
        }.subscribeOn(Schedulers.elastic())
    }

    override fun topReputation(guildId: String, amount: Long): Flux<ReputationContext> {
        return Mono.fromCallable { jedisPool.resource.use { it.zrevrangeWithScores(guildId, 0, amount.dec().coerceAtLeast(0)) } }
            .subscribeOn(Schedulers.elastic())
            .flatMapMany { Flux.fromIterable(it) }
            .map { ReputationContext(guildId, it.element, it.score) }
    }
}