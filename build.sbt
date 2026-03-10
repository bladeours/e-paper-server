import com.typesafe.sbt.packager.MappingsHelper.directory
import com.typesafe.sbt.packager.docker.*

name := """e-paper-server"""

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.7.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies += "com.microsoft.playwright" % "playwright" % "1.40.0"
libraryDependencies += "net.sf.biweekly" % "biweekly" % "0.6.8"


Docker / daemonUserUid  := Some("1000")
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown
dockerUpdateLatest := true
dockerExposedPorts ++= Seq(9000, 9001)
dockerEnvVars := Map(
  "PLAYWRIGHT_BROWSERS_PATH" -> "/ms-playwright"
)
dockerBaseImage := "eclipse-temurin:21-jdk-jammy"

Docker / mappings ++= {
  val publicDir = baseDirectory.value / "public" / "stylesheets"
  publicDir.listFiles().map { file =>
    file -> s"/opt/docker/public/stylesheets/${file.getName}"
  }.toSeq
}

Docker / mappings ++= {
  val publicDir = baseDirectory.value / "public" / "javascripts"
  publicDir.listFiles().map { file =>
    file -> s"/opt/docker/public/javascripts/${file.getName}"
  }.toSeq
}

Docker / mappings ++= {
  val publicDir = baseDirectory.value / "public" / "images"
  publicDir.listFiles().map { file =>
    file -> s"/opt/docker/public/images/${file.getName}"
  }.toSeq
}

dockerCommands ++= Seq(
  Cmd("USER", "root"),
  Cmd("RUN", """java -cp "/opt/docker/lib/*" com.microsoft.playwright.CLI install-deps chromium"""),
  Cmd("RUN", """java -cp "/opt/docker/lib/*" com.microsoft.playwright.CLI install chromium"""),
  Cmd("RUN", "mkdir /ms-playwright/tmp"),
  Cmd("RUN", "chown -R 1000:1000 /ms-playwright"),
  Cmd("USER", "1000:1000")
)