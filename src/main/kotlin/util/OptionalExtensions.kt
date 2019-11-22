package com.grosslicht.discord.thanksbot.util

import java.util.*

fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)