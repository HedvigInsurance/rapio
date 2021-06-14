import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.springframework.boot") version "2.1.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.31"
    kotlin("plugin.spring") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    id("org.liquibase.gradle") version "2.0.1"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.31"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.31"
}

extra["springCloudVersion"] = "Greenwich.SR3"
extra["jdbiVersion"] = "3.8.2"
extra["arrowVerions"] = "0.10.0"

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/arrow-kt/arrow-kt/")
    // maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local/' } // for SNAPSHOT builds
    maven {
        url = uri("https://maven.pkg.github.com/HedvigInsurance/libs")
        credentials {
            username = project.findProperty("GITHUB_USER") as? String ?: System.getenv("GITHUB_USER")
            password = project.findProperty("GITHUB_TOKEN") as? String ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.hedvig.libs", "logging", "1.0.2")

    implementation(platform("org.jdbi:jdbi3-bom:${property("jdbiVersion")}"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}"))

    constraints {
        create("org.springframework:spring-test") {
            version { reject("4.2.4.RELEASE") }
        }
    }

    implementation("org.javamoney", "moneta", "1.3", ext = "pom")
    implementation("org.javamoney", "moneta", "1.3")
    implementation("org.zalando", "jackson-datatype-money", "1.1.1")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-openfeign-core")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("io.github.openfeign:feign-httpclient")

    implementation("org.liquibase:liquibase-core:3.6.3")
    implementation("com.vladmihalcea:hibernate-types-52:2.6.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.31")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.31")
    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")

    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")
    implementation("org.jdbi:jdbi3-sqlobject")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject")
    implementation("org.jdbi:jdbi3-jackson2")

    api("org.postgresql:postgresql:42.2.6")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-config")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-ribbon")

    implementation("io.github.microutils:kotlin-logging:1.7.6")

    implementation("io.sentry:sentry-spring:1.7.27")
    implementation("io.sentry:sentry-logback:1.7.27")
    implementation("net.logstash.logback:logstash-logback-encoder:6.2")
    implementation("ch.qos.logback:logback-access")

    implementation("com.neovisionaries:nv-i18n:1.27")

    api("io.arrow-kt:arrow-core:${property("arrowVerions")}")
    api("io.arrow-kt:arrow-syntax:${property("arrowVerions")}")
    api("io.arrow-kt:arrow-optics:${property("arrowVerions")}")
    kapt("io.arrow-kt:arrow-meta:${property("arrowVerions")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    testImplementation("com.ninja-squad:springmockk:1.1.2")
    testImplementation("io.mockk:mockk:1.9.1")
    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("org.apache.httpcomponents:httpclient:4.5.13")

    liquibaseRuntime("org.liquibase:liquibase-core:3.6.1")
    liquibaseRuntime("org.postgresql:postgresql:42.2.6")
    liquibaseRuntime("ch.qos.logback:logback-core:1.2.3")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")
}

group = "com.hedvig"
version = "0.0.1-SNAPSHOT"
description = "rapio"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

liquibase {
    activities.register("main") {
        this.arguments =
            mapOf(
                "logLevel" to "info",
                "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
                "url" to "jdbc:postgresql://localhost:5432/rapio",
                "username" to "postgres",
                "password" to "hedvig"
            )
    }
    runList = "main"
}

allOpen {
    annotation("org.springframework.context.annotation.Configuration")
    annotation("javax.persistence.Entity")
}
noArg {
    annotation("javax.persistence.Entity")
}
