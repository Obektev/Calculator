plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":calculator-developers-api"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
