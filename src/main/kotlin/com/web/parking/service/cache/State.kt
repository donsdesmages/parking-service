package com.web.parking.service.cache

enum class State(description: String) {
    NO_REGISTERED("NO_REGISTERED"),
    REGISTERED("REGISTERED"),
    REGISTRATION_PROCESS("REGISTRATION_PROCESS"),
    CHANGE_DATA_PROCESS("CHANGE_DATA_PROCESS")

}