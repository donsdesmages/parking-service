package com.web.parking.service.service.impl

import com.web.parking.service.cache.State
import com.web.parking.service.entity.CarEntity
import com.web.parking.service.entity.UserEntity
import com.web.parking.service.messenger.MessageService
import com.web.parking.service.repository.UserRepository
import com.web.parking.service.service.UserService
import com.web.parking.service.util.CommandConstant.Companion.REGISTRATION
import com.web.parking.service.util.CommandConstant.Companion.START
import com.web.parking.service.util.param.RegistrationProcessParam
import com.web.parking.service.util.ReplyMessageСonstant
import com.web.parking.service.util.ReplyMessageСonstant.Companion.COMMAND_NOT_FOUND
import com.web.parking.service.util.param.ChangeProcessParam
import mu.KotlinLogging
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val validateCarServiceImpl: ValidateCarServiceImpl,
    private val checkStateServiceImpl: CheckStateServiceImpl,
    @Lazy private val commandServiceImpl: СommandServiceImpl,
    @Lazy private val messageService: MessageService
) : UserService {
    private val log = KotlinLogging.logger {}
    private val registeredState = State.valueOf("REGISTERED").toString()
    private val registrationProcess = State.valueOf("REGISTRATION_PROCESS").toString()


    fun validateMessage(update: Update?): Boolean {
        return when {
            update?.hasMessage() == true && update.message?.hasText() == true -> true
            update?.callbackQuery != null -> true
            else -> false
        }
    }

    fun createUser(telegramUserId: Long,
                   state: String,
                   userName: String,
                   carNumber: String,
                   regionNumber: String
    )
    {
        val carEntity = CarEntity(null, carNumber)
        val userEntity = UserEntity(
            null,
            state,
            userName,
            telegramUserId,
            carEntity)

        userRepository.save(userEntity)
    }

    override fun initialStart(params: RegistrationProcessParam, state: String) {
        val checkStateInRepository = userRepository.findStateByTelegramUserId(params.telegramUserId)
        if (checkStateInRepository.toString() != state) {
            updateUserStateOrCreateUser(params.telegramUserId, state, params.userName, params.messageText)
        } else {
            log.error { "Текущее состояние ${state}, " +
                    "уже было назначено данному пользователю" +
                    params.userName
            }
        }
    }

    override fun updateUserStateOrCreateUser(telegramUserId: Long,
                                             state: String,
                                             userName: String,
                                             carNumber: String
    ) {
        val checkState = checkStateServiceImpl.checkState(telegramUserId, registeredState)
        if (!checkState) {
            val userOptional = userRepository.findStateByTelegramUserId(telegramUserId)
            if (userOptional.isPresent) {
                val updateUser = userOptional.get()
                updateUser.car.carNumber = carNumber
                updateUser.state = state

                userRepository.save(updateUser).also { log.info { "User state updated at: $state" } }
            }
            else {
                createUser(
                    telegramUserId,
                    state,
                    userName,
                    carNumber,
                    regionNumber = ""
                )
                log.info { "New user created" }
            }
        }
    }

    fun startRegistrationProcess(params: RegistrationProcessParam): Boolean {
        updateUserStateOrCreateUser(params.telegramUserId, registrationProcess, params.userName, params.messageText)
            .runCatching {}
            .onSuccess { log.info {"User was created"} }
            .onFailure { log.error {  "User No created"} }

        val checkState = checkStateServiceImpl.checkState(params.telegramUserId, registrationProcess)
        if (checkState) {
            if (params.messageText != REGISTRATION) {
                if (validateCarServiceImpl.validateCarNumber(params.chatId, params.messageText)) {
                    updateUserStateOrCreateUser(params.telegramUserId, registeredState, params.userName,
                        params.messageText
                    )
                    messageService.manualForUser(params.chatId)
                    return true
                }
            }
        }
        return false
    }

    fun processUpdate(update: Update?) {
        val messageText = update?.message?.text ?: ""
        val chatId = update?.message?.chatId ?: 0
        val userName = update?.message?.chat?.firstName ?: ""
        val telegramUserId = update?.message?.chat?.id ?: 0

        val callbackQuery = update?.callbackQuery
        val data = callbackQuery?.data
        val chatIdInlineCallback = callbackQuery?.message?.chatId ?: 0
        val telegramUserIdCallback = callbackQuery?.message?.chat?.id ?: 0
        val userNameCallback = callbackQuery?.message?.chat?.firstName ?: ""
        val messageTextCallback = callbackQuery?.message?.text ?: ""

        val params = RegistrationProcessParam(
            chatId,
            telegramUserId,
            userName,
            messageText
        )

        val paramsCallbackQuery = ChangeProcessParam(
            chatIdInlineCallback,
            telegramUserIdCallback,
            userNameCallback,
            messageTextCallback
        )

        data?.let { commandServiceImpl.checkingHeadMenuCommand(it, chatIdInlineCallback) }
        data?.let { commandServiceImpl.checkingUpdataCarDataCommand(it, chatIdInlineCallback, paramsCallbackQuery) }
        data?.let { commandServiceImpl }

        if(messageText == START) {
            handleUserRegistrationOrGreeting(params)
                .also { log.info { "User calling parking-service-bot with command /start" } }
        }

        if (messageText == REGISTRATION) { checkAndStartRegistration(params) }
        val userState = userRepository.findStateByTelegramUserId(telegramUserId)

        when (State.valueOf(userState.get().state)) {
            State.NO_REGISTERED -> { handleStartCommand(params) }
            State.REGISTRATION_PROCESS -> { startRegistrationProcess(params) }
            State.REGISTERED -> { messageService.greetingRegisteredUser(chatId, userName) }
            State.CHANGE_DATA_PROCESS -> { changeUserData(chatId, userName, messageText) }
            else -> {
                messageService.sendMessage(
                    messageService.createMessage(chatId, ReplyMessageСonstant.MESSAGE_START
                    ))
            }
        }
    }

    fun handleUserRegistrationOrGreeting(params: RegistrationProcessParam) {
        val state = State.valueOf("NO_REGISTERED").toString()
        val validateUser = validateUser(params.telegramUserId)
        if (validateUser) updateUserStateOrCreateUser(params.telegramUserId,
            state,
            params.userName,
            params.messageText
        )
        else {
            val checkState = checkStateServiceImpl.checkState(params.telegramUserId, state)
            if (!checkState) {
                messageService.sendMessage(messageService.createMessage(
                        params.chatId, "Приветствуем вас, " +
                            params.userName + "!"
                    ))
            }
        }
    }

    fun checkAndStartRegistration(params: RegistrationProcessParam) {
        val checkState = checkStateServiceImpl.checkState(params.telegramUserId, registrationProcess)
        val checkRegisteredState = checkStateServiceImpl.checkState(params.telegramUserId, registeredState)
        if (!checkState && !checkRegisteredState) {
            messageService.sendMessage(messageService.createMessage(
                params.chatId, ReplyMessageСonstant.REGISTRATION_MANUAL
            ))
            initialStart(params, registrationProcess)
        }
    }

    fun handleStartCommand(params: RegistrationProcessParam) {
        if (params.messageText == START) messageService.greetingUser(params.chatId, params.userName)
        else messageService.sendMessage(messageService.createMessage(params.chatId, COMMAND_NOT_FOUND))
    }

    fun validateUser(telegramUserId: Long): Boolean {
        val checkTelegramIdInDataBase = userRepository.findUserTelegramId(telegramUserId)
            .orElse(null)
            ?.telegramUserId ?: ""

        return checkTelegramIdInDataBase != telegramUserId
    }

    fun changeUserData(chatId: Long, userName: String, carNumber: String) {
        val validate = validateCarServiceImpl.validateCarNumber(chatId, carNumber)
        if (validate) {
            updateUserStateOrCreateUser(
                chatId,
                registeredState,
                userName,
                carNumber
            )
                .runCatching { }
                .onSuccess { log.info { "User data has been successfully changed" } }
                .onFailure { log.info { "An error occurred while changing data" } }
                .also { messageService.changeCarDataSuccessfully(chatId) }
        }
    }

    fun updateUserState(telegramUserId: Long, state: String) {
        val userOptional = userRepository.findStateByTelegramUserId(telegramUserId)
        if (userOptional.isPresent) {
            val updateUser = userOptional.get()
            updateUser.state = state

            userRepository.save(updateUser).also { log.info { "User state updated at: $state" } }
        }
    }
}
