package com.grosslicht.discord.thanksbot.commands

import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import com.grosslicht.discord.thanksbot.util.pluralize
import reactor.core.publisher.Mono
import kotlin.math.roundToLong

class GetReputationCommand(private val reputationService: ReputationService) : BaseCommand {
    override fun canHandle(ctx: MessageContext): Boolean =
        ctx.message.content.map { it.startsWith("?rep") }.orElse(false)

    override fun handle(ctx: MessageContext): Mono<Void> {
        val userToLookup =
            if (ctx.message.userMentionIds.isEmpty()) Mono.just(ctx.member)
            else ctx.message.userMentions.next().flatMap { it.asMember(ctx.guildId) }
        return userToLookup
            .flatMap { user ->
                reputationService.getReputation(ctx.guildId.asString(), user.id.asString())
                    .flatMap { rep ->
                        ctx.message.channel
                            .flatMap {
                                it.createMessage { msg ->
                                    msg.setContent(
                                        "**${user.displayName}** has been thanked ${rep.score.roundToLong()} ${"time".pluralize(
                                            rep.score
                                        )}."
                                    )
                                }
                            }
                    }
            }
            .then()
    }
}