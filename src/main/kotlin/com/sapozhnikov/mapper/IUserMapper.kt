package com.sapozhnikov.mapper

import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import com.sapozhnikov.model.dao.UserEntity
import java.time.LocalDate
import java.util.*

interface IUserMapper {
    fun mapToUserEntity(userModel: User): UserEntity
    fun mapToUserModel(userEntity: UserEntity): User
    fun mapToUserModel(id: UUID, userToCreate: CreateUser): User
    fun mapToUserModel(id: UUID, userToUpdate: UpdateUser, registrationDate: LocalDate): User
}