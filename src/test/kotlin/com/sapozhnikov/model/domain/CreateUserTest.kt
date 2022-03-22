package com.sapozhnikov.model.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CreateUserTest {
    private lateinit var mapper: ObjectMapper
    private lateinit var userToCreate: CreateUser

    @BeforeEach
    fun beforeEach() {
        mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        userToCreate = CreateUser(
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
        )
    }

    @Test
    fun `from json`() {
        val result = mapper.readValue<CreateUser>(
            """
                {
                    "first_name": "Default",
                    "last_name": "User",
                    "age": 20,
                    "login": "defUser",
                    "email": "default.user@gmail.com"
                }
            """.trimIndent()
        )
        expectThat(result).isEqualTo(userToCreate)
    }

    @Test
    fun `to json`() {
        val string = mapper.writeValueAsString(userToCreate)
        val tree = mapper.readTree(string)

        expectThat(tree).isEqualTo(
            mapper.createObjectNode().apply {
                put("first_name", "Default")
                put("last_name", "User")
                put("age", 20)
                put("login", "defUser")
                put("email", "default.user@gmail.com")
            }
        )
        expectThat(mapper.readValue<CreateUser>(string))
            .isEqualTo(userToCreate)
    }
}