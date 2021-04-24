package producer;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Producer;

import app.model.avro.generated.AvroFIND3Data;
import config.Config;

public class ProducerFIND3 implements Config {

	public static long seq = 0;
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
				AvroFIND3Data testData = buildMessage(CLIENT_ID, "r287", Long.toString(Instant.now().getEpochSecond()), rand);
				try {	
					KafkaUtil.send(producer, testData);
	
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
}