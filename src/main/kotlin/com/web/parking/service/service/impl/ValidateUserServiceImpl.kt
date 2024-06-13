package com.web.parking.service.service.impl

import com.web.parking.service.repository.RegionsRepository
import com.web.parking.service.service.ValidateUserService
import com.web.parking.service.util.RegexConstant.Companion.REGULAR_EXPRESSION
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class ValidateUserServiceImpl(
    private val regionsRepository: RegionsRepository
) : ValidateUserService {
    private val log = KotlinLogging.logger {}

    override fun validateMessage(update: Update?): Boolean =
        update?.hasMessage() == true && update.message?.hasText() == true

    override fun validateCarNumber(carNumber: String,): Boolean {
        val fullCarNumber = carNumber.uppercase()
        val carNumber = fullCarNumber.substring(0, 6)
        val carRegion = fullCarNumber.substring(6)

        val checkResult = (Regex(REGULAR_EXPRESSION).matches(carNumber)
                && allowedRegionRepository(carRegion))

        return checkResult;
    }

    override fun allowedRegionRepository(carRegion: String): Boolean {
        val allowedRegionsRepository = regionsRepository.findAll()

        for (region in allowedRegionsRepository) {
            if (region.regionNumber.trim() == carRegion.trim()) {
                return true
            }
        }
        return false
    }
}