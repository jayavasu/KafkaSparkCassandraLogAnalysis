# LogAnalysisKafkaSparkCassandra
This is a very simple scala project using Apache Spark Streaming to accept log files from Kafka and write a logging summary to Cassandra.
This project expects with single node kafka, cassandra and spark configurations.

This application does the following:

Connects directly from the Spark driver to Cassandra, creates a keyspace and table to store results.
It starts a Spark streaming session and connects it to Kafka. Summarize messages received in each 5 second period by finding word ERROR. 
Saves the result in Cassandra.
Stops the streaming session after 30 seconds.
Uses Spark SQL to connect to Cassandra and extract the summary results table data that has been saved.

This sample has been built with the following versions:
- Scala 2.11.8
- Kafka 2.9.1-0.8.2.1
- Spark 2.1.0
- Spark Cassandra Connector Assembly 1.6.4
- Cassandra 2.1.5

To build this:

Clone the repository:
cd ~
git clone https://github.com/jayavasu/KafkaSparkCassandraLogAnalysis.git
Build the project:
cd KafkaSparkCassandraLogAnalysis
sbt assembly

It requires you to create a topic called "test" in kafka
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test


Publish any log file like the following in a kafka console.
kafka-console-producer.sh --broker-list localhost:9092 --topic test > controller.log


Run the sample

Open another console window, submit your Spark job:
cd ~/LogAnalysisKafkaSparkCassandra
~/spark-2.0.0-bin-hadoop2.7/bin/spark-submit --properties-file spark.conf --class LogAnalysisKafkaSparkCassandra target/scala-2.11/spark-cassandra-connector-assembly-1.6.4.jar

In cassandra you can see the results in LogAnalysis.log_error_count and also the result will be printed in the spark console.