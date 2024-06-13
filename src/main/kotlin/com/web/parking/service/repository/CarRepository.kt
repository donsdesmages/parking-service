package com.web.parking.service.repository

import com.web.parking.service.entity.CarEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CarRepository : JpaRepository<CarEntity, Long>
