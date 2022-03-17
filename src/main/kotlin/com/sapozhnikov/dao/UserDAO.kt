package com.sapozhnikov.dao

import com.sapozhnikov.model.UserEntity
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface UserDAO {

    @SqlQuery("SELECT * FROM PUBLIC.USERS")
    @RegisterBeanMapper(UserEntity::class)
    fun findAllUser(): List<UserEntity>

}