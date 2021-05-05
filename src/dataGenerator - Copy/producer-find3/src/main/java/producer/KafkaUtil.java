package producer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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

    public static String createJsonString(double px, double py, double pz, double ox, double oy, double oz, double ow, long seq, long secs, long nsecs, String frame_id) {
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

    public static AvroFIND3Data buildMessage(String senderName, String location, String timeStamp, Random rand, long seq) throws InterruptedException{
		HashMap<String, Integer> wifiDataMap = new HashMap<String,Integer>();
		for(int i = 0; i < ACCESPOINTS.length; i++) {
			wifiDataMap.put(ACCESPOINTS[i], 0 - (rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET));
		}

		//Generate Odometry data
		ArrayList<String> odomDataList = new ArrayList<String>();

		var random = ThreadLocalRandom.current();
		Instant now = Instant.now();
		odomDataList.add(KafkaUtil.createJsonString(
			random.nextDouble(0.0, 10.0), random.nextDouble(0.0, 10.0), 0.0, 
			0.008384221233427525, 0.004507092293351889, 0.03855949640274048, 0.9992109537124634, 
			seq, now.getEpochSecond(), now.getNano(), "base_link"));
		seq++;
		TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS / 4);

		now = Instant.now();
		odomDataList.add(KafkaUtil.createJsonString(
			random.nextDouble(20.0, 30.0), random.nextDouble(0.0, 10.0), 0.0, 
			0.0083421990275383, 0.004321090877056122, 0.03856131061911583, 0.9992120862007141,
			seq, now.getEpochSecond(), now.getNano(), "base_link"));
		seq++;
		TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS / 4);

		now = Instant.now();
		odomDataList.add(KafkaUtil.createJsonString(
			random.nextDouble(20.0, 30.0), random.nextDouble(20.0, 30.0), 0.0, 
			0.008235296234488487, 0.0042941151186823845, 0.03856196999549866, 0.9992130398750305, 
			seq, now.getEpochSecond(), now.getNano(), "base_link"));
		seq++;
		TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS / 4);

		now = Instant.now();
		odomDataList.add(KafkaUtil.createJsonString(
			random.nextDouble(0.0, 10.0), random.nextDouble(20.0, 30.0), 0.0,
			0.00830506905913353, 0.004295618738979101, 0.038561683148145676, 0.9992125034332275, 
			seq, now.getEpochSecond(), now.getNano(), "base_link"));
		seq++;
		TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS / 4);

		//Create message to be send
        AvroFIND3Data testData = AvroFIND3Data.newBuilder() 
            .setSenderName(senderName)
            .setLocation(location)
            .setFindTimestamp(timeStamp)
            .setOdomData(odomDataList)
            .setWifiData(wifiDataMap)
            .build();

        return testData;
    }
}