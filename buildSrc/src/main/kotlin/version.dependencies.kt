
// Define libraries & versions in a single place
object Versions {
    const val support = "26.1.0"
    
    //Sdk and tools
    const val compile_sdk_version = 30
    const val target_sdk_version = 30
    const val min_sdk_version = 23
    const val build_tools_version = "30.0.1"

    const val application_id = "com.focus617.myopengldemo"
    const val version_name = "1.0"
    const val version_code = 1

    //Gradle
    const val gradle_tools = "4.0.1"

    //kotlin
    const val kotlin_version = "1.4.0"
    const val core_ktx_version = "1.3.1"
    const val coroutines_version = "1.3.9"
    const val kotlin_reflect_version = "1.4.0"

    //App dependencies
    const val appcompat_version = "1.2.0"
    const val fragment_version = "1.2.5"
    const val constraintlayout_version = "1.1.3"
    const val material_version = "1.2.1"
    const val vectordrawable_version = "1.1.0"

    // Jetpack
    const val lifecycle_version = "2.2.0"
    const val room_version = "2.2.5"
    const val navigation_version = "2.3.0"
    const val nav_saft_args_version = "2.3.0-alpha01"
    const val recyclerview_version = "1.1.0"
    const val recyclerview_selection_version = "1.1.0-rc01"

    //Third party libraries
    const val javaxAnnotations_version = "1.0"
    const val javaxInject_version = "1"
    const val google_gson_version = "2.8.5"
    const val glide_version = "4.11.0"

    const val common_lang3_version = "3.11"

    // Log
    const val timber_version = "4.7.1"
    const val kotlin_logging_version = "1.8.3"
    const val slf4j_version = "1.7.30"
    const val log4j_version = "2.13.1"

    // JGit
    const val jgit_version = "5.6.1.202002131546-r"

    // Network & Http
    const val retrofit_version = "2.7.1"
    const val moshi_version = "1.8.0"
    const val retrofit_coroutines_adapter_version = "0.9.2"
//  const val okhttp_version = "4.2.1"

    // AndroidX Test
    const val androidx_test_core_version = "1.2.0"
    const val androidx_test_core_kotlin_version = "1.2.0"
    const val androidx_test_runner_version = "1.2.0"
    const val androidx_test_rules_version = "1.2.0"
    const val androidx_arch_core_testing_version = "2.1.0"

    // Dagger 2
    const val dagger_version = "2.16"
    const val hilt_version = "2.28-alpha"

    // Assertions
    const val androidx_test_ext_junit_version = "1.1.1"
    const val androidx_test_ext_junit_kotlin_version = "1.1.1"
    const val androidx_test_ext_truth_version = "1.2.0"
    const val google_truth_version = "1.0"

    // 3rd party Testing
    const val junit_version = "4.13"
    const val mockito_version = "2.19.0"
    const val mockito_kt_version = "1.5.0"
    const val espresso_version = "3.3.0"
    const val robolectric_version = "4.3.1"
    const val kluent_version = "1.14"
    const val hamcrest_version = "1.3"
    const val uiautomator_version = "2.2.0"
}

object Dependencies {
    // classpath
    const val gradle_tools = "com.android.tools.build:gradle:${Versions.gradle_tools}"
    const val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_version}"
    const val nav_saft_arges = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.nav_saft_args_version}"
    const val hilt_gradle_plugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt_version}"

    // Kotlin
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin_version}"
    const val kotlin_stdlib_jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin_version}"
    const val kotlin_reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_reflect_version}"

    // Android KTX
    const val androidx_core_ktx = "androidx.core:core-ktx:${Versions.core_ktx_version}"

    // Coroutines
    const val kotlinx_coroutines_core =  "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines_version}"
    const val kotlinx_coroutines_android =  "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines_version}"
    const val kotlinx_coroutines_test =  "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines_version}"

    // javax
    const val jsr250_api =  "javax.annotation:jsr250-api:${Versions.javaxAnnotations_version}"
    const val javax_inject =  "javax.inject:javax.inject:${Versions.javaxInject_version}"

    // Support libraries
    const val androidx_appcompat =  "androidx.appcompat:appcompat:${Versions.appcompat_version}"
    const val androidx_fragment =  "androidx.fragment:fragment:${Versions.fragment_version}"
    const val material =  "com.google.android.material:material:${Versions.material_version}"
    const val androidx_constraintlayout =  "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout_version}"
    const val androidx_vectordrawable =  "androidx.vectordrawable:vectordrawable:${Versions.vectordrawable_version}"

    // Navigation
    const val navigation_fragment =  "androidx.navigation:navigation-fragment-ktx:${Versions.navigation_version}"
    const val navigation_ui =  "androidx.navigation:navigation-ui-ktx:${Versions.navigation_version}"

    // ViewModel
    const val lifecycle_viewmodel =  "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycle_version}"
    const val lifecycle_viewmodel_ktx =  "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_version}"
    // LiveData
    const val lifecycle_livedata =  "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycle_version}"
    // Saved state module for ViewModel
    const val lifecycle_viewmodel_savedstate =  "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle_version}"

    // Room
    const val room_runtime =  "androidx.room:room-runtime:${Versions.room_version}"
    // annotationProcessor
    const val room_compiler =   "androidx.room:room-compiler:${Versions.room_version}"
    // optional - Kotlin Extensions and Coroutines support for Room
    const val room_ktx =  "androidx.room:room-ktx:${Versions.room_version}"
    // Test helpers
    const val room_testing =  "androidx.room:room-testing:${Versions.room_version}"

    // RecyclerView
    const val recyclerview =  "androidx.recyclerview:recyclerview:${Versions.recyclerview_version}"
    // For control over item selection of both touch and mouse driven selection
    const val recyclerview_selection =  "androidx.recyclerview:recyclerview-selection:${Versions.recyclerview_selection_version}"

    // Used for Log
    const val timber =  "com.jakewharton.timber:timber:${Versions.timber_version}"
    const val kotlin_logging = "io.github.microutils:kotlin-logging:${Versions.kotlin_logging_version}"
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    const val slf4j = "org.slf4j:slf4j-simple:${Versions.slf4j_version}"

//    const val slf4j_api = "org.slf4j:slf4j-api:${Versions.slf4j_version}"
//    const val log4j_core = "org.apache.logging.log4j:log4j-core:${Versions.log4j_version}"
//    const val slf4j_over_log4j = "org.apache.logging.log4j:log4j-slf4j-impl:${Versions.log4j_version}"

    // JGit
    const val jgit = "org.eclipse.jgit:org.eclipse.jgit:${Versions.jgit_version}"

    // GSON
    const val google_gson =  "com.google.code.gson:gson:${Versions.google_gson_version}"

    // Image loading - Glide
    const val glide =  "com.github.bumptech.glide:glide:${Versions.glide_version}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide_version}"

    // Api - Retrofit (with Moshi) and OkHttp
    const val retrofit2 =  "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    //const val retrofit2_converter_scalars =  "com.squareup.retrofit2:converter-scalars:${Versions.retrofit_version}"
    const val retrofit2_converter_moshi =  "com.squareup.retrofit2:converter-moshi:${Versions.retrofit_version}"
    const val moshi =  "com.squareup.moshi:moshi:${Versions.moshi_version}"
    const val moshi_Kotlin =  "com.squareup.moshi:moshi-kotlin:${Versions.moshi_version}"
    const val retrofit2_kotlin_coroutines_adapter =  "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofit_coroutines_adapter_version}"

//    const val okhttp3 =  "com.squareup.okhttp3:okhttp:${Versions.okhttp_version}"
//    const val okhttp3_logging_interceptor =  "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp_version}"

    // Core - TodoApp: CharUtils
    const val commons_lang3 = "org.apache.commons:commons-lang3:${Versions.common_lang3_version}"

    // 2. Unit/Integration tests dependencies
    const val test_runner = "androidx.test.runner.AndroidJUnitRunner"
    const val junit =  "junit:junit:${Versions.junit_version}"
    const val kotlin_test =  "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin_version}"
    const val kotlin_test_junit =  "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin_version}"

    // Androidx JUnitRunner and JUnit Rules
    const val androidx_test_runner =  "androidx.test:runner:${Versions.androidx_test_runner_version}"
    const val androidx_test_rules =  "androidx.test:rules:${Versions.androidx_test_rules_version}"
     // AndroidX Test Core library
    const val androidx_test_core =  "androidx.test:core:${Versions.androidx_test_core_version}"
    const val androidx_test_core_ktx =  "androidx.test:core-ktx:${Versions.androidx_test_core_kotlin_version}"
    const val androidx_arch_core_testing =  "androidx.arch.core:core-testing:${Versions.androidx_arch_core_testing_version}"

    const val robolectric =  "org.robolectric:robolectric:${Versions.robolectric_version}"

    // Assertions
    const val androidx_test_ext_junit =  "androidx.test.ext:junit:${Versions.androidx_test_ext_junit_version}"
    const val androidx_test_ext_truth =  "androidx.test.ext:truth:${Versions.androidx_test_ext_truth_version}"
    const val androidx_test_ext_junit_ktx =  "androidx.test.ext:junit-ktx:${Versions.androidx_test_ext_junit_kotlin_version}"

    const val kluent =  "org.amshove.kluent:kluent:${Versions.kluent_version}"
    const val google_truth =  "com.google.truth:truth:${Versions.google_truth_version}"
    const val hamcrest_all =  "org.hamcrest:hamcrest-all:${Versions.hamcrest_version}"
    // Optional -- Hamcrest library
    const val hamcrest_library =  "org.hamcrest:hamcrest-library:${Versions.hamcrest_version}"

    // Mockito for Unit Test
    const val mockito_core =  "org.mockito:mockito-core:${Versions.mockito_version}"
    const val mockito_kt =  "com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockito_kt_version}"
    // Mockito for Android
    //const val mockito_android =  "org.mockito:mockito-android:${Versions.mockito_version}"

    // Espresso dependencies
    const val espresso_core =  "androidx.test.espresso:espresso-core:${Versions.espresso_version}"
    const val espresso_contrib =  "androidx.test.espresso:espresso-contrib:${Versions.espresso_version}"
    const val espresso_intents =  "androidx.test.espresso:espresso-intents:${Versions.espresso_version}"
    const val espresso_accessibility =  "androidx.test.espresso:espresso-accessibility:${Versions.espresso_version}"
    const val espresso_web =  "androidx.test.espresso:espresso-web:${Versions.espresso_version}"
    const val espresso_idling_resource =  "androidx.test.espresso:espresso-idling-resource:${Versions.espresso_version}"
    const val idling_concurrent =  "androidx.test.espresso.idling:idling-concurrent:${Versions.espresso_version}"

    // Dagger 2 dependencies
    const val dagger =  "com.google.dagger:dagger:${Versions.dagger_version}"
    const val dagger_compiler = "com.google.dagger:dagger-compiler:${Versions.dagger_version}"
    const val dagger_android_processor = "com.google.dagger:dagger-android-processor:${Versions.dagger_version}"

    // Hilt
    const val hilt_android = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val hilt_android_compiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"

    // Optional -- UI testing with UI Automator
    const val uiautomator =  "androidx.test.uiautomator:uiautomator:${Versions.uiautomator_version}"

}