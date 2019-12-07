# Apache Pulsar

- A simple client API with bindings for Java, Go, Python and C++ (https://pulsar.apache.org/docs/en/concepts-overview/)
- Seamless scalability to over a million topics.
- A serverless connector framework Pulsar IO, which is built on Pulsar Functions, makes it easier to move data in and out Apache Pulsar.


- messages will be retained by Pulsar, even if the consumer gets disconnected (https://pulsar.apache.org/docs/en/concepts-messaging/)

## Architecture Overview (https://pulsar.apache.org/docs/en/concepts-architecture-overview/)
A Pulsar instance is composed of one or more Pulsar clusters the cluster contains:
 - Brokers
 - Bookkeeper
 - Zookeeper

### Brokers
- stateless component
runs:
    - HTTP server that exposes a REST API 
    - dispatcher, which is an asynchronous TCP server for all data transfers

### Bookkeeper
Used for persistent message storage

### Zookeeper 
ZooKeeper cluster called the configuration store handles coordination tasks involving multiple clusters