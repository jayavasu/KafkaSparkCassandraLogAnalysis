/**
 * Spark reads log file received from Kafka and finds the error lines and gets the count
 * of errors in the log file. This is very basic log file analysis project using
 * Kafka, Spark Streaming and Cassandra.
 */
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

import org.apache.spark.sql
import org.apache.spark.sql.cassandra._
import com.datastax.spark.connector._

import kafka.serializer.StringDecoder 
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

import com.datastax.driver.core.{Session, Cluster, Host, Metadata}
import com.datastax.spark.connector.streaming._
import scala.collection.JavaConversions._

object LogAnalysisKafkaSparkCassandra {

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("LogAnalysisKafkaSparkCassandra")
     
    val cluster = Cluster.builder().addContactPoint("localhost").build()
    val session = cluster.connect()
    session.execute("CREATE KEYSPACE IF NOT EXISTS LogAnalysis WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };")
    session.execute("CREATE TABLE IF NOT EXISTS LogAnalysis.log_error_count (log_type text PRIMARY KEY, count int) ")
    session.execute("TRUNCATE LogAnalysis.log_error_count")
    session.close()

    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val timer = new Thread() {
      override def run() {
        Thread.sleep(1000 * 30)
        ssc.stop()
      }
    }

    val topicsSet = Set[String] ("test")
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092")
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    
    val errors = messages.map(_._2).flatMap(_.split(" ")).filter(word => (word == "ERROR")).map(word => (word, 1L)).reduceByKey(_ + _).map({case (word,count) => (word,count)}) 

    errors.print() 
    
    errors.saveToCassandra("LogAnalysis", "log_error_count", SomeColumns("log_type" as "_1", "count" as "_2"))
    
    ssc.start() 
    timer.start() 
    ssc.awaitTermination() 
    ssc.stop() 

    val sc = new SparkContext(sparkConf)
    val csc = new org.apache.spark.sql.SQLContext(sc)
    val log_rdd = csc.sql("SELECT * from LogAnalysis.log_error_count")
    log_rdd.show(100) 
    sc.stop()
  }
}