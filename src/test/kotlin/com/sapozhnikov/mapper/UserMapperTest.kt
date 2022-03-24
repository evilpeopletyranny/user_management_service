package com.sapozhnikov.mapper

import com.sapozhnikov.model.dao.UserEntity
import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDate
import java.util.*

class UserMapperTest {
    private lateinit var userMapper: UserMapper
    private lateinit var userEntity: UserEntity

    @BeforeEach
    fun beforeEach() {
        userMapper = UserMapper()
        userEntity = UserEntity(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )
    }

    @Test
    fun `from userEntity to user`() {
        val userModel = User(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )
        expectThat(userModel).isEqualTo(userMapper.mapToUserModel(userEntity))
    }

    @Test
    fun `from user to userEntity`() {
        val userModel = User(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )
        expectThat(userMapper.mapToUserEntity(userModel)).isEqualTo(userEntity)
    }

    @Test
    fun `from createUser to userEntity`() {
        val createUser = CreateUser(
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
        )
        expectThat(
            userMapper.mapToUserEntity(
                UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
                createUser,
                LocalDate.now()
            )
        ).isEqualTo(userEntity)
    }

    @Test
    fun `from updateUser to userEntity`() {
        val updateUser = UpdateUser(
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com"
        )
        expectThat(
            userMapper.mapToUserEntity(
                UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
                updateUser,
                LocalDate.now()
            )
        ).isEqualTo(userEntity)
    }
}