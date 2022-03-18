package com.sapozhnikov.configuration

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import javax.validation.Valid

class ManagementServiceConfiguration(
    @Valid @JsonProperty("database") val database: DataSourceFactory = DataSourceFactory()
): Configuration()