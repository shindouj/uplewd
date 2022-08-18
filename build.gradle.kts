import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("org.sonarqube") version "3.0"
}

group = "net.jeikobu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val branchName: String? = System.getenv("BRANCH_NAME")
val buildNumber: String? = System.getenv("BUILD_NUMBER")

repositories {
    mavenCentral()
}

sonarqube {
    if (branchName != null) {
        properties {
            property("sonar.projectKey", "shindouj_uplewd")
            property("sonar.organization", "shindouj")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.branch.name", branchName)
            property("sonar.coverage.jacoco.xmlReportPaths", "$projectDir/build/reports/jacoco/test/jacocoTestReport.xml")
        }
    }
}

tasks.getByName<BootBuildImage>("bootBuildImage") {
    when (branchName) {
        "master" -> {
            imageName = "docker.jeikobu.net/shindouj/uplewd:latest"
            isPublish = true
        }
        "develop" -> {
            imageName = "docker.jeikobu.net/shindouj/uplewd:snapshot"
            isPublish = true
        }
        else -> {
            imageName = "shindouj/uplewd:dev_build"
            isPublish = false
        }
    }

    if (branchName != null) {
        docker {
            publishRegistry {
                username = System.getenv("DOCKER_REGISTRY_USER")
                password = System.getenv("DOCKER_REGISTRY_PASS")
                url = "https://docker.jeikobu.net/v2/"
            }
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
