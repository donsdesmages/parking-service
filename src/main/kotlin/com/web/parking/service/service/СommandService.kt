package com.web.parking.service.service

import com.web.parking.service.util.param.ChangeProcessParam

public interface СommandService {
    fun checkingHeadMenuCommand(data: String, chatIdInlineCallback: Long)

    fun checkingUpdataCarDataCommand(data: String, chatIdInlineCallback: Long, params: ChangeProcessParam)
}