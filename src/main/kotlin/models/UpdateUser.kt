package models

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class UpdateUser(
    @NotBlank @Length(min = 2, max = 50)
    @JsonProperty("first_name")
    val firstName: String,

    @NotBlank
    @Length(min = 2, max = 50)
    @JsonProperty("last_name")
    val lastName: String,

    @Range(min = 16, max = 99)
    @JsonProperty("age")
    val age: Int,

    @Length(min = 4, max = 50)
    @JsonProperty("login")
    val login: String,

    @Email(regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    @JsonProperty("email")
    val email: String
) {
}