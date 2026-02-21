plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("maven-publish")
}

android {
    namespace = "dev.sniffer"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

val publicationVersion: String by rootProject.extra
val publicationGroup: String by rootProject.extra

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = publicationGroup
                artifactId = "sniffer"
                version = publicationVersion
                pom {
                    name.set("Sniffer")
                    description.set(project.findProperty("POM_DESCRIPTION") as? String ?: "Sniffer Android debugging library")
                    url.set(project.findProperty("POM_URL") as? String ?: "https://github.com/debz-g/sniffer")
                    licenses {
                        license {
                            name.set(project.findProperty("POM_LICENSE_NAME") as? String ?: "GPL-3.0")
                            url.set(project.findProperty("POM_LICENSE_URL") as? String ?: "https://www.gnu.org/licenses/gpl-3.0.html")
                        }
                    }
                    scm {
                        url.set(project.findProperty("POM_SCM_URL") as? String ?: "https://github.com/debz-g/sniffer")
                        connection.set(project.findProperty("POM_SCM_CONNECTION") as? String ?: "scm:git:git://github.com/debz-g/sniffer.git")
                        developerConnection.set(project.findProperty("POM_SCM_DEV_CONNECTION") as? String ?: "scm:git:ssh://git@github.com/debz-g/sniffer.git")
                    }
                    developers {
                        developer {
                            id.set("debz-g")
                            name.set("debz-g")
                            url.set("https://github.com/debz-g")
                        }
                    }
                }
            }
        }
        repositories {
            mavenLocal()
            // Uncomment and add credentials when publishing to Maven Central:
            // maven {
            //     name = "Central"
            //     url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            //     credentials(PasswordCredentials::class)
            // }
        }
    }
}
