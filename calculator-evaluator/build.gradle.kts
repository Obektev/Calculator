plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":calculator-parser"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
