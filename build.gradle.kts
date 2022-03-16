import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.vladsapozhnikov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.dropwizard:dropwizard-core:2.0.28")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")
    implementation("io.swagger:swagger-core:1.6.5")
    implementation("io.swagger:swagger-jersey2-jaxrs:1.6.5")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("ManagementServiceApp")
}