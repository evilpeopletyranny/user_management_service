package com.sapozhnikov.model.dao

import com.codahale.metrics.MetricRegistry
import com.sapozhnikov.LiquibaseMigrator
import io.dropwizard.db.DataSourceFactory
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import java.time.LocalDate
import java.util.*


class UserDAOTest {

    private lateinit var userDAO: UserDAO

    @BeforeEach
    fun setup() {
        val datasource = DataSourceFactory().apply {
            driverClass = "org.h2.Driver"
            user = ""
            password = ""
            url = "jdbc:h2:mem:testdb"
            charset("UTF-8")
        }.build(MetricRegistry(), "testdb")

        LiquibaseMigrator.migrate(datasource)

        val jdbi = Jdbi.create(datasource)
            .installPlugin(H2DatabasePlugin())
            .installPlugin(KotlinPlugin())
            .installPlugin(KotlinSqlObjectPlugin())

        userDAO = jdbi.onDemand(UserDAO::class)
    }


    @Test
    fun `user successfully created`() {
        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        userDAO.insertUser(user)
        val result = userDAO.findUserById(user.id)
        expectThat(result.isPresent).isTrue()
        expectThat(result.get()).isEqualTo(user)
    }

    @Test
    fun `user not created because such login already exists`() {
        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val userWithDuplicateLogin = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Jack",
            lastName = "Dawson",
            age = 20,
            login = "defUser",
            email = "Jack.Dawson@gmail.com",
            registrationDate = LocalDate.now()
        )

        userDAO.insertUser(user)
        assertThrows<UnableToExecuteStatementException> {
            userDAO.insertUser(userWithDuplicateLogin)
        }
    }

    @Test
    fun `user not created because such email already exists`() {
        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val userWithDuplicateLogin = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Jack",
            lastName = "Dawson",
            age = 20,
            login = "JacKDaWsoN",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        userDAO.insertUser(user)
        assertThrows<UnableToExecuteStatementException> {
            userDAO.insertUser(userWithDuplicateLogin)
        }
    }

    @Test
    fun `user updated successfully`() {
        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val userToUpdate = UserEntity(
            id = user.id,
            firstName = "Jack",
            lastName = "Dawson",
            age = 20,
            login = "JacKDaWsoN",
            email = "Jack.Dawson@gmail.com",
            registrationDate = user.registrationDate
        )

        userDAO.insertUser(user)
        userDAO.updateUser(userToUpdate)

        val result = userDAO.findUserById(user.id)
        expectThat(result.isPresent).isTrue()
        expectThat(result.get().id).isEqualTo(user.id)
        expectThat(result.get()).isEqualTo(userToUpdate)
    }

    @Test
    fun `user not updated because such login already exists`() {
        val oldUser = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Old",
            lastName = "User",
            age = 30,
            login = "oldUser",
            email = "old.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val userToUpdate = UserEntity(
            id = user.id,
            firstName = "Jack",
            lastName = "Dawson",
            age = 20,
            login = "oldUser",
            email = "Jack.Dawson@gmail.com",
            registrationDate = user.registrationDate
        )

        userDAO.insertUser(oldUser)
        userDAO.insertUser(user)

        assertThrows<UnableToExecuteStatementException>  {
            userDAO.updateUser(userToUpdate)
        }
    }

    @Test
    fun `user not updated because such email already exists`() {
        val oldUser = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Old",
            lastName = "User",
            age = 30,
            login = "oldUser",
            email = "old.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        val userToUpdate = UserEntity(
            id = user.id,
            firstName = "Jack",
            lastName = "Dawson",
            age = 20,
            login = "oldUser",
            email = "old.user@gmail.com",
            registrationDate = user.registrationDate
        )

        userDAO.insertUser(oldUser)
        userDAO.insertUser(user)

        assertThrows<UnableToExecuteStatementException>  {
            userDAO.updateUser(userToUpdate)
        }
    }

    @Test
    fun `user deleted successfully`() {
        val user = UserEntity(
            id = UUID.randomUUID(),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.now()
        )

        userDAO.insertUser(user)
        userDAO.deleteById(user.id)

        expectThat(userDAO.findUserById(user.id).isEmpty).isTrue()
    }
}