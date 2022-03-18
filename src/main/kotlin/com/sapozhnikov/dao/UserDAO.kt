package com.sapozhnikov.dao

import com.sapozhnikov.model.UserEntity
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Date
import java.util.*

interface UserDAO {

    @SqlQuery("SELECT * FROM USERS")
    fun findAllUser(): List<UserEntity>

    @SqlQuery("SELECT * FROM USERS WHERE id = :id")
    fun getUserById(@Bind("id") id: UUID): UserEntity

    @SqlUpdate("INSERT INTO USERS (id, first_name, last_name, age, login, email, registration_date) values (:id, :first_name, :last_name, :age, :login, :email, :registration_date)")
    fun insertUser(@Bind("id") id: UUID, @Bind("first_name") first_name: String, @Bind("last_name") last_name: String, @Bind("age") age: Int, @Bind("login") login: String, @Bind("email") email: String, @Bind("registration_date") registration_date: Date)

    @SqlUpdate("DELETE FROM USERS WHERE id = :id")
    fun deleteById(@Bind("id") id: UUID)
}