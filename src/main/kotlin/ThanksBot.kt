package com.grosslicht.discord.thanksbot

import com.grosslicht.discord.thanksbot.config.BotConfig
import com.grosslicht.discord.thanksbot.config.BotSpec
import com.grosslicht.discord.thanksbot.di.KoinLogger
import com.grosslicht.discord.thanksbot.di.botModule
import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.MessageHandler
import com.grosslicht.discord.thanksbot.util.toNullable
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.core.logger.Level

private val logger = KotlinLogging.logger {}

fun main() {
    startKoin {
        logger(KoinLogger(Level.DEBUG))
        modules(botModule)
    }

    ThanksBot().start()
}

class ThanksBot : KoinComponent {
    private val config by inject<BotConfig>()
    private val messageHandler by inject<MessageHandler>()

    fun start() {
        val client = DiscordClientBuilder(config[BotSpec.token]).build()

        client.eventDispatcher.on(ReadyEvent::class.java)
            .subscribe { ready -> logger.info { "Logged in as ${ready.self}" } }
        client.eventDispatcher.on(MessageCreateEvent::class.java)
            .map { MessageContext(it.message, it.member.toNullable(), it.guildId.toNullable(), it.guild) }
            .flatMap { messageHandler.handle(it) }
            .subscribe()

        client.login().block()
    }
}