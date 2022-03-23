package com.sapozhnikov.resource

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sapozhnikov.mapper.UserMapper
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.model.dao.UserEntity
import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
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
            any(),
            CreateUser(user.firstName, user.lastName, user.age, user.login, user.email))
        } returns user

        val entity: Entity<CreateUser> = Entity.entity(
            CreateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            MediaType.APPLICATION_JSON_TYPE
        )
        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
        verify { userDAO.insertUser(userMapper.mapToUserEntity(user)) }
    }

    @Test
    fun `user creation error, uniqueness violation`() {
        every { userDAO.insertUser(userMapper.mapToUserEntity(user)) } throws
                UnableToExecuteStatementException("login uniqueness violation")
        every { userMapper.mapToUserModel(
            any(),
            CreateUser(user.firstName, user.lastName, user.age, user.login, user.email))
        } returns user

        val entity: Entity<CreateUser> = Entity.entity(
            CreateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.CONFLICT)
        verify { userDAO.insertUser(userMapper.mapToUserEntity(user)) }
    }

    /**
     * THIS DOES NOT WORK
     * */
    @Test
    fun `get all users`() {
        every { userMapper.mapToUserEntity(user) } returns
                UserEntity(user.id, user.firstName, user.lastName, user.age, user.login, user.email, user.registrationDate)
        every { userDAO.findAllUser() } returns
                listOf(userMapper.mapToUserEntity(user))

//        val response = ext.target("/user")
//            .request()
//            .get(GenericType<List<User>> {})

    }


    @Test
    fun `get user success`() {
        every { userMapper.mapToUserEntity(user) } returns
                UserEntity(user.id, user.firstName, user.lastName, user.age, user.login, user.email, user.registrationDate)
        every { userMapper.mapToUserModel(userMapper.mapToUserEntity(user)) } returns
                user
        every { userMapper.mapToUserModel(
            any(),
            UpdateUser(user.lastName, user.lastName, user.age, user.login, user.email),
            LocalDate.now())
        } returns user
        every { userDAO.findUserById(user.id) } returns
                Optional.of(userMapper.mapToUserEntity(user))

        val response = ext.target("/user/${user.id}")
            .request()
            .get(User::class.java)

        expectThat(response.id).isEqualTo(user.id)
        verify { userDAO.findUserById(user.id) }
    }

    @Test
    fun `get user not found`() {
        every { userDAO.findUserById(user.id) } returns
                Optional.empty()

        val response = ext.target("/user/${UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")}")
            .request()
            .get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
    }

    /**
     * THIS DOES NOT WORK
     * */
    @Test
    fun `update user`() {
        every { userMapper.mapToUserEntity(user) } returns
                UserEntity(user.id, user.firstName, user.lastName, user.age, user.login, user.email, user.registrationDate)
        every { userMapper.mapToUserModel(userMapper.mapToUserEntity(user)) } returns
                user
        every { userMapper.mapToUserModel(
            any(),
            UpdateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            LocalDate.now()
        ) } returns user
        every { userDAO.findUserById(user.id) } returns
                Optional.of(userMapper.mapToUserEntity(user))
        every { userDAO.updateUser(userMapper.mapToUserEntity(user)) } returns
                Unit

        val entity: Entity<UpdateUser> = Entity.entity(
            UpdateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")}")
            .request()
            .put(entity)

        println(response.status)
        println(response.date)
        println(response.statusInfo)
    }

    @Test
    fun `user update error not found`() {
        every { userDAO.findUserById(user.id) } returns
                Optional.empty()

        val entity: Entity<UpdateUser> = Entity.entity(
            UpdateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60")}")
            .request()
            .put(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
    }

    @Test
    fun `user update error , uniqueness violation`() {
        every { userMapper.mapToUserEntity(user) } returns
                UserEntity(user.id, user.firstName, user.lastName, user.age, user.login, user.email, user.registrationDate)
        every { userDAO.findUserById(user.id) } returns
                Optional.of(userMapper.mapToUserEntity(user))
        every { userDAO.updateUser(userMapper.mapToUserEntity(user)) } throws
                UnableToExecuteStatementException("login uniqueness violation")
        every { userMapper.mapToUserModel(
            any(),
            UpdateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            user.registrationDate)
        } returns user

        val entity: Entity<UpdateUser> = Entity.entity(
            UpdateUser(user.firstName, user.lastName, user.age, user.login, user.email),
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${user.id}")
            .request()
            .put(entity)

        expectThat(response.status).isEqualTo(409)
        verify { userDAO.updateUser(userMapper.mapToUserEntity(user)) }
    }

    @Test
    fun `delete user`() {
        every { userMapper.mapToUserEntity(user) } returns
                UserEntity(user.id, user.firstName, user.lastName, user.age, user.login, user.email, user.registrationDate)
        every { userMapper.mapToUserModel(userMapper.mapToUserEntity(user)) } returns
                user
        every { userDAO.findUserById(user.id) } returns
                Optional.of(userMapper.mapToUserEntity(user))
        every { userDAO.deleteById(user.id) } returns
                Unit

        val response = ext.target("/user/${user.id}")
            .request()
            .delete()

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
    }

    @Test
    fun `user delete error, not found`() {
        every { userDAO.findUserById(user.id) } returns
                Optional.empty()

        val response = ext.target("/user/${user.id}")
            .request()
            .delete()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
    }
}
