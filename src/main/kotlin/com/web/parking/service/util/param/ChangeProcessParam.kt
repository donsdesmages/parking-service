package com.web.parking.service.util.param

data class ChangeProcessParam(
    val chatIdInlineCallback: Long,
    val telegramUserIdCallback: Long,
    val userNameCallback: String,
    val messageTextCallback: String
)
