package com.web.parking.service.service

interface UserService {
    fun updateUserStateOrCreateUser(telegramUserId : Long, state: String, userName : String, carNumber : String)
    fun initialStart(chatId: Long, state: String, userName: String, telegramUserId: Long, messageText: String)
}