// Play
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.5")

// ScalikeJDBC
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.0.1")

// SBT eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.0.1")