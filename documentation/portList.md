Hadoop
    Namenode WebUI:
        Target: 9870
        published: 9870
        
    Datanode WebUI:
        Target: 9864
        published: 9864

Kafka
    kafdrop WebUI:
        target: 9000
        published: 8000
            
    kafka01:
        Data Docker Intern
        - target: 9092 
          published: 9092
        Data Docker extern
        - target: 29092
          published: 29092
     
Spark     
    spark-master:
        WebUI
        - target: 8080
          published: 8080
        Service Port
        - target: 7077
          published: 7077
              
    spark-worker:
        WebUI
        - target: 8081
          published: 8081
Druid
    coordinator:
        
        - target: 8081
          published: 5000
          
    broker:

        - target: 8082
          published: 5100
          
    router:

        - target: 8888
          published: 5200
          
    middlemanager:

        - target: 8091
          published: 5300
      
    historical:

        - target: 8083
          published: 5400

Superset
    superset:
        WebUI
        - target: 8088
          published: 8088