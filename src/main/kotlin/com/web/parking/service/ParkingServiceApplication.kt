package com.web.parking.service

import com.web.parking.service.bot.BotConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BotConfig::class)
class ParkingServiceApplication

fun main(args: Array<String>) {
	runApplication<ParkingServiceApplication>(*args)
}
