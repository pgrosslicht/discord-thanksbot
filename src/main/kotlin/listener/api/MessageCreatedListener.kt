package com.grosslicht.discord.thanksbot.listener.api

import com.grosslicht.discord.thanksbot.service.api.MessageContext
import reactor.core.publisher.Mono

interface MessageCreatedListener {
    fun onMessageCreated(ctx: MessageContext): Mono<Void>
}