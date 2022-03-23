package com.sapozhnikov.resource

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sapozhnikov.mapper.UserMapper
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.model.dao.UserEntity
import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.User
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDate
import java.util.*
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ExtendWith(DropwizardExtensionsSupport::class)
class UserResourceTest {
    private var userDAO: UserDAO = mockk()
    private var userMapper: UserMapper = mockk()

    private var ext: ResourceExtension = ResourceExtension.builder()
        .addResource(UserResource(userMapper, userDAO))
        .build()
        .apply {
            objectMapper.registerModule(KotlinModule())
        }

    private lateinit var user: User

    @BeforeEach
    fun setup() {
        user = User(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "def.user@gmail.com",
            registrationDate = LocalDate.now()
        )
    }

    @Test
    fun `create new user`() {
        every { userDAO.insertUser(userMapper.mapToUserEntity(user)) } returns
            Unit
        every { userMapper.mapToUserModel(
            user.id,
            CreateUser(user.firstName, user.lastName, user.age, user.login, user.email))
        } returns user

        val entity: Entity<CreateUser> = Entity.entity(
            CreateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            MediaType.APPLICATION_JSON_TYPE
        )
        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.status).isEqualTo(200)
        verify { userDAO.insertUser(userMapper.mapToUserEntity(user)) }
    }

    @Test
    fun `get all users`() {

    }

    @Test
    fun `get user success`() {
        every { userMapper.mapToUserEntity(user) } returns
                UserEntity(user.id, user.firstName, user.lastName, user.age, user.login, user.email, user.registrationDate)
        every { userMapper.mapToUserModel(userMapper.mapToUserEntity(user)) } returns
                user
        every { userDAO.findUserById(UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")) } returns
                Optional.of(userMapper.mapToUserEntity(user))

        val foundUser = ext.target("/user/${UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")}")
            .request()
            .get(User::class.java)

        expectThat(foundUser.id).isEqualTo(user.id)
        verify { userDAO.findUserById(UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")) }
    }

    @Test
    fun `get user not found`() {
        every { userDAO.findUserById(UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")) } returns
                Optional.empty()

        val response = ext.target("/user/${UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")}")
            .request().get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
    }

    @Test
    fun `update user`() {

    }

    @Test
    fun `delete user`() {

    }

}