import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.swagger.jackson.SwaggerModule
import resource.UserResource

class ManagementServiceApp : Application<ManagementServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args : Array<String>) = ManagementServiceApp().run(*args)
    }

    override fun getName(): String {
        return "user-management-service"
    }

    override fun initialize(bootstrap: Bootstrap<ManagementServiceConfiguration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
        bootstrap.objectMapper.registerModule(SwaggerModule())
    }

    override fun run(configuration: ManagementServiceConfiguration?, environment: Environment?) {
        val userResource = UserResource()

        if (environment != null) {
            environment.jersey().register(userResource)
        }
    }
}