package com.sapozhnikov.model.dao

import com.sapozhnikov.model.domain.UserEntity
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

interface UserDAO {

    @SqlQuery(
        """
            SELECT * FROM USERS
        """
    )
    fun findAllUser(): List<UserEntity>

    @SqlQuery(
        """
            SELECT * FROM USERS 
            WHERE id = :id
        """
    )
    fun findUserById(id: UUID): UserEntity?

    @SqlUpdate(
        """
            INSERT INTO USERS (id, first_name, last_name, age, login, email, registration_date)
            VALUES (:id, :firstName, :lastName, :age, :login, :email, :registrationDate)
        """
    )
    fun insertUser(@BindBean user: UserEntity)

    @SqlUpdate(
        """
            DELETE FROM USERS 
            WHERE id = :id
        """
    )
    fun deleteById(id: UUID)

    @SqlUpdate(
        """
            UPDATE USERS SET (first_name, last_name, age, login, email) = 
            (:firstName, :lastName, :age, :login, :email)
            WHERE id = :id
        """
    )
    fun updateUser(@BindBean user: UserEntity)
}