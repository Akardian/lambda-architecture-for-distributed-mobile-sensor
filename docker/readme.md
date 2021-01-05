## Docker

This folder contains all docker compose files to start the full lambda Architecture. Additionally it contains a handful of scripts for easy startup of the architecture.

###  docker-compose folder

- **Dashboard**: 
    - docker-compose: Contains configuration to start the Dashboard "superset" and the backend software "redis" and "postgres". 
    - superset_config.py: Configuration file for "superset".
- **Druid**: 
    - docker-compose: Contains all needed instance for Druid, "postgres" and "zookeeper". 
    - storage folder: Is used for a bind of the "druid coordinator"
    - druid.env: Advanced configuration of druid.
- **hdfs**
    - docker-compose: Contains configuration for "HDFS", only the Namenode and Datanode are active. No services for the Map Reduce jobs art active.
    - hadoop.env; Advanced configuration of "HDFS".
- **Kafka**
    - docker-compose: Configuration for "kafka" with 2 listener and a "zookeeper" service. Additionally the monitoring software "kafdrop" is included.
- **portainer**
    - docker-compose: compose file for a portainer instance. This compose file is not included in any startups by default.
- **spark**:
    - docker-compose: Contains a spark master and multiple worker container.
    - spark-defaults.conf: Sets default configurations for the spark submit script. Enables the spark history server.

### Scripts

- **.env-example**: Includes important configurations for the docker up and down scripts. This files need to be copied and renamed to .env for the scripts to work.
- **docker-up.sh**: Starts the full lambda architecture and the spark history server.
- **docker-down.sh**: Stops the full lambda architecture.
- **docker-up-slim.sh**: Starts only Kafka, HDFS and Spark. It dose not Start the spark History server.
- **docker-down-slim.sh**: Stops Kafka, HDFS, Spark.
- **superset-init.sh**: Runs the command to set the password for the Dashboard "superset". Needs to be run once after the first start.
    