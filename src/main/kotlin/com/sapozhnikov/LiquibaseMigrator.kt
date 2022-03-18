package com.sapozhnikov

import io.dropwizard.db.DataSourceFactory
import io.dropwizard.setup.Environment
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

object LiquibaseMigrator {

    private const val NO_CONTEXT_MODE = ""

    fun migrate(database: DataSourceFactory, environment: Environment) {
        val managedDataSource = database.build(environment.metrics(), "migrations")
        val connection = managedDataSource.connection
        connection.use {
            val migrator = Liquibase("migrations.xml", ClassLoaderResourceAccessor(), JdbcConnection(connection))
            migrator.update(NO_CONTEXT_MODE)
        }
    }
}