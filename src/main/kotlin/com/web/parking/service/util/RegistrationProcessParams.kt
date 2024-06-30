package com.web.parking.service.util

data class RegistrationProcessParams(
    val chatId: Long,
    val telegramUserId: Long,
    val userName: String,
    val messageText: String
)
