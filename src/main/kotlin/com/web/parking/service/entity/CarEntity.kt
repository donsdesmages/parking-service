package com.web.parking.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "car", schema = "public")
data class CarEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "car_number")
    val carNumber: String,
)