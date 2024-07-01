package com.web.parking.service.service.impl

import com.web.parking.service.cache.State
import com.web.parking.service.messenger.MessageService
import com.web.parking.service.service.СommandService
import com.web.parking.service.util.param.ChangeProcessParam
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class СommandServiceImpl(
    private val messageService: MessageService,
    private val userServiceImpl: UserServiceImpl,
    private val carServiceImpl: ValidateCarServiceImpl
): СommandService
{
    private val changeDataProcess = State.valueOf("CHANGE_DATA_PROCESS").toString()
    private val log = KotlinLogging.logger {}


    override fun checkingHeadMenuCommand(data: String, chatIdInlineCallback: Long) {
        if (data == "head_menu") messageService.selectionOption(chatIdInlineCallback)
    }

    override fun checkingUpdataCarDataCommand(
        data: String,
        chatIdInlineCallback:
        Long, params: ChangeProcessParam
    )
    {
        if (data == "change") {
            messageService.changeCarDataUser(chatIdInlineCallback)
            userServiceImpl.updateUserState(
                params.chatIdInlineCallback,
                changeDataProcess,
            )
        }
    }

    fun checkingBlockedExitCommand(
        data: String,
        chatIdInlineCallback:
        Long, params: ChangeProcessParam
    )
    {
        if (data == "blocked_exit") {

        }
    }
}