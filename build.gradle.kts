plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.l3m4rk"
version = "0.0.1-SNAPSHOT"
description = "unreliable"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:2.1.0")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.modulith:spring-modulith-starter-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
