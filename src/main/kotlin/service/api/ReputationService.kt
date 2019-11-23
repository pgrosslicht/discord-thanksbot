package com.grosslicht.discord.thanksbot.service.api

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class ReputationContext(val guildId: String, val memberId: String, val score: Double)

interface ReputationService {
    fun incrementReputation(guildId: String, memberId: String): Mono<ReputationContext>

    fun incrementReputation(guildId: String, memberIds: Collection<String>): Flux<ReputationContext>

    fun getReputation(guildId: String, memberId: String): Mono<ReputationContext>

    fun topReputation(guildId: String, amount: Long = 5): Flux<ReputationContext>
}