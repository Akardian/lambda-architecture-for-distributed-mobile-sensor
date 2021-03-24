package producer;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Producer;

import app.model.avro.generated.AvroFIND3Data;
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
				AvroFIND3Data testData = buildMessage(CLIENT_ID, "r287", Long.toString(Instant.now().getEpochSecond()), rand);
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
		HashMap<String, Integer> wifiDataMap = new HashMap<String,Integer>();
		for(int i = 0; i < ACCESPOINTS.length; i++) {
			wifiDataMap.put(ACCESPOINTS[i], 0 - (rand.nextInt(RANDOM_BOUND)+RANDOM_OFFSET));
		}

		ArrayList<String> odomDataList = new ArrayList<String>();

		odomDataList.add("{\"position\":{\"x\":-0.0130076939240098,\"y\":-0.08033571392297745,\"z\":0.0},\"orientation\":{\"x\":0.005217432044446468,\"y\":-0.0005960796843282878,\"z\":0.3365990221500397,\"w\":0.941633403301239}}");
		odomDataList.add("{\"position\":{\"x\":-0.0097260857000947,\"y\":-0.07754592597484589,\"z\":0.0},\"orientation\":{\"x\":0.005673218052834272,\"y\":-0.001788094057701528,\"z\":0.3537658751010895,\"w\":0.9353150725364685}}");
		odomDataList.add("{\"position\":{\"x\":-0.004763695877045393,\"y\":-0.07332725077867508,\"z\":0.0},\"orientation\":{\"x\":0.005788087844848633,\"y\":-0.0015548872761428356,\"z\":0.340519517660141,\"w\":0.9402183294296265}}");
		odomDataList.add("{\"position\":{\"x\":-0.004763695877045393,\"y\":-0.07332725077867508,\"z\":0.0},\"orientation\":{\"x\":0.005560061428695917,\"y\":-0.001257477910257876,\"z\":0.34051811695098877,\"w\":0.9402206540107727}}");
		odomDataList.add("{\"position\":{\"x\":-0.004763695877045393,\"y\":-0.07332725077867508,\"z\":0.0},\"orientation\":{\"x\":0.005040878430008888,\"y\":0.0001761785097187385,\"z\":0.3405124545097351,\"w\":0.9402264952659607}}");

        AvroFIND3Data testData = AvroFIND3Data.newBuilder() //Create message to be send
            .setSenderName(senderName)
            .setLocation(location)
            .setFindTimestamp(timeStamp)
            .setOdomData(odomDataList)
            .setWifiData(wifiDataMap)
            .build();

        return testData;
    }
}