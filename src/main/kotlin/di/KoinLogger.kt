package com.grosslicht.discord.thanksbot.di

import mu.KotlinLogging
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE


private val logger = KotlinLogging.logger {}

class KoinLogger(level: Level) : Logger(level) {
    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.debug { msg }
            Level.INFO -> logger.info { msg }
            Level.ERROR -> logger.error { msg }
        }
    }
}