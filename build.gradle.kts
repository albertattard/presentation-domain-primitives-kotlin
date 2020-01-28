plugins {
    kotlin("jvm") version "1.3.61"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
    id("com.github.ben-manes.versions") version "0.27.0"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    val kotlinVersion = "1.3.61"
    val junitVersion = "4.13"

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

defaultTasks("clean", "dependencyUpdates", "ktlintFormat", "test")
