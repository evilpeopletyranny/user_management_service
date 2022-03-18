package com.sapozhnikov

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sapozhnikov.mapper.IUserMapper
import com.sapozhnikov.mapper.UserMapper
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.resource.UserResource
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.federecio.dropwizard.swagger.SwaggerBundle
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class ManagementServiceApp : Application<ManagementServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args : Array<String>) = ManagementServiceApp().run(*args)
    }

    override fun getName(): String {
        return "user-management-service"
    }

    override fun initialize(bootstrap: Bootstrap<ManagementServiceConfiguration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())

        bootstrap.addBundle(object : SwaggerBundle<ManagementServiceConfiguration>() {
            override fun getSwaggerBundleConfiguration(configuration: ManagementServiceConfiguration): SwaggerBundleConfiguration {
                return SwaggerBundleConfiguration().apply {
                    resourcePackage = "com.sapozhnikov"
                    title = "user management server"
                    description = "Generated documentation for REST API server."
                }
            }
        })

        bootstrap.addBundle(object : MigrationsBundle<ManagementServiceConfiguration>() {
            override fun getDataSourceFactory(configuration: ManagementServiceConfiguration): DataSourceFactory {
                return configuration.database
            }
        })


    }

    override fun run(configuration: ManagementServiceConfiguration, environment: Environment) {
        LiquibaseMigrator.migrate(configuration.database, environment)
//        val factory = JdbiFactory()
//        val jdbi = factory.build(environment, configuration.database, "h2")
//        jdbi.installPlugin(H2DatabasePlugin())
//        jdbi.installPlugin(KotlinPlugin())
//        jdbi.installPlugin(KotlinSqlObjectPlugin())

        val di = DependencyInjectionConfiguration.getInstance(configuration, environment)

        val userResource = di.instance<UserResource>()
        environment.jersey().register(userResource)
    }
}

object DependencyInjectionConfiguration {
    fun getInstance(configuration: ManagementServiceConfiguration, environment: Environment) : Kodein {
        return Kodein {
            bind<JdbiFactory>() with singleton { JdbiFactory() }
            bind<Jdbi>() with singleton {
                instance<JdbiFactory>()
                .build(environment, configuration.database, "h2")
                .installPlugin(H2DatabasePlugin())
                .installPlugin(KotlinPlugin())
                .installPlugin(KotlinSqlObjectPlugin())}
            bind<IUserMapper>() with singleton { UserMapper() }
            bind<UserDAO>() with singleton { instance<Jdbi>().onDemand(UserDAO::class ) }
        }
    }
}