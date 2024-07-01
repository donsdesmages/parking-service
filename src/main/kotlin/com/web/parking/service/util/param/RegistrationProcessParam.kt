package com.web.parking.service.util.param

data class RegistrationProcessParam(
    val chatId: Long,
    val telegramUserId: Long,
    val userName: String,
    val messageText: String
)
