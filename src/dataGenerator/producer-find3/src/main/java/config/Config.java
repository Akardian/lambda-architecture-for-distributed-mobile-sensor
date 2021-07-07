package config;

public interface Config {
    //Kafka Configuration
    public final static String TOPIC = "find3Streaming-test-input";
    public final static String BOOTSTRAP_SERVERS = "10.8.0.1:9093"; //list of broker addresses "IP:Port,IP:Port"
    public final static String CLIENT_ID = "Data01"; //to track the source of a requests

    public final static int RANDOM_SEED = 123456789;
    public final static int RANDOM_BOUND = 60;
    public final static int RANDOM_OFFSET = 30;

    public final static int SET_SIZE = 1000;
    public final static int SET_REPEATS = 10;

    public final static int SLEEP_SECONDS = 10;
    public final static int SLEEP_MILLISECONDS = SLEEP_SECONDS * 1000;

    public final static String[] ACCESPOINTS = {
        "bc:26:c7:40:90:ee", "bc:26:c7:40:90:ec", "bc:26:c7:7c:cd:23",
        "bc:26:c7:40:90:ed", "bc:26:c7:7c:cd:22", "bc:26:c7:7c:cd:21",
        "14:59:c0:20:39:80", "bc:26:c7:7c:cd:2e", "bc:26:c7:7c:cd:2d",
        "bc:26:c7:40:90:e3", "bc:26:c7:7c:cd:2c", "bc:26:c7:7c:cd:4e",
        "bc:26:c7:7c:cd:4d", "bc:26:c7:40:90:e1", "bc:26:c7:40:90:e2",
        "cc:ce:1e:ae:39:be", "cc:ce:1e:ae:39:bf", "a0:cf:5b:69:6b:f0"
    };
}
