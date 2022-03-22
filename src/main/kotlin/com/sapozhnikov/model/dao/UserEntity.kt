package com.sapozhnikov.model.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.time.LocalDate
import java.util.*

data class UserEntity(
    @ColumnName("id")
    val id: UUID,

    @ColumnName("first_name")
    val firstName: String,

    @ColumnName("last_name")
    val lastName: String,

    @ColumnName("age")
    val age: Int,

    @ColumnName("login")
    val login: String,

    @ColumnName("email")
    val email: String,

    @ColumnName("registration_date")
    val registrationDate: LocalDate
)
