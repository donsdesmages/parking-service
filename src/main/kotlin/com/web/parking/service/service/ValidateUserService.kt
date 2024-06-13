package com.web.parking.service.service

import org.telegram.telegrambots.meta.api.objects.Update

interface ValidateUserService {
    fun validateMessage(update: Update? ): Boolean
    fun validateCarNumber(carNumber: String): Boolean
    fun allowedRegionRepository(carRegion: String): Boolean
}