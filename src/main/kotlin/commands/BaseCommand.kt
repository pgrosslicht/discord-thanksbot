package com.grosslicht.discord.thanksbot.commands

import com.grosslicht.discord.thanksbot.service.api.MessageContext
import reactor.core.publisher.Mono

interface BaseCommand {
    fun canHandle(ctx: MessageContext): Boolean = false

    fun handle(ctx: MessageContext): Mono<Void>
}