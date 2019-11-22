package com.grosslicht.discord.thanksbot.service.impl

import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.MessageHandler
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import reactor.core.publisher.Mono

class MessageHandlerImpl(private val reputationService: ReputationService) : MessageHandler {
    private val thanksRegex = """(thanks|thank you)""".toRegex(RegexOption.IGNORE_CASE)
    override fun handle(ctx: MessageContext): Mono<Void> {
        if (ctx.guildId == null || ctx.member == null || ctx.member.isBot)
            return Mono.empty()
        if (ctx.message.content.map { it.contains(thanksRegex) }.orElse(false)
            && ctx.message.userMentionIds.isNotEmpty()
        ) {
            return ctx.message.userMentions
                .collectList()
                .flatMap {
                    reputationService.incrementReputation(ctx.guildId.asString(), it.map { u -> u.id.asString() })
                        .then(Mono.just(it))
                }
                .zipWith(ctx.message.channel)
                .flatMap {
                    it.t2.createMessage { msg ->
                        msg.setContent("+1 for ${it.t1.joinToString(", ") { u -> "**${u.username}**" }}")
                    }
                }
                .then()
        }
        return Mono.empty()
    }
}