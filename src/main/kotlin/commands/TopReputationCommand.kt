package com.grosslicht.discord.thanksbot.commands

import com.grosslicht.discord.thanksbot.service.api.MessageContext
import com.grosslicht.discord.thanksbot.service.api.ReputationContext
import com.grosslicht.discord.thanksbot.service.api.ReputationService
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.util.Snowflake
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import kotlin.math.roundToLong

class TopReputationCommand(private val reputationService: ReputationService) : BaseCommand {
    override fun canHandle(ctx: MessageContext): Boolean =
        ctx.message.content.map { it.startsWith("?toprep") }.orElse(false)

    override fun handle(ctx: MessageContext): Mono<Void> {
        return reputationService.topReputation(ctx.guildId.asString())
            .flatMapSequential { rep ->
                ctx.guild.flatMap { it.getMemberById(Snowflake.of(rep.memberId)) }
                    .zipWith(Mono.just(rep))
            }
            .collectList()
            .flatMap { list ->
                ctx.message.channel.flatMap { formatList(it, list) }
            }
            .then()
    }

    private fun formatList(channel: MessageChannel, list: List<Tuple2<Member, ReputationContext>>): Mono<Message> {
        if (list.isEmpty()) {
            return channel.createMessage { it.setContent("No thanks recorded yet!") }
        }
        return channel.createEmbed { embed ->
            embed.setTitle("Top Reputation")
            val str = StringBuilder()
            list.forEachIndexed { index, t ->
                str.appendln("${index.inc()}. **${t.t1.displayName}** - **${t.t2.score.roundToLong()}** rep")
            }
            embed.setDescription(str.toString())
        }
    }

}