package com.sapozhnikov.resource

import com.sapozhnikov.mapper.IUserMapper
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import com.sapozhnikov.model.domain.UserEntity
import io.swagger.annotations.*
import java.util.*
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Controller for working with the user model
 **/
@Path("/user")
@Api(
    value = "user",
    description = "Rest API for user operations",
    tags = ["User API"]
)
@Produces(MediaType.APPLICATION_JSON)
class UserResource(
    private val userMapperImpl: IUserMapper,
    private val userDao: UserDAO,
) {
    /**
     * Route to create a new user
     * @param newUser - data of the created user
     * @return created user
     * */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "creating a new user",
        notes = "creates a new user based on valid data",
        response = User::class
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 422, message = "Wrong data")
        ]
    )
    fun createNewUser(@Valid newUser: CreateUser): Response {
        val user = userMapperImpl.mapToUserModel(newUser)
        userDao.insertUser(userMapperImpl.mapToUserEntity(user))
        return Response.ok(user).build()
    }

    /**
     * Route to get all users
     * @return list of all users
     * */
    @GET
    @ApiOperation(
        value = "gets all users",
        notes = "gets all users from database",
        response = User::class,
        responseContainer = "List")
    @ApiResponse(code = 200, message = "Ok")
    fun getAllUsers(
        @DefaultValue("25")
        @QueryParam("limit")
        limit: Int,

        @QueryParam("0")
        offset: Int
    ): Response {
        println(limit)
        println(offset)



        val userList: List<UserEntity> = userDao.findAllUser()

        return Response.ok(
            userList.map { user -> userMapperImpl.mapToUserModel(user) }
        ).build()
    }

    /**
     * Route to get user by id
     * @param id - user id to find
     * @return found user or 404 not found request
     *         if user does not found
     * */
    @GET
    @Path("/{id}")
    @ApiOperation(
        value = "get user",
        notes = "get user by id",
        response = User::class,
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 404, message = "User is not found")
        ]
    )
    fun getUser(@ApiParam(value = "user id to get", required = true) @PathParam("id") id: UUID): Response {
        val user = userDao.findUserById(id)

        return if (user.isEmpty) {
            Response.status(Response.Status.NOT_FOUND).build()
        }
        else Response.ok(userMapperImpl.mapToUserModel(user.get())).build()
    }

    /**
     * Route to change user data
     * @param id - user id to change
     * @param userToUpdate - changed user data
     * @return changed user or 404 not found request
     *         if user does not found
     * */
    @PUT
    @Path("{id}")
    @ApiOperation(
        value = "updating user data",
        notes = "update user data by passed id",
        response = User::class,
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 404, message = "User is not found"),
            ApiResponse(code = 422, message = "Wrong data")
        ]
    )
    fun updateUser(@ApiParam(value = "user id to update", required = true) @PathParam("id") id: UUID, @Valid userToUpdate: UpdateUser): Response {
        val user = userDao.findUserById(id)

        return if (user.isEmpty) {
            Response.status(Response.Status.NOT_FOUND).build()
        }
        else {
            val updatedUser = userMapperImpl.mapToUserModel(user.get().id, userToUpdate, user.get().registrationDate)
            userDao.updateUser(userMapperImpl.mapToUserEntity(updatedUser))
            Response.ok(updatedUser).build()
        }

    }

    /**
     * Route to delete a user
     * @param id - user id to delete
     * @return deleted user or 404 not found request
     *         if user does not found
     * */
    @DELETE
    @ApiOperation(
        value = "deleting user",
        notes = "delete user by id",
        response = User::class,
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 404, message = "User is not found"),
        ]
    )
    @Path("{id}")
    fun deleteUser(@ApiParam(value = "user id to delete", required = true) @PathParam("id") id: UUID): Response {
        val user = userDao.findUserById(id)

        return if (user.isEmpty) {
            Response.status(Response.Status.NOT_FOUND).build()
        }
        else {
            userDao.deleteById(id)
            Response.ok(userMapperImpl.mapToUserModel(user.get())).build()
        }

    }
}