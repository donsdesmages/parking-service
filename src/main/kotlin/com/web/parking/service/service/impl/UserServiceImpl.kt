package com.web.parking.service.service.impl

import com.web.parking.service.entity.CarEntity
import com.web.parking.service.entity.UserEntity
import com.web.parking.service.repository.UserRepository
import com.web.parking.service.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun createUser(telegramUserId: Long,
                            userName: String,
                            carNumber: String,
                            regionNumber: String) {

        val carEntity = CarEntity(null, carNumber)
        val userEntity = UserEntity(
            null,
            userName,
            telegramUserId,
            carEntity
        )

        userRepository.save(userEntity)
    }
}