## src

This folder contains all source files.

###  dataGenerator folder

This folder contains all projects which are connected to data generation or sending data to the lambda architecture.

- **producer-avro**: This is an example producer the send avro encoded data to Kafka.
- **producer-find3**: This project generates find3 data and Robot Operating System(ROS) Odometry it's main purpose is for testing. 
-  **producer-json**: This is an example producer the send json encoded data to Kafka.
-  **testKafkaConsumer**: This is an example Kafka consumer.
-  **testKafkaProducer**: This is an example Kafka producer.

### spark folder

- **sparkFind3Streaming**: Spark streaming project to read live "ros" and find3 data from Kafka and process it.
- **sparkFind3Batch**: The batch version for the "ros" and find3 data
- **sparkWordcount**: Spark word count example.

    