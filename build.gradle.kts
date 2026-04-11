import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway
import org.gradle.api.tasks.compile.GroovyCompile

plugins {
	java
	groovy
	id("org.springframework.boot") version "3.5.9"
	id("io.spring.dependency-management") version "1.1.7"
	id("net.ltgt.errorprone") version "4.3.0"
	id("net.ltgt.nullaway") version "2.3.0"
	id("dev.monosoul.jooq-docker") version "8.0.9"
	id("org.graalvm.buildtools.native") version "0.10.6"
}

group = "com.urgoringo"
version = "0.0.1-SNAPSHOT"
description = "Mealkit"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
		vendor = JvmVendorSpec.matching("GraalVM")
	}
}

// GraalVM native build tools is not Gradle 9 configuration-cache compatible;
// marking the task causes Gradle to skip config cache only for native builds
tasks.withType<org.graalvm.buildtools.gradle.tasks.GenerateResourcesConfigFile>().configureEach {
	notCompatibleWithConfigurationCache("GraalVM native build tools is not compatible with Gradle 9 configuration cache")
}

graalvmNative {
	binaries {
		named("main") {
			imageName.set("mealkit")
			mainClass.set("com.urgoringo.mealkit.MealkitApplication")
			buildArgs.add("--strict-image-heap")
			buildArgs.add("-H:+ReportExceptionStackTraces")
			javaLauncher.set(
				javaToolchains.launcherFor {
					languageVersion = JavaLanguageVersion.of(25)
					vendor = JvmVendorSpec.matching("GraalVM")
				}
			)
		}
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

//dependencyManagement {
//	imports {
//		mavenBom("io.zonky.test.postgres:embedded-postgres-binaries-bom:18.1.0")
//	}
//}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq") {
		exclude(group = "org.jooq")
	}
	implementation("org.jooq:jooq:3.20.10")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("com.github.kagkarlsson:db-scheduler-spring-boot-starter:16.6.0")
	implementation("org.jspecify:jspecify:1.0.0")
	implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
	implementation("com.github.f4b6a3:uuid-creator:6.1.1")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	errorprone("com.google.errorprone:error_prone_core:2.44.0")
	errorprone("com.uber.nullaway:nullaway:0.12.12")
	jooqCodegen("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers:2.0.2")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.2")
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.2")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:1.11.4")
    testImplementation("net.datafaker:datafaker:2.4.2")
    testImplementation("org.apache.groovy:groovy:5.0.0")
    testImplementation("org.spockframework:spock-core:2.4-M7-groovy-5.0")
    testImplementation("org.spockframework:spock-spring:2.4-M7-groovy-5.0")
    testImplementation("com.athaydes:spock-reports:2.5.1-groovy-4.0")
	testImplementation("org.awaitility:awaitility:4.3.0")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

}

tasks.withType<Test> {
	useJUnitPlatform()

	// Enable parallel test execution
//	maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
	maxParallelForks = 1

	// Show test output for Spock given/when/then blocks
	testLogging {
		events("passed", "skipped", "failed")
		showStandardStreams = false  // Disable verbose logging for cleaner output
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}
}

// Configure Groovy compilation to use Java 25 bytecode (compatible with Groovy 5.0)
tasks.withType<GroovyCompile>().configureEach {
	sourceCompatibility = "25"
	targetCompatibility = "25"
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

		// Exclude generated code from analysis (for Lombok and jOOQ)
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

// Configure jOOQ code generation using reusable Testcontainer
jooq {
	withContainer {
		image {
			name = "postgres:18.1-alpine"
		}
	}
}

tasks {
	generateJooqClasses {
		schemas.set(listOf("public"))
		basePackageName.set("com.urgoringo.mealkit.jooq")
		outputDirectory.set(project.layout.buildDirectory.dir("generated-jooq"))
	}
	
	// Make compileJava depend on jOOQ code generation
	compileJava {
		dependsOn(generateJooqClasses)
	}

	bootBuildImage {
		builder.set("paketobuildpacks/builder-jammy-tiny:latest")
		environment.set(mapOf(
			"BP_NATIVE_IMAGE" to "true",
			"BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "--strict-image-heap -H:+ReportExceptionStackTraces",
			"BP_JVM_VERSION" to "25"
		))
		imageName.set("${project.name}:${project.version}")
	}
}

// Add generated sources to the main source set
sourceSets {
	main {
		java {
			srcDir("build/generated-jooq")
		}
	}
}
