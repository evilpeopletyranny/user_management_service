package com.sapozhnikov.mapper

import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import com.sapozhnikov.model.domain.UserEntity
import java.time.LocalDate
import java.util.*

interface IUserMapper {
    fun mapToUserEntity(userModel: User): UserEntity
    fun mapToUserModel(userEntity: UserEntity): User
    fun mapToUserModel(userEntity: CreateUser): User
    fun mapToUserModel(id: UUID, userEntity: UpdateUser, registrationDate: LocalDate): User
}