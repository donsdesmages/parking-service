package com.web.parking.service.util

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class Button {
    fun createButton(nameButton: String,
                     message: SendMessage,
                     resize: Boolean,
                     oneTime: Boolean,
                     selective: Boolean
    ) {
        val keyboardMarkup = ReplyKeyboardMarkup()
        val keyboardRow = KeyboardRow()
        val exitBlockedButton = KeyboardButton(nameButton)

        keyboardRow.add(exitBlockedButton)
        val keyboard = listOf(keyboardRow)
        keyboardMarkup.keyboard = keyboard
        keyboardMarkup.resizeKeyboard = resize
        keyboardMarkup.oneTimeKeyboard = oneTime
        keyboardMarkup.selective = selective

        message.replyMarkup = keyboardMarkup
    }

    fun createInlineButton(
        nameButton: String,
        callbackData: String,
        message: SendMessage
    ) {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val inlineKeyboardButton = InlineKeyboardButton()
        inlineKeyboardButton.text = nameButton
        inlineKeyboardButton.callbackData = callbackData

        val keyboardRow = listOf(inlineKeyboardButton)
        val keyboard = listOf(keyboardRow)
        inlineKeyboardMarkup.keyboard = keyboard

        message.replyMarkup = inlineKeyboardMarkup
    }

    fun buttonRegistration(message: SendMessage) {
        createButton( "Регистрация",
            message,
            resize = true,
            oneTime = true,
            selective = true
        )
    }

    fun buttonRegistrationInline(message: SendMessage): List<InlineKeyboardButton> {
        val button = InlineKeyboardButton().apply {
            text = "Регистрация"
            callbackData = "registration"
        }
        return listOf(button)
    }

    fun buttonBlocked(message: SendMessage): List<InlineKeyboardButton> {
        val button = InlineKeyboardButton().apply {
            text = "Перекрыл выезд"
            callbackData = "blocked_exit"
        }
        return listOf(button)
    }
    fun menuButton(message: SendMessage) {
        createInlineButton(
            "Главное Меню",
            "head_menu",
            message
        )
    }
}