package com.web.parking.service.bot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("bot")
data class BotConfig(
    val botName: String,
    val botToken: String
)
