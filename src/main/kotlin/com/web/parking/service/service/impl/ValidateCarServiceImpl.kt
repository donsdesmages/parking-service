package com.web.parking.service.service.impl

import com.web.parking.service.messenger.MessageService
import com.web.parking.service.repository.CarRepository
import com.web.parking.service.repository.RegionsRepository
import com.web.parking.service.repository.UserRepository
import com.web.parking.service.service.ValidateCarService
import com.web.parking.service.util.RegexConstant.Companion.REGULAR_EXPRESSION
import com.web.parking.service.util.ReplyMessageСonstant
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class ValidateCarServiceImpl(
    private val regionsRepository: RegionsRepository,
    private val carRepository: CarRepository,
    @Lazy private val messageService: MessageService
) : ValidateCarService {

    override fun validateMessage(update: Update?): Boolean =
        update?.hasMessage() == true && update.message?.hasText() == true

    override fun validateCarNumber(chatId: Long, carNumber: String): Boolean {
        val checkCarNumberInRepository = checkCarNumberInRepository(carNumber)
        if (checkCarNumberInRepository) {
            if (carNumber.length > 6) {
                val fullCarNumber = carNumber.uppercase()
                val carNum = fullCarNumber.substring(0, 6)
                val carRegion = fullCarNumber.substring(6)

                val checkResult = (Regex(REGULAR_EXPRESSION)
                    .matches(carNum)
                        && allowedRegionRepository(carRegion))

                if (!checkResult) {
                    messageService.sendMessage(
                        messageService.createMessage(chatId,
                            ReplyMessageСonstant.FAIL
                        ))
                }

                return checkResult;
            }
            messageService.sendMessage(
                messageService.createMessage(chatId,
                    ReplyMessageСonstant.FAIL
                ))
        }
        else {
            messageService.sendMessage(
                messageService.createMessage(chatId, ReplyMessageСonstant.ALREADY_REGISTERED
                ))
        }

        return false
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

    fun checkCarNumberInRepository(carNumber: String): Boolean {
        val checkCarNumber = carRepository.findAll()

        for (numberCar in checkCarNumber) {
            if (numberCar.carNumber.trim() == carNumber) {
                return true
            }
        }
        return false
    }
}