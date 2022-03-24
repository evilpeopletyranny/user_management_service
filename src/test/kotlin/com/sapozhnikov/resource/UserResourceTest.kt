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
import strikt.assertions.isNotNull
import java.time.LocalDate
import java.util.*
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
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

    private var userModel: User = User(
        id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
        firstName = "Default",
        lastName = "User",
        age = 20,
        login = "defUser",
        email = "def.userModel@gmail.com",
        registrationDate = LocalDate.now()
    )

    private var userEntity: UserEntity = UserEntity(
        id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
        firstName = "Default",
        lastName = "User",
        age = 20,
        login = "defUser",
        email = "def.userModel@gmail.com",
        registrationDate = LocalDate.now()
    )

    @BeforeEach
    fun setup() {
        every { userMapper.mapToUserModel(userEntity) } returns
                userModel
    }

    @Test
    fun `create new user`() {
        val createUser = CreateUser(
            userModel.firstName, userModel.lastName, userModel.age, userModel.login, userModel.email
        )

        every { userDAO.insertUser(userEntity) } returns
            Unit

        every { userMapper.mapToUserEntity(
            any(),
            createUser,
            LocalDate.now())
        } returns userEntity

        val entity: Entity<CreateUser> = Entity.entity(
            createUser,
            MediaType.APPLICATION_JSON_TYPE
        )
        val response = ext.target("/user")
            .request()
            .post(entity)

        println(response.date)
        println(response.status)
        println(response.statusInfo)

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
        verify { userDAO.insertUser(userEntity) }
        verify {
            userMapper.mapToUserEntity(
                any(),
                createUser,
                LocalDate.now()
            )
        }
    }

    @Test
    fun `user creation error, uniqueness violation`() {
        val createUser = CreateUser(
            userModel.firstName, userModel.lastName, userModel.age, userModel.login, userModel.email
        )

        every { userDAO.insertUser(userEntity) } throws
                UnableToExecuteStatementException("login uniqueness violation")

        every { userMapper.mapToUserEntity(
            any(),
            createUser,
            LocalDate.now())
        } returns userEntity

        val entity: Entity<CreateUser> = Entity.entity(
            createUser,
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.CONFLICT)
        verify { userDAO.insertUser(userEntity) }
        verify { userMapper.mapToUserEntity(
                any(),
                createUser,
                LocalDate.now()
            )
        }
    }

    @Test
    fun `user creation error, validation failed`() {
        val createUser = CreateUser(
            firstName = "A",
            lastName = "B",
            age = 999,
            login = userModel.login,
            email = userModel.email
        )

        val entity: Entity<CreateUser> = Entity.entity(
            createUser,
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.status).isEqualTo(422)
    }

    @Test
    fun `get all users`() {
        every { userMapper.mapToUserEntity(userModel) } returns
                userEntity

        every { userDAO.findAllUser() } returns
                listOf(userMapper.mapToUserEntity(userModel))


        val response = ext.target("/user")
            .request()
            .get()

        expectThat(response.readEntity(object : GenericType<List<User>>() {})).isNotNull()
        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)

        verify { userMapper.mapToUserEntity(userModel) }
        verify { userDAO.findAllUser() }
    }

    @Test
    fun `get user success`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.of(userEntity)

        val response = ext.target("/user/${userModel.id}")
            .request()
            .get(User::class.java)

        expectThat(response.id).isEqualTo(userModel.id)

        verify { userDAO.findUserById(userModel.id) }
    }

    @Test
    fun `get user not found`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.empty()

        val response = ext.target("/user/${userModel.id}")
            .request()
            .get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)

        verify { userDAO.findUserById(userModel.id)  }
    }

    @Test
    fun `delete user`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.of(userEntity)
        every { userDAO.deleteById(userModel.id) } returns
                Unit

        val response = ext.target("/user/${userModel.id}")
            .request()
            .delete()

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)

        verify { userDAO.findUserById(userModel.id) }
        verify { userDAO.deleteById(userModel.id) }
    }

    @Test
    fun `user delete error, not found`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.empty()

        val response = ext.target("/user/${userModel.id}")
            .request()
            .delete()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)

        verify { userDAO.findUserById(userModel.id) }
    }

    @Test
    fun `update user`() {
        val updateUser = UpdateUser(
            userModel.firstName,
            userModel.lastName,
            userModel.age,
            userModel.login,
            userModel.email
        )

        every { userDAO.findUserById(userModel.id) } returns
                Optional.of(userEntity)

        every { userMapper.mapToUserEntity(
            userModel.id,
            updateUser,
            LocalDate.now()
        ) } returns userEntity

        every { userDAO.updateUser(userEntity) } returns
                Unit

        val entity: Entity<UpdateUser> = Entity.entity(
            updateUser,
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${userModel.id}")
            .request()
            .put(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)

        verify { userDAO.findUserById(userModel.id) }
        verify {  userMapper.mapToUserEntity(
            userModel.id,
            updateUser,
            LocalDate.now()
        ) }
        verify { userDAO.updateUser(userEntity) }
    }

    @Test
    fun `user update error not found`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.empty()

        val entity: Entity<UpdateUser> = Entity.entity(
            UpdateUser(userModel.firstName, userModel.lastName, userModel.age, userModel.login, userModel.email),
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${userModel.id}")
            .request()
            .put(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)

        verify { userDAO.findUserById(userModel.id) }
    }

    @Test
    fun `user update error , uniqueness violation`() {
        val updateUser = UpdateUser(
            userModel.firstName,
            userModel.lastName,
            userModel.age,
            userModel.login,
            userModel.email
        )

        every { userDAO.findUserById(userModel.id) } returns
                Optional.of(userEntity)

        every { userDAO.updateUser(userEntity) } throws
                UnableToExecuteStatementException("login uniqueness violation")

        every { userMapper.mapToUserEntity(
                any(),
                updateUser,
                LocalDate.now()
            )
        } returns userEntity

        val entity: Entity<UpdateUser> = Entity.entity(
            updateUser,
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${userModel.id}")
            .request()
            .put(entity)

        expectThat(response.status).isEqualTo(409)

        verify { userDAO.findUserById(userModel.id) }
        verify { userDAO.updateUser(userEntity) }
        verify { userMapper.mapToUserEntity(
            any(),
            updateUser,
            LocalDate.now()
        ) }
    }

    @Test
    fun `user update error, validation failed`() {
        val updateUser = UpdateUser(
            firstName = "A",
            lastName = "B",
            age = 999,
            login = userModel.login,
            email = userModel.email
        )

        val entity: Entity<UpdateUser> = Entity.entity(
            updateUser,
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user/${userModel.id}")
            .request()
            .put(entity)

        expectThat(response.status).isEqualTo(422)
    }
}
