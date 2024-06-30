package com.web.parking.service.service.impl

import com.web.parking.service.messenger.MessageService
import com.web.parking.service.service.СommandService
import org.springframework.stereotype.Service

@Service
class СommandServiceImpl(
    private val messageService: MessageService,
): СommandService {

    /**
     * todo: добавить проверку data callbackQuery.
     *  если пользоветль нажмет регистраиця, предложить зарегаться, но только с новым номером авто
     *  если перекрыл выезд, то соответсвующие проверки
     */
    override fun checkingHeadMenuCommand(data: String, chatIdInlineCallback: Long) {
        if (data == "head_menu") messageService.selectionOption(chatIdInlineCallback)
    }
}