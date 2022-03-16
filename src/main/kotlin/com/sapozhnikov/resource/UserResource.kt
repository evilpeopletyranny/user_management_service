package com.sapozhnikov.resource

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import com.sapozhnikov.models.CreateUser
import com.sapozhnikov.models.UpdateUser
import com.sapozhnikov.models.User
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
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
class UserResource {
    /**
     * id generation
     * Starts with 1 because  User.usersList has 1 user by default
     * */
    private val counter: AtomicInteger = AtomicInteger(1)

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
    fun createNewUser(@Valid newUser: CreateUser): Response? {
        val user = User(counter.incrementAndGet(), newUser.firstName, newUser.lastName, newUser.age, newUser.login, newUser.email, LocalDate.now())
        User.usersList.add(user)
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
    fun getAllUsers(): Response? {
        return Response.ok(User.usersList).build()
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
    fun getUser(@ApiParam(value = "user id to get", required = true) @PathParam("id") id: Int): Response? {
        val user = User.usersList.find { user -> user.id == id}

        return if (user != null) Response.ok(user).build()
        else Response.status(Response.Status.NOT_FOUND).build()
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
    fun updateUser(@ApiParam(value = "user id to update", required = true) @PathParam("id") id: Int, @Valid userToUpdate: UpdateUser): Response? {
        val oldUser = User.usersList.find { user -> user.id == id }
        if (oldUser != null) {
            val ind = User.usersList.indexOf(oldUser)

            val user = User(id, userToUpdate.firstName, userToUpdate.lastName, userToUpdate.age,
                userToUpdate.login, userToUpdate.email, oldUser.registrationDate)

                User.usersList[ind] = user

                return Response.ok(user).build()
        }

        return Response.status(Response.Status.NOT_FOUND).build()
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
    fun deleteUser(@ApiParam(value = "user id to delete", required = true) @PathParam("id") id: Int): Response? {
        val user = User.usersList.find { user -> user.id == id }

        return if (user != null)
        {
            User.usersList.remove(user)
            Response.ok(user).build()
        }
        else Response.status(Response.Status.NOT_FOUND).build()
    }
}