package com.grosslicht.discord.thanksbot.service.ratelimit.api

import discord4j.core.`object`.util.Snowflake

interface RateLimiter {
    fun tryConsume(command: String, user: Snowflake): Boolean
}