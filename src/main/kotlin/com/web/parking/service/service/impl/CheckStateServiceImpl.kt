package com.web.parking.service.service.impl

import com.web.parking.service.cache.State
import com.web.parking.service.repository.UserRepository
import com.web.parking.service.service.CheckStateService
import org.springframework.stereotype.Service

@Service
class CheckStateServiceImpl(
    private val userRepository: UserRepository
) : CheckStateService {
    override fun checkState(telegramUserId: Long, state: String): Boolean {
        val userState = userRepository.findStateByTelegramUserId(telegramUserId).orElse(null)
        return userState?.let { State.valueOf(it.state).toString() == state } ?: false
    }
}