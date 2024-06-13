package com.web.parking.service.bot

import com.web.parking.service.service.impl.UserServiceImpl
import com.web.parking.service.service.impl.ValidateUserServiceImpl
import com.web.parking.service.util.Button
import com.web.parking.service.util.CommandConstant.Companion.REGISTRATION
import com.web.parking.service.util.CommandConstant.Companion.START
import com.web.parking.service.util.ReplyMessageСonstant.Companion.GREETING
import com.web.parking.service.util.ReplyMessageСonstant.Companion.REGISTRATION_GUIDE
import com.web.parking.service.util.ReplyMessageСonstant.Companion.SUCCESSFUL
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class TelegramBot(
    private val botConfig: BotConfig,
    private val userServiceImpl: UserServiceImpl,
    private val validateUser: ValidateUserServiceImpl,
) : TelegramLongPollingBot(botConfig.botToken) {
    private val log = logger {}

    override fun onUpdateReceived(update: Update?) {
        if (validateUser.validateMessage(update)) {
            val messageText = update?.message?.text ?: ""
            val chatId = update?.message?.chatId ?: 0
            val userName = update?.message?.chat?.firstName ?: ""
            val telegramUserId = update?.message?.chat?.id ?: 0

            if (validateUser.validateCarNumber(messageText)) {
                userServiceImpl.createUser(
                    telegramUserId,
                    userName,
                    carNumber = messageText, null.toString()).also { log.info { "User was created" } }

                sendMessage(createMessage(chatId, SUCCESSFUL))
            }

            when (messageText) {
                START -> greetingUsers(chatId, userName)
                REGISTRATION -> sendMessage(createMessage(chatId, REGISTRATION_GUIDE))
            }
        }
    }

    private fun createMessage(chatId: Long, text: String): SendMessage {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = text

        return message
    }

    private fun greetingUsers(chatId: Long, name: String) {
        val message = createMessage(chatId,"Приветствуем вас, $name!" + GREETING)
        val button = Button()
        button.buttonRegistration(message)

        sendMessage(message).also { log.info { "Message has been sended" } }
    }

    private fun sendMessage(message: SendMessage) =
        execute(message)
            .runCatching {}
            .onSuccess { log.info { "Success" } }
            .onFailure { log.error { "Non success" } }

    override fun getBotUsername(): String {
        return botConfig.botName
    }
}