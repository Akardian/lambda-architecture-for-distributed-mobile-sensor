# lambda-architecture-for-distributed-mobile-sensor
Lambda architecture for distributed mobile sensor

Introduction and Goals
====================
In "Internet of Things" setups large amounts of data are generate from sensors and communication. The large amount of data results in a high complexity which makes the data hard to handle and complex to analyse. Nathan Marz developed the lambda architecture to reduce the rising complexity. The lambda architecture duplicates the incoming data onto 2 path. The Batch layer and speed layer, this reduces the complexity of each path. The architecture is scalable and distributed which makes a good messenger important. We compared the wide spread Apache Kafka with new Apache Pulsar to find the differences between the messengers. 
(TODO: Redo)

Requirements Overview  
-----------------------
     
- Batch layer
- Speed layer
- Serving layer
- Dashboard
- Test data generator

Stakeholders
------------

| Role/Name       | Description                         |
|-----------------|-------------------------------------|
| Mike Wüstenberg | Documentation, Research, Programing |

<sub>Table 1. Stakeholder</sub>

System Scope and Context
========================

Business Context
----------------

![](https://github.com/Akardian/lambda-architecture-for-distributed-mobile-sensor/blob/master/images/1-1BusinessContext.png?raw=true)

<sub>Figure 1. Business Context</sub>

| Component           | Explanation                                       |
|---------------------|---------------------------------------------------|
| Loomo               | Provides sonsore data such as Odometry data       |
| Test Data Generator | Generates test data for testing and experiment's  |
| Find3               | Provides WiFi signal data such as signal strength |
| Lambda Architecktue | Stores and processes the collected data           |
| Grafana Dashboard   | Visualisation of the data                         |

<sub>Table 3. Business Context</sub>

Technical Context    
---------------------

![](https://raw.githubusercontent.com/Akardian/lambda-architecture-for-distributed-mobile-sensor/master/images/2-1TechnicalContext.png)

<sub>Figure 2. Technical Context</sub>

| Component           | Input                                    | Output                               |
|---------------------|------------------------------------------|--------------------------------------|
| Loomo               | none                                     | Generates Sensore data               |
| Test Device         | none                                     | Generates Test data                  |
| Messaging Service   | Raw Sensore data from Loomo, Test Device | Raw Sensore data to Streaming layer  |
|                     | Transformed data from Streaming Layer    | Transformed data to Serving Layer    |
| Streaming Layer     | Raw Sensore Data from Messaging Service  | Saves data to HDFS                   |
|                     | none                                     | Tranformed data to Messaging Service |
| Master Data Storage | Raw Data from Streaming Layer            | Archived data to Batch Layer         |
| Batch Layer         | Archived Date from Master Data Storage   | Transformed data to Serving layer    |
| Grafana             | Processed data stored in Druid           | Dashboard                            |

<sub>Table 4. Technical Context</sub>

Solution Strategy
=================

Lambda Architecture
![](https://raw.githubusercontent.com/Akardian/lambda-architecture-for-distributed-mobile-sensor/master/images/3-1LambdaArchitecture.png)

<sub>Figure 3-1. Lambda Architecture Clean</sub>

![](https://raw.githubusercontent.com/Akardian/lambda-architecture-for-distributed-mobile-sensor/master/images/3-2LambdaArchitectureTechnollogy.png)

<sub>Figure 3-2. Lambda Architecture Technology </sub>

Technologies
- Input
    - Apache Kafka: Transmission of Data
- Batch Layer
    - Hadoop HDFS: Persistent distributed data storage
    - Spark
- Speed Layer
    - Spark Streaming
- Serving Layer
    - Apache Druid
- Visualisation
    - Grafana Dashboard

Building Block View
===================

todo: spark-streaming, spark-batch, spark-experiment
Folder structure
- src
    - dataGenerator
        - producer-find3
    - spark


Port View  
-----------
          
(todo)


### producer-find3

##### Purpose
This Java programs purpose is to generate consistent test data and send it via Kafka to the Lambda architecture. The test data is generated in random generate batches but each batch will look the same.
The size and and number of batches send can be configured inside the program.
The data is serialised into a "Avro" format and send to a "Kafka" service.

![](https://raw.githubusercontent.com/Akardian/lambda-architecture-for-distributed-mobile-sensor/master/images/4-1BlockviewProducerFind3.png)

<sub>Figure 4-1. Producer-Find3 Blockview</sub>

| Component      | Purpose                                                         |
|----------------|-----------------------------------------------------------------|
| ProducerFIND3  | Build batch test data and send them to kafka                    |
| Config         | Configuration of Kafka connection address, batch size and more. |
| KafkaUtil      | Utility methods for data generation                             |
| AvroSerializer | Serializer for Java object into Avro byte stream                |

<sub>Table 5. Producer-Find3 Blockview</sub>

##### Avro schema

```json
{
"type": "record",
"name": "AvroFIND3Data",
"namespace": "app.model.avro.generated",
"fields": [
    {"name": "senderName", "type": "string", "doc": "Name of the sender"},
    {"name": "location", "type": "string", "doc": "Location of the device based on wifi data"},
    {"name": "findTimestamp", "type": "string", "doc": "Timestamp of data entry"},
    {"name": "odomData", "type": { "type":"array", "items": "string"},"default": []},
    {"name": "wifiData", "type": {"type": "map", "values": "int"}, "default": {}}
  ]
}
```

##### Data Generation

senderName: fixed name(Data01)
location: fixed location(r287)
findTimestamp: current Timestamp in epoch second format
odomData: Json string containing 4 entrys of odometry data. Each entry contains x,y,z for position and x,y,z,w for its rotation. The rotation is a fixed double value. The position are randomly generated.
    1. position between: x(00-10) and y(00-10)
    2. position between: x(10-20) and y(00-10)
    3. position between: x(10-20) and y(10-20)
    4. position between: x(10-20) and y(0-10)
wifiData: List of 18 access points with a double value between -30 and -90

### \<Name black box 2\> {#__name_black_box_2}

*\<black box template\>*

### \<Name black box n\> {#__name_black_box_n}

*\<black box template\>*

### \<Name interface 1\> {#__name_interface_1}

...

### \<Name interface m\> {#__name_interface_m}

Runtime View {#section-runtime-view}
============

\<Runtime Scenario 1\> {#__runtime_scenario_1}
----------------------

-   *\<insert runtime diagram or textual description of the scenario\>*

-   *\<insert description of the notable aspects of the interactions
    between the building block instances depicted in this diagram.\>*

\<Runtime Scenario 2\> {#__runtime_scenario_2}
----------------------

... {#_}
---

\<Runtime Scenario n\> {#__runtime_scenario_n}
----------------------

Deployment View {#section-deployment-view}
===============

Infrastructure Level 1 {#_infrastructure_level_1}
----------------------

***\<Overview Diagram\>***

Motivation

:   *\<explanation in text form\>*

Quality and/or Performance Features

:   *\<explanation in text form\>*

Mapping of Building Blocks to Infrastructure

:   *\<description of the mapping\>*

Infrastructure Level 2 {#_infrastructure_level_2}
----------------------

### *\<Infrastructure Element 1\>* {#__emphasis_infrastructure_element_1_emphasis}

*\<diagram + explanation\>*

### *\<Infrastructure Element 2\>* {#__emphasis_infrastructure_element_2_emphasis}

*\<diagram + explanation\>*

...

### *\<Infrastructure Element n\>* {#__emphasis_infrastructure_element_n_emphasis}

*\<diagram + explanation\>*

Cross-cutting Concepts {#section-concepts}
======================

*\<Concept 1\>* {#__emphasis_concept_1_emphasis}
---------------

*\<explanation\>*

*\<Concept 2\>* {#__emphasis_concept_2_emphasis}
---------------

*\<explanation\>*

...

*\<Concept n\>* {#__emphasis_concept_n_emphasis}
---------------

*\<explanation\>*

Design Decisions {#section-design-decisions}
================

Quality Requirements {#section-quality-scenarios}
====================

Quality Tree {#_quality_tree}
------------

Quality Scenarios {#_quality_scenarios}
-----------------

Risks and Technical Debts {#section-technical-risks}
=========================

Glossary {#section-glossary}
========

+-----------------------------------+-----------------------------------+
| Term                              | Definition                        |
+===================================+===================================+
| \<Term-1\>                        | \<definition-1\>                  |
+-----------------------------------+-----------------------------------+
| \<Term-2\>                        | \<definition-2\>                  |
+-----------------------------------+-----------------------------------+

**About arc42**

arc42, the Template for documentation of software and system
architecture.

By Dr. Gernot Starke, Dr. Peter Hruschka and contributors.

Template Revision: 7.0 EN (based on asciidoc), January 2017

© We acknowledge that this document uses material from the arc 42
architecture template, <http://www.arc42.de>. Created by Dr. Peter
Hruschka & Dr. Gernot Starke.