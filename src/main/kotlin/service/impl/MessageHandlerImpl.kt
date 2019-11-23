package com.grosslicht.discord.thanksbot.service.impl

import com.grosslicht.discord.thanksbot.listener.api.MessageCreatedListener
import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.MessageHandler
import reactor.core.publisher.Mono

class MessageHandlerImpl() : MessageHandler {
    private val messageCreatedListeners: MutableList<MessageCreatedListener> = mutableListOf()

    override fun onMessageCreated(ctx: MessageContext): Mono<Void> {
        if (ctx.member.isBot)
            return Mono.empty()
        return Mono.zipDelayError(messageCreatedListeners.map { it.onMessageCreated(ctx) }) { null }
    }

    override fun addMessageCreatedListener(listener: MessageCreatedListener) {
        messageCreatedListeners.add(listener)
    }
}