name := "kafka-log-cassandra-streaming"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.0" % "provided"

libraryDependencies += "com.datastax.spark" % "spark-cassandra-connector_2.10" % "1.6.4"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.0" % "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1"

libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.8.2.1"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.8.2.1"

libraryDependencies += "log4j" % "log4j" % "1.2.14"

libraryDependencies += "com.yammer.metrics" % "metrics-core" % "2.2.0"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.4"

