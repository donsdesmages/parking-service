package com.web.parking.service.repository

import com.web.parking.service.entity.CarEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository : JpaRepository<CarEntity, Long> {
    fun findByCarNumber(carNumber: String): CarEntity?
}
