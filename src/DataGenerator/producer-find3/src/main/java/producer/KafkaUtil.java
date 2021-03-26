package producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.LongSerializer;

import app.model.avro.generated.AvroFIND3Data;

import config.Config;

public class KafkaUtil implements Config{
    public static Producer<Long, AvroFIND3Data> createProducer() {
        //Kafka Configuration
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS); //list of broker addresses "IP:Port,IP:Port"
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID); //to track the source of a requests
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
    
    public static void send(Producer<Long, AvroFIND3Data> producer, AvroFIND3Data message) throws Exception {
        try {
            //Create Record(Message) to send
            final ProducerRecord<Long, AvroFIND3Data> record = new ProducerRecord<>(TOPIC, message); 
            //Send Record 
            producer.send(record);
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
            System.exit(-1);
        } finally {

        }
    }

    public static String createJsonString(double px, double py, double pz, double ox, double oy, double oz, double ow, double seq, long secs, long nsecs, String frame_id) {
        String json = 
            "{"+
                "\"pose\":{" +
				    "\"position\":{" +
					    "\"x\":" + px + "," +
                        "\"y\":" + py + "," +
                        "\"z\":" + pz + "" +
                    "}," + 
				    "\"orientation\":{" + 
				        "\"x\":" + ox + "," +
                        "\"y\":" + oy + "," +
                        "\"z\":" + oz + "," +
                        "\"w\":" + ow + "" +
                    "}" +
                "}," +
			    "\"header\":{" +
				    "\"seq\":" + seq + "," +
				    "\"stamp\":{" +
					    "\"secs\":" + secs + "," +
                        "\"nsecs\":" + nsecs + "" +
                    "}," +
				    "\"frame_id\":\"" + frame_id + "\"" +
			    "}" +
            "}";
        return json;
    }
}