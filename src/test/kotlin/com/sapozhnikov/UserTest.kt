package com.sapozhnikov

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.sapozhnikov.model.domain.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDate
import java.util.*

class UserTest {
    private lateinit var mapper: ObjectMapper
    private lateinit var user: User

    @BeforeEach
    fun beforeEach() {
        mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        user = User(
            id = UUID.fromString("fbd6086d-0afe-479c-873b-f50986c19c60"),
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
            registrationDate = LocalDate.of(2022, 3, 21)
        )
    }

    @Test
    fun `from json`() {
        val result = mapper.readValue<User>(
            """
                {
                    "id": "fbd6086d-0afe-479c-873b-f50986c19c60",
                    "first_name": "Default",
                    "last_name": "User",
                    "age": 20,
                    "login": "defUser",
                    "email": "default.user@gmail.com",
                    "registration_date": [
                        2022,
                        3,
                        21
                    ]
                }
            """.trimIndent()
        )
        expectThat(result).isEqualTo(user)
    }

    @Test
    fun `to json`() {
        val string = mapper.writeValueAsString(user)
        val tree = mapper.readTree(string)

        expectThat(tree).isEqualTo(
            mapper.createObjectNode().apply {
                put("id", "fbd6086d-0afe-479c-873b-f50986c19c60")
                put("first_name", "Default")
                put("last_name", "User")
                put("age", 20)
                put("login", "defUser")
                put("email", "default.user@gmail.com")
                putArray("registration_date").add(2022).add(3).add(21)
            }
        )

        expectThat(mapper.readValue<User>(string))
            .isEqualTo(user)
    }


}