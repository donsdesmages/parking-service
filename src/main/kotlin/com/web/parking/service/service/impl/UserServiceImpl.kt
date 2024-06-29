package com.web.parking.service.service.impl

import com.web.parking.service.cache.State
import com.web.parking.service.entity.CarEntity
import com.web.parking.service.entity.UserEntity
import com.web.parking.service.messenger.MessageService
import com.web.parking.service.repository.UserRepository
import com.web.parking.service.service.UserService
import com.web.parking.service.util.Button
import com.web.parking.service.util.CommandConstant.Companion.REGISTRATION
import com.web.parking.service.util.CommandConstant.Companion.START
import com.web.parking.service.util.ReplyMessageСonstant
import mu.KotlinLogging
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val validateCarServiceImpl: ValidateCarServiceImpl,
    private val checkStateServiceImpl: CheckStateServiceImpl,
    @Lazy private val messageService: MessageService
) : UserService {
    private val log = KotlinLogging.logger {}

    fun createUser(telegramUserId: Long,
                   state: String,
                   userName: String,
                   carNumber: String,
                   regionNumber: String) {

        val carEntity = CarEntity(null, carNumber)
        val userEntity = UserEntity(
            null,
            state,
            userName,
            telegramUserId,
            carEntity)

        userRepository.save(userEntity)
    }

    override fun updateUserStateOrCreateUser(telegramUserId: Long, state: String, userName: String, carNumber: String) {
        val userOptional = userRepository.findStateByTelegramUserId(telegramUserId)
        if (userOptional.isPresent) {
            val updateUser = userOptional.get()
            updateUser.car.carNumber = carNumber
            updateUser.state = state

            userRepository.save(updateUser).also { log.info { "User state updated at: ${state}" } }
        } else {
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

    override fun initialStart(chatId: Long, state: String, userName: String, telegramUserId: Long, messageText: String) {
        val checkStateInRepository = userRepository.findStateByTelegramUserId(telegramUserId)
        if (checkStateInRepository.toString() != state) {
            updateUserStateOrCreateUser(telegramUserId, state, userName, messageText)
        } else {
            log.error { "Текущее состояние ${state}, уже было назначено данному пользователю ${userName}" }
        }
    }

    fun startRegistrationProcess(chatId: Long,
                                 telegramUserId: Long,
                                 userName: String,
                                 messageText: String): Boolean  {

        val registrationProcess = State.valueOf("REGISTRATION_PROCESS").toString()
        val registeredState = State.valueOf("REGISTERED").toString()
        updateUserStateOrCreateUser(
            telegramUserId,
            registrationProcess,
            userName,
            messageText
        )
            .runCatching {}
            .onSuccess { log.info {"User was created"} }
            .onFailure { log.error {  "User No created"} }

        val checkState = checkStateServiceImpl.checkState(telegramUserId, registrationProcess)
        if (checkState) {
            if (messageText != REGISTRATION) {
                if (validateCarServiceImpl.validateCarNumber(chatId, messageText)) {
                    updateUserStateOrCreateUser(
                        telegramUserId,
                        registeredState,
                        userName,
                        messageText
                    )
                    messageService.guideForUser(chatId, userName)
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

        if(messageText == START) {
            handleUserRegistrationOrGreeting(telegramUserId, userName, messageText, chatId)
                .also { log.info { "User calling parking-service-bot with command /start" } }
        }
        /**
         * TODO:
         * Пофиксить возможность повторной регитсрации
         * для того же пользовтеля при нажатии кнопки 'Регистрация' для того же номера авто
         */
        if (messageText == REGISTRATION) {
            val noRegisteredState = State.valueOf("REGISTRATION_PROCESS").toString()
            val checkState = checkStateServiceImpl.checkState(telegramUserId, noRegisteredState)
            if (!checkState) {messageService.sendMessage(messageService.createMessage(chatId, ReplyMessageСonstant.REGISTRATION_GUIDE))
                initialStart(chatId, noRegisteredState, userName, telegramUserId, messageText)
            }
        }
        val userState = userRepository.findStateByTelegramUserId(telegramUserId)

        when (State.valueOf(userState.get().state)) {
            State.NO_REGISTERED -> handleStartCommand(chatId, userName, telegramUserId, messageText)
            State.REGISTRATION_PROCESS -> {
                handleRegistrationCommand(chatId, userName, telegramUserId, messageText)
            }
            State.REGISTERED -> { handleRegisteredUser(chatId, userName) }
            else -> {
                handleUnknownCommand(chatId)
            }
        }
    }

    fun handleStartCommand(chatId: Long, userName: String, telegramUserId: Long, messageText: String) {
        if (messageText == START) {
            messageService.greetingUser(chatId, userName)
        } else {
            messageService.sendMessage(
                messageService.createMessage(
                    chatId,
                    "Бот не знает такой команды."
                            + "\n" + "Для взаимодействия, нажмите /start"))
        }
    }
    fun handleRegistrationCommand(chatId: Long,
                                  userName: String,
                                  telegramUserId: Long,
                                  messageText: String) {
        startRegistrationProcess(chatId, telegramUserId, userName, messageText)
    }

    fun handleUnknownCommand(chatId: Long) {
        messageService.sendMessage(
            messageService.createMessage(chatId, ReplyMessageСonstant.MESSAGE_START
        ))
    }

    fun handleRegisteredUser(chatId: Long, userName: String) {
        messageService.greetingRegisteredUser(chatId, userName)
    }

    fun handleUserRegistrationOrGreeting(
        telegramUserId: Long,
        userName: String,
        messageText: String,
        chatId: Long
    ) {
        val state = State.valueOf("NO_REGISTERED").toString()
        val validateUser = validateUser(telegramUserId)

        if (validateUser) {
            updateUserStateOrCreateUser(telegramUserId, state, userName, messageText)
        } else {
            val checkState = checkStateServiceImpl.checkState(telegramUserId, state)
            if (!checkState) {
                messageService.sendMessage(
                    messageService.createMessage(
                        chatId,
                        "Приветствуем вас, $userName" + ReplyMessageСonstant.CHOOSE_AN_ACTON
                    ))
            }
        }
    }

    fun validateUser(telegramUserId: Long): Boolean {
        val checkTelegramIdInDataBase = userRepository.findUserTelegramId(telegramUserId)
            ?.orElse(null)
            ?.telegramUserId ?: ""

        if (checkTelegramIdInDataBase != telegramUserId) {
            return true
        }
        else {
            return false
        }
    }
}