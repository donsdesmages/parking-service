package com.web.parking.service.repository

import com.web.parking.service.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>
