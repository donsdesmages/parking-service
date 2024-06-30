package com.web.parking.service.model

data class User(
    private val chatId: Long,
    private val stateUser: String,
    private val telegramUserId: Long,
    private val userName: String
)
