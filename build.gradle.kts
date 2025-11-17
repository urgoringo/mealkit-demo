import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway

plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("net.ltgt.errorprone") version "4.3.0"
	id("net.ltgt.nullaway") version "2.3.0"
}

group = "com.urgoringo"
version = "0.0.1-SNAPSHOT"
description = "Mealkit"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
	testCompileOnly {
		extendsFrom(configurations.testAnnotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.mapstruct:mapstruct:1.6.3")
	implementation("org.jspecify:jspecify:1.0.0")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	errorprone("com.google.errorprone:error_prone_core:2.44.0")
	errorprone("com.uber.nullaway:nullaway:0.12.12")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers:2.0.2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.jbehave:jbehave-core:5.2.0")
    testImplementation("org.jbehave:jbehave-spring:5.2.0")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Configure NullAway to check the main package
nullaway {
	annotatedPackages.add("com.urgoringo")
}

tasks.withType<JavaCompile>().configureEach {
	options.errorprone {
		// Disable all Error Prone checks except NullAway
		disableAllChecks.set(true)

		// Enable NullAway and treat violations as errors (fails build)
		nullaway {
			error()
		}

		// Configure NullAway options
		option("NullAway:JSpecifyMode", "true")

		// Exclude generated code from analysis (for Lombok and MapStruct)
		excludedPaths.set(".*/build/generated/.*")

		// Disable warnings in generated code
		disableWarningsInGeneratedCode.set(true)
	}
}

// Disable Error Prone for test code
tasks.named<JavaCompile>("compileTestJava") {
	options.errorprone {
		isEnabled.set(false)
	}
}
