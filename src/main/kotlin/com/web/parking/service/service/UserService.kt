package com.web.parking.service.service

import com.web.parking.service.util.param.RegistrationProcessParam

interface UserService {
    fun updateUserStateOrCreateUser(telegramUserId: Long,
                                    state: String,
                                    userName: String,
                                    carNumber: String
    )
    fun initialStart(params: RegistrationProcessParam, state: String)
}