package com.grosslicht.discord.thanksbot.listener.impl

import com.grosslicht.discord.thanksbot.listener.api.MessageCreatedListener
import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import reactor.core.publisher.Mono

class ThanksListener(private val reputationService: ReputationService) : MessageCreatedListener {
    private val thanksRegex = """(thanks|thank you)""".toRegex(RegexOption.IGNORE_CASE)

    override fun onMessageCreated(ctx: MessageContext): Mono<Void> {
        if (ctx.message.content.map { it.contains(thanksRegex) }.orElse(false)
            && ctx.message.userMentionIds.isNotEmpty()
        ) {
            return ctx.message.userMentions
                .filter { it.isBot }
                .filter { it.id == ctx.member.id }
                .flatMap { it.asMember(ctx.guildId) }
                .collectList()
                .flatMap {
                    reputationService.incrementReputation(ctx.guildId.asString(), it.map { u -> u.id.asString() })
                        .then(Mono.just(it))
                }
                .zipWith(ctx.message.channel)
                .flatMap {
                    it.t2.createMessage { msg ->
                        msg.setContent("+1 for ${it.t1.joinToString(", ") { u -> "**${u.displayName}**" }}")
                    }
                }
                .then()
        }
        return Mono.empty()
    }
}