package com.sapozhnikov.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ApiModel(value = "User", description = "user model to store in database")
data class User(
    @ApiModelProperty(
        value = "ID",
        example = "22",
    )
    @JsonProperty("id")
    val uid: String,

    @ApiModelProperty(
        value = "first username",
        example = "Jack",
    )
    @JsonProperty("first_name")
    val firstName: String,

    @ApiModelProperty(
        value = "second username",
        example = "Dawson",
    )
    @JsonProperty("last_name")
    val lastName: String,

    @ApiModelProperty(
        value = "user age",
        example = "25",
    )
    @JsonProperty("age")
    val age: Int,

    @ApiModelProperty(
        value = "user login",
        example = "jAckDaWson23",
    )
    @JsonProperty("login")
    val login: String,

    @ApiModelProperty(
        value = "user email",
        example = "jack.dawson@gmail.com",
    )
    @JsonProperty("email")
    val email: String,

    @ApiModelProperty(
        value = "user data registration",
        example = "yyyy/mm/dd",
    )
    @JsonProperty("registration_date")
    val createdAt: LocalDate
)
{
    companion object {
        val usersList: MutableList<User> = mutableListOf(
            User(UUID.randomUUID().toString(),"Default", "User", 18, "defUser", "default.user@gmail.com", LocalDate.now())
        )
    }
}
