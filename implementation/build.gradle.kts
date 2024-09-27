plugins {
  alias(libs.plugins.paperweight)
}

dependencies {
  api(project(":packet-board-api"))

  paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}
