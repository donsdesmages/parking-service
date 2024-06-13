package com.web.parking.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "user", schema = "public")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_name")
    val userName: String,

    @Column(name = "telegram_user_id")
    val telegramUserId: Long,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "car_number")
    val car: CarEntity
)


