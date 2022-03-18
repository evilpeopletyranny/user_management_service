package com.sapozhnikov.dao

import com.sapozhnikov.model.UserEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.LocalDate
import java.util.*

interface UserDAO {

    @SqlQuery("SELECT * FROM USERS")
    fun findAllUser(): List<UserEntity>

    @SqlQuery("SELECT * FROM USERS WHERE id = :id")
    fun findUserById(@Bind("id") id: UUID): UserEntity?

    @SqlUpdate("INSERT INTO USERS (id, first_name, last_name, age, login, email, registration_date) VALUES (:id, :first_name, :last_name, :age, :login, :email, :registration_date)")
    fun insertUser(@Bind("id") id: UUID, @Bind("first_name") first_name: String, @Bind("last_name") last_name: String, @Bind("age") age: Int, @Bind("login") login: String, @Bind("email") email: String, @Bind("registration_date") registration_date: LocalDate)

    @SqlUpdate("DELETE FROM USERS WHERE id = :id")
    fun deleteById(@Bind("id") id: UUID)

    @SqlUpdate("UPDATE USERS SET (first_name, last_name, age, login, email) = (:first_name, :last_name, :age, :login, :email) WHERE id = :id")
    fun updateUser(@Bind("id") id: UUID, @Bind("first_name") first_name: String, @Bind("last_name") last_name: String, @Bind("age") age: Int, @Bind("login") login: String, @Bind("email") email: String, @Bind("registration_date") registration_date: LocalDate)
}