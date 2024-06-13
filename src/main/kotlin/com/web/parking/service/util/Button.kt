package com.web.parking.service.util

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow


class Button {
    fun buttonRegistration(message: SendMessage) {
        val keyboardMarkup = ReplyKeyboardMarkup()
        val keyboardRow = KeyboardRow()
        val registrationButton = KeyboardButton("Регистрация")

        keyboardRow.add(registrationButton)
        val keyboard = listOf(keyboardRow)
        keyboardMarkup.keyboard = keyboard
        keyboardMarkup.resizeKeyboard = true
        keyboardMarkup.oneTimeKeyboard = true
        keyboardMarkup.selective = true

        message.replyMarkup = keyboardMarkup
    }
}