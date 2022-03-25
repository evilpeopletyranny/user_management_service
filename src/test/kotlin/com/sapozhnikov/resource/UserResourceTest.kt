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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    private fun parseQueryString(queryString: String): List<Pair<String, String>> {
        require(queryString.startsWith("?")) {
            "Query string must start with ?"
        }

        if (queryString.isEmpty() || queryString == "?") return emptyList()

        return queryString.drop(1).split("&").map { item ->
            item.split("=").let { param ->
                when (param.size) {
                    1 -> param.first() to ""
                    2 -> param.first() to param.last()
                    else -> throw IllegalArgumentException("Multiple = signs in parameter: '$item'")
                }
            }
        }
    }

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
            any())
        } returns userEntity

        val entity: Entity<CreateUser> = Entity.entity(
            createUser,
            MediaType.APPLICATION_JSON_TYPE
        )
        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.readEntity(User::class.java)).isEqualTo(userModel)
        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
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
            any())
        } returns userEntity

        val entity: Entity<CreateUser> = Entity.entity(
            createUser,
            MediaType.APPLICATION_JSON_TYPE
        )

        val response = ext.target("/user")
            .request()
            .post(entity)

        expectThat(response.statusInfo).isEqualTo(Response.Status.CONFLICT)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            """
                {
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                }
            """
        ]
    )
    fun `user creation error, empty fields`(body: String) {
        val createUser = CreateUser(
            userModel.firstName,
            userModel.lastName,
            userModel.age,
            userModel.login,
            userModel.email
        )
        every { userMapper.mapToUserEntity(
            any(),
            createUser,
            any())
        } returns userEntity

        val response = ext.target("/user")
            .request()
            .post(Entity.json(body))

        expectThat(response.status).isEqualTo(400)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "       ",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "      ",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "      ",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "       "
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": 124335
                }
            """,
            """
                {
                   "first_name": "q",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "Q",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 10, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "q",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user"
                }
            """
        ]
    )
    fun `user creation error, validation failed`(body: String) {
        val createUser = CreateUser(
            userModel.firstName,
            userModel.lastName,
            userModel.age,
            userModel.login,
            userModel.email
        )
        every { userMapper.mapToUserEntity(
            any(),
            createUser,
            any())
        } returns userEntity

        val response = ext.target("/user")
            .request()
            .post(Entity.json(body))

        expectThat(response.status).isEqualTo(422)
    }

    @Test
    fun `get all users`() {
        every { userMapper.mapToUserEntity(userModel) } returns
                userEntity

        every { userDAO.findAllUser(
            limit = 25,
            offset = 0,
            orderBy = "id",
            sort = "ASC")
        } returns
            listOf(userEntity)


        val response = ext.target("/user")
            .request()
            .get()

        val resList = response.readEntity(object : GenericType<List<User>>() {})
        expectThat(resList).isNotNull()
        expectThat(resList).isEqualTo(listOf(userModel))
        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "?",
            "?limit=1",
            "?limit=10",
            "?limit=100",
            "?offset=0",
            "?offset=10",
            "?offset=99",
            "?orderBy=id",
            "?orderBy=first_name",
            "?orderBy=last_name",
            "?orderBy=age",
            "?orderBy=login",
            "?orderBy=email",
            "?orderBy=registration_date",
            "?sort=ASC",
            "?sort=DESC"
        ]
    )
    fun `list succeeds on valid query strings` (queryString: String) {
        val params = if (queryString.isNotEmpty()) {
            parseQueryString(queryString)
        }
        else {
            emptyList()
        }

        every { userDAO.findAllUser(any(), any(), any(), any()) } returns
                emptyList()

        val response = ext.target("/user")
            .let { params.fold(it) { target, param -> target.queryParam(param.first, param.second) } }
            .request()
            .get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "?limit=-1",
            "?limit=101",
            "?offset=-1",
            "?offset=100",
            "?orderBy=idd",
            "?orderBy=firstName",
            "?orderBy=lastName",
            "?orderBy=agee",
            "?orderBy=logiin",
            "?orderBy=emaiil",
            "?orderBy=registrationDate",
            "?sort=ASCC",
            "?sort=DESCC"
        ]
    )
    fun `list fails with 400 on invalid query strings`(queryString: String) {
        val params = parseQueryString(queryString)

        val response = ext.target("/user")
            .let { params.fold(it) { target, param -> target.queryParam(param.first, param.second) } }
            .request()
            .get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.BAD_REQUEST)
    }

    @Test
    fun `list passes query params to dao correctly`() {
        every { userDAO.findAllUser(any(), any(), any(), any()) } returns
                emptyList()

        val response = ext.target("/user")
            .queryParam("limit", "10")
            .queryParam("offset", "5")
            .queryParam("orderBy", "id")
            .queryParam("sort", "ASC")
            .request()
            .get()

        verify(exactly = 1) {
            userDAO.findAllUser(
                limit = 10,
                offset = 5,
                orderBy = "id",
                sort = "ASC"
            )
        }

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
    }

    @Test
    fun `get user success`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.of(userEntity)

        val response = ext.target("/user/${userModel.id}")
            .request()
            .get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.OK)
        expectThat(response.readEntity(User::class.java)).isEqualTo(userModel)
    }

    @Test
    fun `get user not found`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.empty()

        val response = ext.target("/user/${userModel.id}")
            .request()
            .get()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
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
        expectThat(response.readEntity(User::class.java)).isEqualTo(userModel)
    }

    @Test
    fun `user delete error, not found`() {
        every { userDAO.findUserById(userModel.id) } returns
                Optional.empty()

        val response = ext.target("/user/${userModel.id}")
            .request()
            .delete()

        expectThat(response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
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
            any()
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
        expectThat(response.readEntity(User::class.java)).isEqualTo(userModel)
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
                any()
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
            any()
        ) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            """
                {
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                }
            """
        ]
    )
    fun `user update error, empty fields`(body: String) {
        val response = ext.target("/user")
            .request()
            .post(Entity.json(body))

        expectThat(response.status).isEqualTo(400)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "       ",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "      ",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "      ",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "       "
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": 124335
                }
            """,
            """
                {
                   "first_name": "q",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "Q",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 10, 
                   "login": "defUser",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "q",
                   "email": "def.user@gmail.com"
                }
            """,
            """
                {
                   "first_name": "Default",
                   "last_name": "User",
                   "age": 20, 
                   "login": "defUser",
                   "email": "def.user"
                }
            """
        ]
    )
    fun `user update error, validation failed`(body: String) {
        val updateUser = UpdateUser(
            userModel.firstName,
            userModel.lastName,
            userModel.age,
            userModel.login,
            userModel.email
        )
        every { userMapper.mapToUserEntity(
            any(),
            updateUser,
            any())
        } returns userEntity

        val response = ext.target("/user/${userModel.id}")
            .request()
            .put(Entity.json(body))

        expectThat(response.status).isEqualTo(422)
    }
}
