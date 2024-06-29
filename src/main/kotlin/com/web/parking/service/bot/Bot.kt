package com.web.parking.service.bot

import com.web.parking.service.service.impl.UserServiceImpl
import com.web.parking.service.service.impl.ValidateCarServiceImpl
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class Bot(
    private val botConfig: BotConfig,
    private val userServiceImpl: UserServiceImpl,
    private val validateUser: ValidateCarServiceImpl,
) : TelegramLongPollingBot(botConfig.botToken) {
    private val log = logger {}

    override fun onUpdateReceived(update: Update?) {
        if (validateUser.validateMessage(update)) {
            userServiceImpl.processUpdate(update)
        }
    }

    override fun getBotUsername(): String {
        return botConfig.botName
    }
}