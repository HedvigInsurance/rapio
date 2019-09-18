import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.springframework.boot") version "2.1.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.31"
    kotlin("plugin.spring") version "1.3.31"
}


repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-test")
    implementation("org.liquibase:liquibase-core:3.6.3")
    implementation("com.vladmihalcea:hibernate-types-52:2.6.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.31")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.31")
    implementation("io.springfox:springfox-swagger2:2.7.0")
    implementation("io.springfox:springfox-swagger-ui:2.7.0")
    implementation("org.springframework.cloud:spring-cloud-openfeign-core")

    api("org.postgresql:postgresql:42.2.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.2.0")
    testImplementation( "org.junit.jupiter:junit-jupiter-engine:5.2.0")
    testImplementation("com.ninja-squad:springmockk:1.1.2")
    testImplementation("io.mockk:mockk:1.9")
}

group = "com.hedvig"
version = "'0.0.1-SNAPSHOT"
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
