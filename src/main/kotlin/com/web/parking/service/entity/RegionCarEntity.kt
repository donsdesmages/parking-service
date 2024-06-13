package com.web.parking.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "region", schema = "public")
data class RegionCarEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "region_number")
    val regionNumber: String
)

