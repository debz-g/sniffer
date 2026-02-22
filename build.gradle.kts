plugins {
    id("com.android.library") version "8.8.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

// Load publish/signing secrets from local.properties (not committed) so they are available as project properties
val localPublishProperties = java.util.Properties()
rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { localPublishProperties.load(it) }
listOf("SONATYPE_USERNAME", "SONATYPE_PASSWORD", "signing.keyId", "signing.password", "signing.secretKeyRingFile").forEach { key ->
    localPublishProperties.getProperty(key)?.let { value -> rootProject.ext.set(key, value) }
}
subprojects {
    listOf("SONATYPE_USERNAME", "SONATYPE_PASSWORD", "signing.keyId", "signing.password", "signing.secretKeyRingFile").forEach { key ->
        rootProject.findProperty(key)?.let { value -> project.ext.set(key, value.toString()) }
    }
}

group = project.findProperty("GROUP")?.toString() ?: "dev.sniffer"
version = project.findProperty("VERSION_NAME")?.toString() ?: "1.0.0"

// Publish coordinates for :sniffer (read by sniffer/build.gradle.kts)
extra["publicationVersion"] = version
extra["publicationGroup"] = group

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(project.findProperty("SONATYPE_USERNAME")?.toString() ?: System.getenv("SONATYPE_USERNAME") ?: "")
            password.set(project.findProperty("SONATYPE_PASSWORD")?.toString() ?: System.getenv("SONATYPE_PASSWORD") ?: "")
        }
    }
}

tasks.register("publishToMavenLocal") {
    group = "publishing"
    description = "Publishes Sniffer to Maven Local (~/.m2/repository)"
    dependsOn(":sniffer:publishReleasePublicationToMavenLocal")
}

// Publish to Sonatype staging, then close so it appears in Central Portal. Then release at https://central.sonatype.com/publishing
tasks.register("publishToMavenCentral") {
    group = "publishing"
    description = "Publish to Sonatype, close staging repo. Then release at https://central.sonatype.com/publishing"
    dependsOn(
        ":sniffer:publishReleasePublicationToSonatypeRepository",
        "closeSonatypeStagingRepository"
    )
}
