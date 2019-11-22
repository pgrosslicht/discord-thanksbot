package com.grosslicht.discord.thanksbot.service.api

import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.util.Snowflake
import reactor.core.publisher.Mono

data class MessageContext(val message: Message, val member: Member?, val guildId: Snowflake?, val guild: Mono<Guild>)

interface MessageHandler {
    fun handle(ctx: MessageContext): Mono<Void>
}