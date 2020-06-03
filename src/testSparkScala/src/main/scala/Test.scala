import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object Test 
{
    def main(args: Array[String]): Unit = {
    
        val i = 10
        println("My val is: " + i )

       // Create a local StreamingContext with two working thread and batch interval of 1 second.
        // The master requires 2 cores to prevent a starvation scenario. 
        val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
        val streamingContext = new StreamingContext(conf, Seconds(1))
        
        val kafkaParams = Map[String, Object](
            "bootstrap.servers" -> "localhost:9092,anotherhost:9092",
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "group.id" -> "use_a_separate_group_id_for_each_stream",
            "auto.offset.reset" -> "latest",
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )

        val topics = Array("topicA", "topicB")
        val stream = KafkaUtils.createDirectStream[String, String](
        streamingContext,
        PreferConsistent,
        Subscribe[String, String](topics, kafkaParams)
        )

        stream.map(record => (record.key, record.value))
    }
}
