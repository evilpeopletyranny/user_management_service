package com.sapozhnikov.mappers

import com.sapozhnikov.model.User
import com.sapozhnikov.model.UserEntity

class UserMapper {
    fun map(userModel: User): UserEntity {
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

    fun map(userEntity: UserEntity): User {
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
}