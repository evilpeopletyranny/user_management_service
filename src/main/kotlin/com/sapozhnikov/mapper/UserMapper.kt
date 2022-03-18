package com.sapozhnikov.mapper

import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import com.sapozhnikov.model.domain.UserEntity
import java.time.LocalDate
import java.util.UUID

class UserMapper : IUserMapper {
    override fun mapToUserEntity(userModel: User): UserEntity {
        return UserEntity(
            userModel.id,
            userModel.firstName,
            userModel.lastName,
            userModel.age,
            userModel.login,
            userModel.email,
            userModel.registrationDate
        )
    }

    override fun mapToUserModel(userEntity: UserEntity): User {
        return User(
            userEntity.id,
            userEntity.firstName,
            userEntity.lastName,
            userEntity.age,
            userEntity.login,
            userEntity.email,
            userEntity.registrationDate
        )
    }

    override fun mapToUserModel(userEntity: CreateUser): User {
        return User(
            UUID.randomUUID(),
            userEntity.firstName,
            userEntity.lastName,
            userEntity.age,
            userEntity.login,
            userEntity.email,
            LocalDate.now()
        )
    }

    override fun mapToUserModel(id: UUID, userEntity: UpdateUser, registrationDate: LocalDate): User {
        return User(
            id,
            userEntity.firstName,
            userEntity.lastName,
            userEntity.age,
            userEntity.login,
            userEntity.email,
            registrationDate
        )
    }
}