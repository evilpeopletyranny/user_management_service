package com.sapozhnikov.configuration

import com.sapozhnikov.mapper.IUserMapper
import com.sapozhnikov.mapper.UserMapper
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.resource.UserResource
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.setup.Environment
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

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
            bind<UserResource>() with singleton { UserResource(instance(), instance()) }
        }
    }
}