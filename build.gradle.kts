import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
    id("com.google.cloud.tools.jib") version "1.8.0"
}

val versionObj = Version(major = 0, minor = 1, revision = 0)

group = "com.grosslicht.discord"
version = "$versionObj"

repositories {
    mavenCentral()
    jcenter()
}

val discord4jVersion = "3.0.10"
val slf4jVersion = "1.7.25"
val log4jVersion = "2.11.1"
val kotlinLoggingVersion = "1.7.6"
val koinVersion = "2.0.1"
val konfVersion = "0.20.0"
val jedisVersion = "3.1.0"
val bucket4jVersion = "4.5.0"
val caffeineVersion = "2.8.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.discord4j:discord4j-core:$discord4jVersion")

    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    implementation("org.koin:koin-core:$koinVersion")
    implementation("com.uchuhimo:konf-core:$konfVersion")
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:$bucket4jVersion")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-jcache:$bucket4jVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("com.github.ben-manes.caffeine:jcache:$caffeineVersion")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val mainClass = "com.grosslicht.discord.thanksbot.ThanksBotKt"

application {
    mainClassName = mainClass
}

jib {
    container.mainClass = mainClass
}

jib.to {
    image = System.getenv("CONTAINER_NAME")
    tags = setOf("latest", "${project.version}")
    auth {
        username = System.getenv("DOCKER_USERNAME")
        password = System.getenv("DOCKER_PASSWORD")
    }

}

data class Version(val major: Int, val minor: Int, val revision: Int) {
    private val buildNumber: String = System.getenv("BUILD_NUMBER") ?: "dev"

    override fun toString(): String = "$major.$minor.$revision-$buildNumber"
}