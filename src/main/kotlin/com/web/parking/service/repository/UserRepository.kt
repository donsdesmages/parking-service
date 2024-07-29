package com.web.parking.service.repository

import com.web.parking.service.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.telegramUserId = :telegramUserId")
    fun findStateByTelegramUserId(telegramUserId: Long): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u WHERE u.telegramUserId = :telegramUserId")
    fun findUserTelegramId(telegramUserId: Long): Optional<UserEntity>
}
