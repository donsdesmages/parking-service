package com.web.parking.service.bot

import com.web.parking.service.service.impl.UserServiceImpl
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class Bot(
    private val botConfig: BotConfig,
    private val userServiceImpl: UserServiceImpl,
) : TelegramLongPollingBot(botConfig.botToken) {

    override fun onUpdateReceived(update: Update?) {
        if (userServiceImpl.validateMessage(update)) {
            userServiceImpl.processUpdate(update)
        }
    }

    override fun getBotUsername(): String {
        return botConfig.botName
    }
}