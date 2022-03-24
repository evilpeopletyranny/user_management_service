package com.sapozhnikov.mapper

import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import com.sapozhnikov.model.dao.UserEntity
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

    override fun mapToUserEntity(id: UUID, userToCreate: CreateUser, registrationDate: LocalDate): UserEntity {
        return UserEntity(
            id,
            userToCreate.firstName,
            userToCreate.lastName,
            userToCreate.age,
            userToCreate.login,
            userToCreate.email,
            registrationDate
        )
    }

    override fun mapToUserEntity(id: UUID, userToUpdate: UpdateUser, registrationDate: LocalDate): UserEntity {
        return UserEntity(
            id,
            userToUpdate.firstName,
            userToUpdate.lastName,
            userToUpdate.age,
            userToUpdate.login,
            userToUpdate.email,
            registrationDate
        )
    }
}