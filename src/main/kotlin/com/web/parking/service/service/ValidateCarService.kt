package com.web.parking.service.service

interface ValidateCarService {
    fun validateCarNumber(chatId: Long, carNumber: String): Boolean
    fun allowedRegionRepository(carRegion: String): Boolean
}