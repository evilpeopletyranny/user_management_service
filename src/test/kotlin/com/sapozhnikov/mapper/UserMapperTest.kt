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
    private lateinit var user: User

    @BeforeEach
    fun beforeEach() {
        userMapper = UserMapper()
        user = User(
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
    fun `from user to userEntity`() {
        val userEntity = UserEntity(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )
        expectThat(userEntity).isEqualTo(userMapper.mapToUserEntity(user))
    }

    @Test
    fun `from userEntity to user`() {
        val userEntity = UserEntity(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )
        expectThat(userMapper.mapToUserModel(userEntity)).isEqualTo(user)
    }

    @Test
    fun `from createUser to user`() {
        val userToCreate = CreateUser(
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com"
        )
        expectThat(userMapper.mapToUserModel(
            UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            userToCreate)
        ).isEqualTo(user)
    }

    @Test
    fun `from updateUser to user`() {
        val userToUpdate = UpdateUser(
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com"
        )
        expectThat(userMapper.mapToUserModel(
            UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            userToUpdate,
            LocalDate.now())
        ).isEqualTo(user)
    }
}