package com.sapozhnikov.model.domain

import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

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
