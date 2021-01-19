package producer;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Producer;

import app.model.avro.generated.AvroFIND3Data;
import app.model.avro.generated.AvroGpsCoordinate;
import app.model.avro.generated.AvroWifiData;
import config.Config;

public class ProducerFIND3 implements Config {

	public static void main(final String[] args) throws Exception {
		Producer<Long, AvroFIND3Data> producer = KafkaUtil.createProducer();
		Random rand = new Random(RANDOM_SEED);

		Timestamp startTime = new Timestamp(System.currentTimeMillis());
		long messageCount = 0;
		long repeat = 0;

		System.out.println("## Send find3 data");
		System.out.println("Set Size: " + SET_SIZE);
		System.out.println("Set repeats: " + SET_REPEATS + "\n");
		
		System.out.println("Random seed: " + RANDOM_SEED);
		System.out.println("Random bound: " + RANDOM_BOUND);
		System.out.println("Random offset: " + RANDOM_OFFSET + "\n");

		System.out.println("Sleep time: " + SLEEP_SECONDS + "\n");

		for(int r = 0; r < SET_REPEATS; r++) {
			repeat++;
			System.out.format("%05d-%010d: Sending Message..", repeat, messageCount);
			rand = new Random(RANDOM_SEED);

			for(int s = 0; s < SET_SIZE; s++) {
				AvroFIND3Data testData = buildMessage(CLIENT_ID, "r287", Long.toString(System.currentTimeMillis()), rand);
				try {	
					KafkaUtil.send(producer, testData);
	
					TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
					messageCount++;	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(". send");
		}

		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		long uptime = endTime.getTime() - startTime.getTime();
		
		long days = TimeUnit.MILLISECONDS.toDays(uptime);
		uptime -= TimeUnit.DAYS.toMillis(days);

		long hours = TimeUnit.MILLISECONDS.toHours(uptime);
		uptime -= TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
		uptime -= TimeUnit.SECONDS.toMillis(seconds);

		System.out.println("\nMessages sends: " + messageCount);
		System.out.println("Repeats done:" + repeat);
		System.out.println("Start time: " + startTime);
		System.out.println("End time: " + endTime);
		System.out.println("Run time: " + days + "D: " + hours + "H:" + minutes + "M:" + seconds + "S:" + uptime + "MI");

		producer.flush();
		producer.close();
	}
    
    private static AvroFIND3Data buildMessage(String senderName, String location, String timeStamp, Random rand){
        AvroGpsCoordinate gps = AvroGpsCoordinate.newBuilder()
            .setLat("53.557857990317935")
            .setLon("10.023013328928275")
            .setAlt("62.60000228881836")
            .build();

            
        AvroWifiData wifi = AvroWifiData.newBuilder()
            .setWifiData(Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:40:90:ee", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:40:90:ec", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:23", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:40:90:ed", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:22", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:21", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("14:59:c0:20:39:80", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:2e", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:2d", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:40:90:e3", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:2c", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:4e", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:7c:cd:4d", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:40:90:e1", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("bc:26:c7:40:90:e2", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("cc:ce:1e:ae:39:be", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("cc:ce:1e:ae:39:bf", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET),
                    new AbstractMap.SimpleEntry<String, Integer>("a0:cf:5b:69:6b:f0", -rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET)
                )                   
            )
            .build();

        AvroFIND3Data testData = AvroFIND3Data.newBuilder() //Create message to be send
            .setSenderName(senderName)
            .setLocation(location)
            .setFindTimestamp(timeStamp)
            .setGpsCoordinate(gps)
            .setWifiData(wifi)
            .build();

        return testData;
    }
}