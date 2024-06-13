package com.web.parking.service.service

interface UserService {
    fun createUser(telegramUserId : Long, userName : String, carNumber : String, regionNumber: String)
}