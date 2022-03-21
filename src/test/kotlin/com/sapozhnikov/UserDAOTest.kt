package com.sapozhnikov

import com.codahale.metrics.MetricRegistry
import com.sapozhnikov.configuration.ManagementServiceConfiguration
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.model.domain.UserEntity
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jackson.Jackson
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.setup.Environment
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*


object UserDAOTest {

    private lateinit var environment: Environment
    private lateinit var jdbi: Jdbi
    private lateinit var configuration: ManagementServiceConfiguration

    private lateinit var dataSourceFactory: DataSourceFactory

    @BeforeAll
    fun setup() {
        configuration = ManagementServiceConfiguration()
        environment = Environment("test-env", Jackson.newObjectMapper(), null, MetricRegistry(), null, null, configuration)
        jdbi = JdbiFactory().build(environment, configuration.database, "h2-test")

        jdbi.installPlugin(H2DatabasePlugin())
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())
    }

    @BeforeAll
    fun getDataSourceFactory() {
        dataSourceFactory = DataSourceFactory()
        dataSourceFactory.driverClass = "org.h2.Driver"
        dataSourceFactory.url = "jdbc:h2:mem:testDb"
        dataSourceFactory.user = "sa"
        dataSourceFactory.password = ""
    }


    private lateinit var userDAO: UserDAO



    class UserDaoTest {
        @Test
        fun test() {

            userDAO = jdbi.onDemand(UserDAO::class)

            val user = UserEntity(
                id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
                firstName = "Default",
                lastName = "User",
                age = 20,
                login = "defUser",
                email = "default.user@gmail.com",
                registrationDate = LocalDate.of(2022, 3, 21)
            )

            userDAO.insertUser(user)
        }
    }

}