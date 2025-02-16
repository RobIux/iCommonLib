import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "1.6-SNAPSHOT"
    id ("maven-publish")
	id ("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

base {
    archivesBaseName = "iCommon-Fabric"
    version = "bundle"
    group = "com.javazilla.mods"
}

dependencies {

    // 1.19.2
    //minecraft("com.mojang:minecraft:1.19.2") 
    //mappings("net.fabricmc:yarn:1.19.2+build.28:v2")
    //modImplementation("net.fabricmc:fabric-loader:0.14.9")
	
	// 1.20
    minecraft("com.mojang:minecraft:1.20.2")
    mappings("net.fabricmc:yarn:1.20.2+build.4:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
	
	// bundle jars
	include(project(":iCommon-Fabric-1.18.2"))
	include(project(":iCommon-Fabric-1.19"))
	include(project(":iCommon-Fabric-1.19.4"))
	include(project(":iCommon-Fabric-1.20.1"))
	include(project(":iCommon-Fabric-1.20.2"))
	include(project(":iCommon-Fabric-1.20.5"))
	include(project(":iCommon-Fabric-1.21"))
	include(project(":iCommon-Fabric-1.21.4"))

	annotationProcessor("com.pkware.jabel:jabel-javac-plugin:1.0.1-1")
    compileOnly("com.pkware.jabel:jabel-javac-plugin:1.0.1-1")
}

// 1.20.5 now requires JDK 21
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_21.toString() // for the IDE support
    options.release.set(16)

    javaCompiler.set(
        javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    )
}

sourceSets {
    main {
        java {
            //srcDir("${rootProject.projectDir}/iCommon-API/src/main/java/com")
            //srcDir("${rootProject.projectDir}/iCommon-Fabric-1.17/src/main/java")

            // Needs fixing for 1.18:
            exclude("me/isaiah/**/*.java")
            exclude("**/icommon.mixins.json")
            exclude("org/minecarts/**/*.java")
            
			//srcDirs = [ "src/main/java" ] 
			srcDirs("src/main/java") 
			
            //srcDir("src/main/java")
        }
        resources {
			 exclude("**/icommon.mixins.json")
        }
    }
}

/*configure([tasks.compileJava]) {
    sourceCompatibility = 16 // for the IDE support
    options.release = 8

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(16)
    }
}*/

//tasks.getByName("compileJava") {
    //sourceCompatibility = 16
    //options.release = 8
//}


tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INHERIT }

tasks.getByName<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to "1.1"
            )
        )
    }
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()
            
            pom {
                name.set(project.name.toLowerCase())
                description.set("A concise description of my library")
                url.set("http://www.example.com/")
            }

            artifact(remapJar)
        }
    }

    repositories {
        val mavenUsername: String? by project
        val mavenPassword: String? by project
        mavenPassword?.let {
            maven(url = "https://repo.codemc.io/repository/maven-releases/") {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}
