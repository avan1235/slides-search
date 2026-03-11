plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    dependencies {
        api(projects.core)

        implementation(libs.apache.poi.ooxml)
    }
}
