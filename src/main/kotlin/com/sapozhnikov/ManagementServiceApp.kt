package com.sapozhnikov

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sapozhnikov.configuration.DependencyInjectionConfiguration
import com.sapozhnikov.configuration.ManagementServiceConfiguration
import com.sapozhnikov.resource.UserResource
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.federecio.dropwizard.swagger.SwaggerBundle
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration
import org.kodein.di.direct
import org.kodein.di.generic.instance

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

        val di = DependencyInjectionConfiguration.getInstance(configuration, environment)

        val userResource = di.direct.instance<UserResource>()
        environment.jersey().register(userResource)
    }
}