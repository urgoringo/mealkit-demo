import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway
import org.gradle.api.tasks.compile.GroovyCompile
import org.jooq.meta.jaxb.Logging

plugins {
	java
	groovy
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("net.ltgt.errorprone") version "4.3.0"
	id("net.ltgt.nullaway") version "2.3.0"
	id("org.jooq.jooq-codegen-gradle") version "3.19.27"
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
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jspecify:jspecify:1.0.0")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	errorprone("com.google.errorprone:error_prone_core:2.44.0")
	errorprone("com.uber.nullaway:nullaway:0.12.12")
	jooqCodegen("org.postgresql:postgresql")
	jooqCodegen("org.jooq:jooq-meta-extensions:3.19.27")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers:2.0.2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.cucumber:cucumber-java:7.21.1")
    testImplementation("io.cucumber:cucumber-spring:7.21.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.21.1")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:1.11.4")
    testImplementation("net.datafaker:datafaker:2.4.2")
    testImplementation("org.apache.groovy:groovy:5.0.0")
    testImplementation("org.spockframework:spock-core:2.4-M7-groovy-5.0")
    testImplementation("org.spockframework:spock-spring:2.4-M7-groovy-5.0")
    testImplementation("com.athaydes:spock-reports:2.5.1-groovy-4.0")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

}

tasks.withType<Test> {
	useJUnitPlatform()
	
	// Show test output for Spock given/when/then blocks
	testLogging {
		events("passed", "skipped", "failed")
		showStandardStreams = true
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

// jOOQ code generation configuration
jooq {
	configuration {
		logging = Logging.WARN
		generator {
			name = "org.jooq.codegen.JavaGenerator"
			database {
				name = "org.jooq.meta.extensions.ddl.DDLDatabase"
				properties {
					property {
						key = "scripts"
						value = "src/main/resources/db/migration/*.sql"
					}
					property {
						key = "sort"
						value = "flyway"
					}
					property {
						key = "defaultNameCase"
						value = "lower"
					}
				}
			}
			generate {
				isDeprecated = false
				isRecords = true
				isImmutablePojos = false
				isFluentSetters = true
				isPojos = true
				isPojosEqualsAndHashCode = true
				isPojosToString = true
				isJavaTimeTypes = true
			}
			target {
				packageName = "com.urgoringo.mealkit.jooq"
				directory = "build/generated/sources/jooq"
			}
		}
	}
}

// Make sure jOOQ code generation runs before compilation
tasks.named("compileJava") {
	dependsOn(tasks.named("jooqCodegen"))
}

// Add generated sources to the main source set
sourceSets {
	main {
		java {
			srcDir("build/generated/sources/jooq")
		}
	}
}
