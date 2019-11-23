package com.grosslicht.discord.thanksbot.util

fun String.pluralize(amount: Number, plural: String? = null): String {
    return if (amount == 1) this else plural ?: "${this}s"
}