package models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class User(
    @JsonProperty("id") val id: Int,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    @JsonProperty("age") val age: Int,
    @JsonProperty("login") val login: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("registration_date") val registrationDate: LocalDate
)
{
    companion object {
        val usersList: MutableList<User> = mutableListOf(
            User(1,"Default", "User", 18, "defUser", "default.user@gmail.com", LocalDate.now())
        )
    }
}
