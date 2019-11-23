package com.grosslicht.discord.thanksbot.service.ratelimit.impl

import com.grosslicht.discord.thanksbot.service.ratelimit.api.RateLimiter
import discord4j.core.`object`.util.Snowflake
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.grid.ProxyManager
import java.time.Duration

class Bucket4jRateLimiter(private val proxyManager: ProxyManager<String>) : RateLimiter {
    private val rateLimit = Bucket4j.configurationBuilder()
        .addLimit(Bandwidth.simple(1, Duration.ofMinutes(1)))
        .build()

    override fun tryConsume(command: String, user: Snowflake): Boolean {
        return proxyManager.getProxy("$command-${user.asString()}", rateLimit).tryConsume(1)
    }
}