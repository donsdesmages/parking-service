package com.web.parking.service.service

interface CheckStateService {
    fun checkState(telegramUserId: Long, state: String): Boolean
}