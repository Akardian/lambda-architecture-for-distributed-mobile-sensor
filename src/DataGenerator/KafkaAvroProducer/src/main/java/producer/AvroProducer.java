package producer;

//import util.properties packages
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import simple producer packages
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.LongSerializer;

import producer.avro.TestData;

// Tutorial
// http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html
public class AvroProducer {
    //Configuration values
    private final static String TOPIC = "test-data-generator-input";
    private final static String BOOTSTRAP_SERVERS = "10.8.0.123:800"; //list of broker addresses "IP:Port,IP:Port"
    private final static String CLIENT_ID = "data-generator-1"; //to track the source of a requests

	public static void main(final String[] args) throws Exception {	
        TestData testData = TestData.newBuilder() //Create message to be send
             .setSenderType("data-generator")
             .setSendereName("data-generator-01")
             .setMessage("Hello Bob")
             .build();

        System.out.println(testData.getSchema().toString(true));
        System.out.println(testData.toString());

        while(true) {
            Random rand = new Random(12345);
            Random count = new Random(67890);
            for(int n = 0; n < 10; n++) {
                int time = 60;
                for(int i = 0; i < count.nextInt(25) +1; i++) {
                    int waitTime = rand.nextInt(2);
                    waitTime++;
        
                    time = time - waitTime;
                    TimeUnit.SECONDS.sleep(waitTime);
        
                    runProducer(1, testData); //Send message 5 times
                }
                TimeUnit.SECONDS.sleep(time);
            }  
        }      
	}

    private static Producer<Long, TestData> createProducer() {
        //Kafka Configuration
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS); //list of broker addresses "IP:Port,IP:Port"
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID); //to track the source of a requests
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
    
    static void runProducer(final int sendMessageCount, TestData message) throws Exception {
        final Producer<Long, TestData> producer = createProducer(); //Create a producer with configuration

        try {
            for (long index = 0; index < sendMessageCount; index++) {
                final ProducerRecord<Long, TestData> record = new ProducerRecord<>(TOPIC, message); //Create Record(Message) to send

                producer.send(record); //Send Record
            }
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
            System.exit(-1);
        } finally {
            producer.flush();
            producer.close();
        }
    }
}