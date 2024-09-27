@file:Suppress("UnstableApiUsage")

rootProject.name = "packet-board"

sequenceOf("api", "implementation", "plugin").forEach {
  val kerbalProject = ":${rootProject.name}-$it"
  include(kerbalProject)
  project(kerbalProject).projectDir = file(it)
}
