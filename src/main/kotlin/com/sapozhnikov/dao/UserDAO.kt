package com.sapozhnikov.dao

import com.sapozhnikov.models.User
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(User::class)
interface UserDAO {

    @SqlQuery("SELECT * FROM USERS")
    fun findAllUser(): List<User>
}