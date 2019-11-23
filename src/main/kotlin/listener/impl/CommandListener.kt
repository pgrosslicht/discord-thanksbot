package com.grosslicht.discord.thanksbot.listener.impl

import com.grosslicht.discord.thanksbot.commands.BaseCommand
import com.grosslicht.discord.thanksbot.listener.api.MessageCreatedListener
import com.grosslicht.discord.thanksbot.service.api.MessageContext
import reactor.core.publisher.Mono

class CommandListener : MessageCreatedListener {
    private val commands: MutableList<BaseCommand> = mutableListOf()

    override fun onMessageCreated(ctx: MessageContext): Mono<Void> {
        return commands.firstOrNull { it.canHandle(ctx) }?.handle(ctx) ?: Mono.empty()
    }

    fun registerCommand(command: BaseCommand) {
        commands.add(command)
    }
}