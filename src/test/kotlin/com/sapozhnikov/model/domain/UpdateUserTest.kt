package com.sapozhnikov.model.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class UpdateUserTest {
    private lateinit var mapper: ObjectMapper
    private lateinit var userToUpdate: UpdateUser

    @BeforeEach
    fun beforeEach() {
        mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        userToUpdate = UpdateUser(
            firstName = "Default",
            lastName = "User",
            age = 20,
            login = "defUser",
            email = "default.user@gmail.com",
        )
    }

    @Test
    fun `from json`() {
        val result = mapper.readValue<UpdateUser>(
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
        expectThat(result).isEqualTo(userToUpdate)
    }

    @Test
    fun `to json`() {
        val string = mapper.writeValueAsString(userToUpdate)
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

        expectThat(mapper.readValue<UpdateUser>(string))
            .isEqualTo(userToUpdate)
    }
}