import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.spring)
	alias(libs.plugins.spring.boot)
}

group = "io.cloudflight"
version = libs.versions.springBootAdmin.get()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
		jvmTarget = JvmTarget.JVM_17
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(platform(libs.spring.boot.bom))
	implementation(platform(libs.spring.boot.admin.bom))
	implementation(platform(libs.spring.cloud.bom))
	implementation(libs.spring.boot.admin.starter.server)
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.actuator)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.oauth2.client)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.kotlin.reflect)
	implementation(libs.spring.cloud.starter.kubernetes.client.all)

	testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
    filter {
        includeTestsMatching("*Test")
        isFailOnNoMatchingTests = false
    }
}

tasks.named("jar") {
	enabled = false
}
