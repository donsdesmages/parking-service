package com.web.parking.service.exception

class UserAlreadyRegisteredException(
    messageException: String,
    val userTelegramId: Long) : RuntimeException(messageException) {
}