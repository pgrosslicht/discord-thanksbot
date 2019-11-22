package com.grosslicht.discord.thanksbot.di

import com.grosslicht.discord.thanksbot.config.BotConfig
import com.grosslicht.discord.thanksbot.config.BotSpec
import com.grosslicht.discord.thanksbot.service.api.MessageHandler
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import com.grosslicht.discord.thanksbot.service.impl.MessageHandlerImpl
import com.grosslicht.discord.thanksbot.service.impl.RedisReputationService
import com.uchuhimo.konf.Config
import org.koin.dsl.module
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

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
    single { RedisReputationService(get()) as ReputationService }
    single { MessageHandlerImpl(get()) as MessageHandler }
}