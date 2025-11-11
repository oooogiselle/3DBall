plugins {//#plugins# specify plugin versions here
  kotlin("multiplatform") version "2.2.0" apply false
  kotlin("plugin.serialization") version "2.2.0" apply false
}

allprojects {//#allprojects# setting for all subprojects
  group = "vision.gears"
  version = "1.0"

  repositories {//#repositories# fetch dependencies from here
    mavenCentral()
    maven{
      url = uri("https://repo1.maven.org/maven2")
    }
  }

}

project("client").layout.buildDirectory.set(File("../build"))