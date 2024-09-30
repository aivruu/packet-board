tasks {
  processResources {
    filesMatching("paper-plugin.yml") {
      expand("version" to project.version)
    }
  }
}

dependencies {
  api(project(":packet-board-api"))
  api(project(":packet-board-implementation"))

  compileOnly(libs.paper)
  compileOnly(libs.configurate)
  compileOnly(libs.placeholderapi)
  compileOnly(libs.luckperms)
}
