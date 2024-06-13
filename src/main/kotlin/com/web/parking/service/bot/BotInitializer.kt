package com.web.parking.service.bot

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class BotInitializer {
    @Bean
    fun telegramsBotApi(bot: TelegramBot) =
        TelegramBotsApi(DefaultBotSession::class.java).apply { registerBot(bot) }
}