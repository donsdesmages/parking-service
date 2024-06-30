package com.web.parking.service.service

import com.web.parking.service.util.RegistrationProcessParams

interface UserService {
    fun updateUserStateOrCreateUser(telegramUserId: Long,
                                    state: String,
                                    userName: String,
                                    carNumber: String
    )
    fun initialStart(params: RegistrationProcessParams, state: String)
}