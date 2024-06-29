package com.web.parking.service.service

import org.telegram.telegrambots.meta.api.objects.Update

interface ValidateCarService {
    fun validateMessage(update: Update? ): Boolean
    fun validateCarNumber(chatId: Long, carNumber: String): Boolean
    fun allowedRegionRepository(carRegion: String): Boolean
}