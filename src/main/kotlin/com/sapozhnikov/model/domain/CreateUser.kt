package com.sapozhnikov.model.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.*

@ApiModel(value = "CreateUser", description = "model for user creation")
data class CreateUser(
    @get:NotBlank
    @get:Size(min = 2, max = 50)
    @ApiModelProperty(
        value = "first username",
        example = "Jack",
    )
    @JsonProperty("first_name")
    val firstName: String,

    @get:NotBlank
    @get:Size(min = 2, max = 50)
    @ApiModelProperty(
        value = "second username",
        example = "Dawson",
    )
    @JsonProperty("last_name")
    val lastName: String,

    @get:Min(16)
    @get:Max(99)
    @ApiModelProperty(
        value = "user age",
        example = "25",
    )
    @JsonProperty("age")
    val age: Int,

    @get:NotBlank
    @get:Size(min = 2, max = 50)
    @ApiModelProperty(
        value = "user login",
        example = "jAckDaWson23",
    )
    @JsonProperty("login")
    val login: String,

    @get:NotBlank
    @get:Email(regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    @ApiModelProperty(
        value = "user email",
        example = "jack.dawson@gmail.com",
    )
    @JsonProperty("email")
    val email: String,
)