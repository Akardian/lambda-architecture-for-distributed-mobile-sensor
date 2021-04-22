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

Building Block View {#section-building-block-view}
===================

Whitebox Overall System {#_whitebox_overall_system}
-----------------------

***\<Overview Diagram\>***

Motivation

:   *\<text explanation\>*

Contained Building Blocks

:   *\<Description of contained building block (black boxes)\>*

Important Interfaces

:   *\<Description of important interfaces\>*

### \<Name black box 1\> {#__name_black_box_1}

*\<Purpose/Responsibility\>*

*\<Interface(s)\>*

*\<(Optional) Quality/Performance Characteristics\>*

*\<(Optional) Directory/File Location\>*

*\<(Optional) Fulfilled Requirements\>*

*\<(optional) Open Issues/Problems/Risks\>*

### \<Name black box 2\> {#__name_black_box_2}

*\<black box template\>*

### \<Name black box n\> {#__name_black_box_n}

*\<black box template\>*

### \<Name interface 1\> {#__name_interface_1}

...

### \<Name interface m\> {#__name_interface_m}

Level 2 {#_level_2}
-------

### White Box *\<building block 1\>* {#_white_box_emphasis_building_block_1_emphasis}

*\<white box template\>*

### White Box *\<building block 2\>* {#_white_box_emphasis_building_block_2_emphasis}

*\<white box template\>*

...

### White Box *\<building block m\>* {#_white_box_emphasis_building_block_m_emphasis}

*\<white box template\>*

Level 3 {#_level_3}
-------

### White Box \<\_building block x.1\_\> {#_white_box_building_block_x_1}

*\<white box template\>*

### White Box \<\_building block x.2\_\> {#_white_box_building_block_x_2}

*\<white box template\>*

### White Box \<\_building block y.1\_\> {#_white_box_building_block_y_1}

*\<white box template\>*

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