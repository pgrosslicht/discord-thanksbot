package com.grosslicht.discord.thanksbot.di

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider
import com.grosslicht.discord.thanksbot.commands.GetReputationCommand
import com.grosslicht.discord.thanksbot.commands.TopReputationCommand
import com.grosslicht.discord.thanksbot.config.BotConfig
import com.grosslicht.discord.thanksbot.config.BotSpec
import com.grosslicht.discord.thanksbot.listener.impl.CommandListener
import com.grosslicht.discord.thanksbot.listener.impl.ThanksListener
import com.grosslicht.discord.thanksbot.service.api.MessageHandler
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import com.grosslicht.discord.thanksbot.service.impl.MessageHandlerImpl
import com.grosslicht.discord.thanksbot.service.impl.RedisReputationService
import com.grosslicht.discord.thanksbot.service.ratelimit.api.RateLimiter
import com.grosslicht.discord.thanksbot.service.ratelimit.impl.Bucket4jRateLimiter
import com.uchuhimo.konf.Config
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.grid.GridBucketState
import io.github.bucket4j.grid.ProxyManager
import io.github.bucket4j.grid.jcache.JCache
import org.koin.core.qualifier.named
import org.koin.dsl.module
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.util.*
import javax.cache.Caching
import javax.cache.expiry.Duration
import javax.cache.expiry.TouchedExpiryPolicy


val botModule = module {
    single {
        Config { addSpec(BotSpec) }
            .from.env()
            .from.systemProperties()
    }
    single {
        val botConfig: BotConfig = get()
        JedisPool(JedisPoolConfig(), botConfig[BotSpec.Jedis.host], botConfig[BotSpec.Jedis.port])
    }
    single(qualifier = named("bucket4j")) {
        val provider = Caching.getCachingProvider(CaffeineCachingProvider::class.java.name)
        val cacheManager = provider.getCacheManager(provider.defaultURI, provider.defaultClassLoader)
        val configuration = CaffeineConfiguration<String, GridBucketState>()
        configuration.setExpiryPolicyFactory { TouchedExpiryPolicy(Duration.FIVE_MINUTES) }
        configuration.maximumSize = OptionalLong.of(500)
        cacheManager.createCache("bucket4j", configuration)
    }
    single {
        val proxy: ProxyManager<String> =
            Bucket4j.extension(JCache::class.java).proxyManagerForCache(get(named("bucket4j")))
        proxy
    }
    single { Bucket4jRateLimiter(get()) as RateLimiter }
    single { RedisReputationService(get()) as ReputationService }
    single { MessageHandlerImpl() as MessageHandler }
    single { ThanksListener(get(), get()) }
    single { CommandListener() }
    single { GetReputationCommand(get()) }
    single { TopReputationCommand(get()) }
}