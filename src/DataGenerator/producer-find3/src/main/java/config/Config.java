package config;

public interface Config {
    //Kafka Configuration
    public final static String TOPIC = "find3Generator-input";
    public final static String BOOTSTRAP_SERVERS = "10.8.0.1:9093"; //list of broker addresses "IP:Port,IP:Port"
    public final static String CLIENT_ID = "Data01"; //to track the source of a requests

    public final static int RANDOM_SEED = 123456789;
    public final static int RANDOM_BOUND = 6;
    public final static int RANDOM_OFFSET = 30;

    public final static int SET_SIZE = 1;
    public final static int SET_REPEATS = 1;

    public final static int SLEEP_SECONDS = 2;
}
