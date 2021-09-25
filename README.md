# UIN: 679144170 - Jawaharlal Sathyanarayan, Shashwath - CS 441 Homework 1

## Project build instructions
Please follow the following steps to run the simulations implemented as part of this homework.
1) Open IntellJ IDEA, in the  welcome screen select “Check out from Version Control” and then “Git”.
2) Enter the following URL and click “Clone”: https://github.com/js-shashwath/CloudOrgSimulator.git
3) Please go to src/main/scala/ and select the Simulation file to run all the models in the Simulations folder. If you want to run any particular model please go to src/main/scala/Simulations and select any one of the simulation files. An IntelliJ run configuration is auto-created when you click the green arrow next to the main method of the simulation file you want to run.

## Structire of the project
### Simulations (src/main/scala/Simulations)
The following Simulation classes written in Scala using Cloud Sim Plus Framework are provided -
- MySimulation1
- MySimulation2
- MySimulation3

### Resources (src/main/resources)
This directory mainly contains the different configuration files used for the created simulations.

### Tests (src/main/test/scala)
The following test class is provided: MySimulation1Test
The following methods are tested:
- createDatacenter: the test checks whether the created Datacenter is not null and that it contains the correct number of hosts.
- createVms: the test checks whether the list of VMs created is not empty and the number of VMs created matches the input configuration.
- createHost: the test checks whether an individual host object created during simulations is not null and it's properties match the input configuration.
- createCloudlets: the test checks whether the list of cloudlets created is not empty and the number of cloudlets created matches the input configuration.

## Configuration
Individual configuration files containing the configuration parameters have been used for all the created simulations.
Configuration parameters for the following entities are used:
- Datacenter
- Host
- VM
- Cloudlet

The pricing model for the datacenters have been based on memory, storage, bandwidth and CPU useage.

## Policies used In Simulations 1,2 and 3
In the simulations MySimulation1, MySimulation2, primarily the following policies are used :
- VM Allocation Policy
- Cloudlet Utilization Model
- Cloudlet Scheduler Type
- VM Scheduler Type

In this simulation 3, the user is prompted to enter different configuration details for the simulation to be run.

The following logic is used to differentiate between the different data centers for simulating the services as per the user requested configurations :

- A Faas offers the highest level of abstraction and if the user enters only cloudlet length the simulation takes places in datacenter1 as this corresponds to the service offered by a Faas DC

- A SaaS brokers have contorl limited only to the scheduling algorithms for the cloudlet. Accessability to the VMs or the Hosts isn't possible which is implemented on DC 1 as well

- A PaaS broker will be able to manage the scheduling policies for the cloudlets and the VMs. This is implemented in datacenter2

- A Iaas broker will have access upto the OS and the network topology of the DC. This has been implemented in datacenter3

In-depth explanation regarding the various configurations and policies used in these different simulations and how they affect the obtained results are provided in the code comments.
## Results Analysis

###MySimulation1:

SIMULATION RESULTS

                                                       SIMULATION 1 RESULTS

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|Actual CPU Time|Total Cost
ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds|               |
--------------------------------------------------------------------------------------------------------------------------------
       0|SUCCESS| 2|   0|        8| 0|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       1|SUCCESS| 2|   0|        8| 1|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       2|SUCCESS| 2|   0|        8| 2|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       3|SUCCESS| 2|   0|        8| 3|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       4|SUCCESS| 2|   1|        8| 4|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       5|SUCCESS| 2|   1|        8| 5|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       6|SUCCESS| 2|   1|        8| 6|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       7|SUCCESS| 2|   1|        8| 7|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       8|SUCCESS| 2|   2|        8| 8|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
       9|SUCCESS| 2|   2|        8| 9|        2|       1000|          2|        0|         1|       1|            1.0|      20.0
--------------------------------------------------------------------------------------------------------------------------------

###MySimulation2:

                                                             SIMULATION 2 RESULTS

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|Actual CPU Time|Total Cost
ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds|               |
--------------------------------------------------------------------------------------------------------------------------------
       0|SUCCESS| 2|   0|        8| 0|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       1|SUCCESS| 2|   0|        8| 1|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       2|SUCCESS| 2|   0|        8| 2|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       3|SUCCESS| 2|   0|        8| 3|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       4|SUCCESS| 2|   1|        8| 4|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       5|SUCCESS| 2|   1|        8| 5|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       6|SUCCESS| 2|   1|        8| 6|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       7|SUCCESS| 2|   1|        8| 7|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       8|SUCCESS| 2|   2|        8| 8|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
       9|SUCCESS| 2|   2|        8| 9|        2|       1000|          2|        0|         4|       4|4.010000000000001|80.20000000000002
--------------------------------------------------------------------------------------------------------------------------------

MySimulation1 uses -
- VM Allocation Policy : First Fit
- Cloudlet Utilization Model: Full
- Cloudlet Scheduler Type: Time-Shared
- VM Scheduler Type: Time-Shared

MySimulation2 uses -
- VM Allocation Policy : Best Fit
- Cloudlet Utilization Model: Dynamic (25%)
- Cloudlet Scheduler Type: Time-Shared
- VM Scheduler Type: Space-Shared

The price difference incurred in the models can be attributed to the cloudlet utilization model as in the full utilization model no additional cloudlets will be served by a VM until it's instructions are fully processed. This overhead is reduced when the 
cloudlets are dynamically serviced by the VM based on the utilization percentage defined. 

###MySimulation3:

                                                       SIMULATION 3 RESULTS

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|Actual CPU Time|Total Cost
ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds|               |
--------------------------------------------------------------------------------------------------------------------------------
       0|SUCCESS| 2|   0|        8| 0|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
      10|SUCCESS| 2|   0|        8|10|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       1|SUCCESS| 2|   1|        8| 1|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
      11|SUCCESS| 2|   1|        8|11|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       2|SUCCESS| 2|   2|        8| 2|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       3|SUCCESS| 2|   3|        8| 3|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       4|SUCCESS| 2|   4|        8| 4|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       5|SUCCESS| 2|   5|        8| 5|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       6|SUCCESS| 2|   6|        8| 6|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       7|SUCCESS| 2|   7|        8| 7|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       8|SUCCESS| 2|   8|        8| 8|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
       9|SUCCESS| 2|   9|        8| 9|        2|          1|          2|        0|         0|       0|0.0010000000000000009|0.050000000000000044
--------------------------------------------------------------------------------------------------------------------------------
Simulation 3 has been executed on datacenter 2 as the broker requires to allocate the OS to the datacenter. As only datacenter 2 offers Infrastructure as a service the above execution takes place on DC 2 and not on DC 1 or DC 2.