package com.web.parking.service.messenger

import com.web.parking.service.bot.Bot
import com.web.parking.service.util.Button
import com.web.parking.service.util.ReplyMessageСonstant
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Service
class MessageService(
    private val telegramBot: Bot
) {
    private val log = KotlinLogging.logger {}

    fun greetingUser(chatId: Long, name: String) {
        val message = createMessage(chatId, "Приветствуем вас, $name!${ReplyMessageСonstant.GREETING}")
        val button = Button()
        button.buttonRegistration(message)

        sendMessage(message).also { log.info { "Message has been sending" } }
    }

    fun greetingRegisteredUser(chatId: Long, name: String) {
        val message = createMessage(chatId, ReplyMessageСonstant.CALLBACK_MENU)
        val button = Button()
        button.menuButton(message)

        sendMessage(message).also { log.info { "Message has been sending to users" } }
    }

    fun guideForUser(chatId: Long) {
        val message = createMessage(chatId, ReplyMessageСonstant.SUCCESSFUL)
        val button = Button()
        button.menuButton(message)

        sendMessage(message).also { log.info { "Message has been sending to users" } }
    }

    fun selectionOption(chatId: Long) {
        val message = createMessage(chatId, ReplyMessageСonstant.SELECTION_OPTION)
        val button = Button()
        val inlineKeyboardButtons = mutableListOf<List<InlineKeyboardButton>>()

        inlineKeyboardButtons.add(button.buttonBlocked(message))
        inlineKeyboardButtons.add(button.buttonRegistrationInline(message))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = inlineKeyboardButtons

        message.replyMarkup = inlineKeyboardMarkup

        sendMessage(message).also { log.info { "Message has been sending to users" } }
    }

    fun createMessage(chatId: Long, text: String): SendMessage {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = text

        return message
    }

    fun sendMessage(message: SendMessage) =
        telegramBot.execute(message)
            .runCatching {}
            .onSuccess { log.info { "Success" } }
            .onFailure { log.error { "Non success" } }
}