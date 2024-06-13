package com.web.parking.service.repository

import com.web.parking.service.entity.RegionCarEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionsRepository: JpaRepository<RegionCarEntity, Long>
