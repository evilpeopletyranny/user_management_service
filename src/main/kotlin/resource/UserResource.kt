package resource

import models.User
import java.util.concurrent.atomic.AtomicInteger
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Контролер для работы с моделью User
 **/
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
class UserResource {
    /**
     * Для задания id
     * Начинается с 1, т.к. в списке всегда есть 1 тестовый user
     * */
    private val counter: AtomicInteger = AtomicInteger(1)

    /**
     * Создание нового user'a
     * */
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    fun createNewUser(newUser: User) {
        User.usersList.add(
            User(counter.incrementAndGet(), newUser.firstName, newUser.lastName, newUser.age, newUser.login, newUser.email, newUser.registrationDate)
        )
    }

    /**
     * Получение всех user'ов
     * */
    @GET
    @Path("/all")
    fun getAllUsers(): List<User> {
        return User.usersList
    }

    /**
     * Получение определенного user'a по id
     * */
    @GET
    @Path("{id}")
    fun getUser(@PathParam("id") id: Int): User? {
        return User.usersList.find { user -> user.id == id}
    }

    /**
     * Изменение user'a
     * */
    @PUT
    @Path("{id}")
    fun updateUser(userToUpdate: User): User? {
        //Наверное проверку на наличие юзера можно сделать лучше
        val ind = User.usersList.indexOf(User.usersList.find { user -> user.id == userToUpdate.id })
        if (ind != -1) {
            User.usersList[ind] = userToUpdate
            return User.usersList[ind]
        }
        return null
    }

    /**
     * Удаление user'a по id
     * */
    @DELETE
    @Path("{id}")
    fun deleteUser(@PathParam("id") id: Int) {
        User.usersList.removeIf { user -> user.id == id}
    }
}