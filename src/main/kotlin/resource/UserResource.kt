package resource

import models.CreateUser
import models.UpdateUser
import models.User
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Controller for working with the user model
 **/
@Path("/user")
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
    fun getUser(@PathParam("id") id: Int): Response? {
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
    fun updateUser(@PathParam("id") id: Int, @Valid userToUpdate: UpdateUser): Response? {
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
    @Path("{id}")
    fun deleteUser(@PathParam("id") id: Int): Response? {
        val user = User.usersList.find { user -> user.id == id }

        return if (user != null)
        {
            User.usersList.remove(user)
            Response.ok(user).build()
        }
        else Response.status(Response.Status.NOT_FOUND).build()
    }
}