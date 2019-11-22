package com.grosslicht.discord.thanksbot.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec

object BotSpec : ConfigSpec("bot") {
    val token by required<String>()

    object Jedis : ConfigSpec("jedis") {
        val host by optional("localhost")
        val port by optional(6379)
    }
}

typealias BotConfig = Config