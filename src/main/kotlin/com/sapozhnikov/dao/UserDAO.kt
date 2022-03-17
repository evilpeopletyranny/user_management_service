package com.sapozhnikov.dao

import com.sapozhnikov.model.UserEntity
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.skife.jdbi.v2.sqlobject.Bind
import java.util.UUID

interface UserDAO {

    @SqlQuery("SELECT * FROM USERS")
    @RegisterBeanMapper(UserEntity::class)
    fun findAllUser(): List<UserEntity>

    @SqlQuery("SELECT * FROM USERS WHERE id =:id")
    fun getUserById(@Bind("id") id: UUID): UserEntity
}