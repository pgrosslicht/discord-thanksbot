package com.grosslicht.discord.thanksbot.listener.impl

import com.grosslicht.discord.thanksbot.listener.api.MessageCreatedListener
import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import com.grosslicht.discord.thanksbot.service.ratelimit.api.RateLimiter
import reactor.core.publisher.Mono


class ThanksListener(private val reputationService: ReputationService, private val rateLimiter: RateLimiter) :
    MessageCreatedListener {
    private val thanksRegex = """(thanks|thank you)""".toRegex(RegexOption.IGNORE_CASE)
    private val maxUserMentions = 5

    override fun onMessageCreated(ctx: MessageContext): Mono<Void> {
        if (ctx.message.content.map { it.contains(thanksRegex) }.orElse(false)
            && ctx.message.userMentionIds.isNotEmpty()
        ) {
            if (ctx.message.userMentionIds.contains(ctx.member.id)) {
                return sendError(ctx, "Are you trying to be funny? You can't thank yourself.")
            }
            if (ctx.message.userMentionIds.size > maxUserMentions) {
                return sendError(ctx, "Don't be too generous, you can only thank $maxUserMentions at once.")
            }
            if (!rateLimiter.tryConsume("thanks", ctx.member.id)) {
                return sendError(ctx, "Woah, woah, slow down there. You can only thank once per minute.")
            }

            return ctx.message.userMentions
                .filter { !it.isBot }
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

    private fun sendError(ctx: MessageContext, str: String): Mono<Void> {
        return ctx.message.channel.flatMap {
            it.createMessage { msg -> msg.setContent(str) }
        }.then()
    }
}