import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration

class ManagementServiceConfiguration: Configuration() {
    @JsonProperty("swagger")
    val swagger: SwaggerBundleConfiguration = SwaggerBundleConfiguration()
}