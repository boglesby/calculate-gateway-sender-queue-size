# Calculate GatewaySender Queue Size
## Description

This project provides a function that calculates the sizes of parallel or serial GatewaySender queues.

For each input GatewaySender id, the **CalculateGatewaySenderQueueEntrySizesFunction**:

- gets the identified GatewaySender
- creates the appropriate **GatewaySenderQueueEntrySizer** based on the type of GatewaySender:
	- **ParallelGatewaySenderQueueEntrySizer** for parallel GatewaySenders
	- **ParallelGatewaySenderQueueByBucketEntrySizer** for parallel GatewaySenders whose entries are to be sized by bucket
	- **SerialGatewaySenderQueueEntrySizer** for serial GatewaySenders
- invokes *calculateEntrySizes* on the **GatewaySenderQueueEntrySizer**

The **GatewaySenderQueueEntrySizer**:

- gets the Region(s) implementing the GatewaySender queue
- for serial GatewaySenders, gets and calculates the sizes of all entries sorted by key and grouped by dispatcher thread
- for parallel GatewaySenders, gets and calculates the sizes of all entries either sorted by key or grouped by bucket

The **GatewayQueueEventRegionEntryObjectFilter** is used by the ObjectGraphSizer to include or exclude specific objects from the entry size.

## Initialization
Modify the **GEODE** environment variable in the *setenv.sh* script to point to a Geode installation directory.
## Build
Build the Spring Boot Client Application and Geode Server Function and sizer classes using gradle like:

```
./gradlew clean jar bootJar
```
## Run Example
### Start and Configure Locator and Servers
Start and configure the locator and 3 servers using the *startandconfigure.sh* script like:

```
./startandconfigure.sh
```
### Load Entries
Run the client to load N Trade instances using the *runclient.sh* script like below.

```
./runclient.sh load 1000 1024
```
The parameters are:

- operation (load)
- number of entries (1000)
- maximum size of the Trade payload (1024)

### Calculate Parallel GatewaySender Queue Entry Sizes
Execute the function to calculate the parallel GatewaySender queue entry sizes using the *runclient.sh* script like below.

```
./runclient.sh calculate-gateway-sender-queue-entry-sizes ny
```
The parameters are:

- operation (calculate-gateway-sender-queue-entry-sizes)
- parallel sender id (ny)

### Calculate Parallel GatewaySender Queue Entry Sizes Grouped by Bucket
Execute the function to calculate the parallel GatewaySender queue entry sizes grouped by bucket using the *runclient.sh* script like below.

```
./runclient.sh calculate-gateway-sender-queue-entry-sizes ny false true
```
The parameters are:

- operation (calculate-gateway-sender-queue-entry-sizes)
- parallel sender id (ny)
- log summary only (false)
- group by bucket (true)

### Calculate Serial GatewaySender Queue Entry Sizes
Execute the function to calculate the serial GatewaySender queue entry sizes using the *runclient.sh* script like below.

```
./runclient.sh calculate-gateway-sender-queue-entry-sizes nyserial
```
The parameters are:

- operation (calculate-gateway-sender-queue-entry-sizes)
- serial sender id (nyserial)

### Shutdown Locator and Servers
Execute the *shutdownall.sh* script to shutdown the servers and locators like:

```
./shutdownall.sh
```
### Remove Locator and Server Files
Execute the *cleanupfiles.sh* script to remove the server and locator files like:

```
./cleanupfiles.sh
```
## Example Sample Output
### Start and Configure Locator and Servers
Sample output from the *startandconfigure.sh* script is:

```
./startandconfigure.sh 
1. Executing - start locator --name=locator --J=-Dgemfire.distributed-system-id=1

................
Locator in <working-directory>/locator on xxx.xxx.x.x[10334] as locator is currently online.
Process ID: 51558
Uptime: 20 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/locator/locator.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

Successfully connected to: JMX Manager [host=xxx.xxx.x.x, port=1099]

Cluster configuration service is up and running.

2. Executing - set variable --name=APP_RESULT_VIEWER --value=any

Value for variable APP_RESULT_VIEWER is now: any.

3. Executing - configure pdx --read-serialized=true

read-serialized = true
ignore-unread-fields = false
persistent = false
Cluster configuration for group 'cluster' is updated.

4. Executing - start server --name=server-1 --server-port=0 --initial-heap=2g --max-heap=2g --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

..............
Server in <working-directory>/server-1 on xxx.xxx.x.x[58969] as server-1 is currently online.
Process ID: 51567
Uptime: 10 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-1/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

5. Executing - start server --name=server-2 --server-port=0 --initial-heap=2g --max-heap=2g --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

...............
Server in <working-directory>/server-2 on xxx.xxx.x.x[59008] as server-2 is currently online.
Process ID: 51571
Uptime: 12 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-2/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

6. Executing - start server --name=server-3 --server-port=0 --initial-heap=2g --max-heap=2g --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

.............
Server in <working-directory>/server-3 on xxx.xxx.x.x[59045] as server-3 is currently online.
Process ID: 51577
Uptime: 10 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-3/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

7. Executing - list members

Member Count : 4

  Name   | Id
-------- | --------------------------------------------------------------
locator  | xxx.xxx.x.x(locator:51558:locator)<ec><v0>:41000 [Coordinator]
server-1 | xxx.xxx.x.x(server-1:51567)<v1>:41001
server-2 | xxx.xxx.x.x(server-2:51571)<v2>:41002
server-3 | xxx.xxx.x.x(server-3:51577)<v3>:41003

8. Executing - create gateway-sender --id=ny --remote-distributed-system-id=2 --parallel=true

 Member  | Status | Message
-------- | ------ | ----------------------------------------
server-1 | OK     | GatewaySender "ny" created on "server-1"
server-2 | OK     | GatewaySender "ny" created on "server-2"
server-3 | OK     | GatewaySender "ny" created on "server-3"

Cluster configuration for group 'cluster' is updated.

9. Executing - create gateway-sender --id=nyserial --remote-distributed-system-id=2 --parallel=false

 Member  | Status | Message
-------- | ------ | ----------------------------------------------
server-1 | OK     | GatewaySender "nyserial" created on "server-1"
server-2 | OK     | GatewaySender "nyserial" created on "server-2"
server-3 | OK     | GatewaySender "nyserial" created on "server-3"

Cluster configuration for group 'cluster' is updated.

10. Executing - sleep --time=5


11. Executing - create region --name=Trade --type=PARTITION_REDUNDANT --gateway-sender-id=ny,nyserial

 Member  | Status | Message
-------- | ------ | -------------------------------------
server-1 | OK     | Region "/Trade" created on "server-1"
server-2 | OK     | Region "/Trade" created on "server-2"
server-3 | OK     | Region "/Trade" created on "server-3"

Cluster configuration for group 'cluster' is updated.

12. Executing - list regions

List of regions
---------------
Trade

13. Executing - deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar

 Member  |       Deployed JAR        | Deployed JAR Location
-------- | ------------------------- | ---------------------------------------------------------
server-1 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-1/server-0.0.1-SNAPSHOT.v1.jar
server-2 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-2/server-0.0.1-SNAPSHOT.v1.jar
server-3 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-3/server-0.0.1-SNAPSHOT.v1.jar

14. Executing - list functions

 Member  | Function
-------- | ---------------------------------------------
server-1 | CalculateGatewaySenderQueueEntrySizesFunction
server-2 | CalculateGatewaySenderQueueEntrySizesFunction
server-3 | CalculateGatewaySenderQueueEntrySizesFunction

************************* Execution Summary ***********************
Script file: startandconfigure.gfsh

Command-1 : start locator --name=locator --J=-Dgemfire.distributed-system-id=1
Status    : PASSED

Command-2 : set variable --name=APP_RESULT_VIEWER --value=any
Status    : PASSED

Command-3 : configure pdx --read-serialized=true
Status    : PASSED

Command-4 : start server --name=server-1 --server-port=0 --initial-heap=2g --max-heap=2g --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-5 : start server --name=server-2 --server-port=0 --initial-heap=2g --max-heap=2g --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-6 : start server --name=server-3 --server-port=0 --initial-heap=2g --max-heap=2g --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-7 : list members
Status    : PASSED

Command-8 : create gateway-sender --id=ny --remote-distributed-system-id=2 --parallel=true
Status    : PASSED

Command-9 : create gateway-sender --id=nyserial --remote-distributed-system-id=2 --parallel=false
Status    : PASSED

Command-10 : sleep --time=5
Status     : PASSED

Command-11 : create region --name=Trade --type=PARTITION_REDUNDANT --gateway-sender-id=ny,nyserial
Status     : PASSED

Command-12 : list regions
Status     : PASSED

Command-13 : deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar
Status     : PASSED

Command-14 : list functions
Status     : PASSED
```
### Load Entries
Sample output from the *runclient.sh* script is:

```
./runclient.sh load 1000 1024

> Task :client:bootRun

2021-01-02 09:16:48.269  INFO 4940 --- [           main] example.client.Client                    : Starting Client on ...
...
2021-01-02 09:16:56.590  INFO 4940 --- [           main] example.client.Client                    : Started Client in 11.116 seconds (JVM running for 11.795)
2021-01-02 09:16:56.593  INFO 4940 --- [           main] example.client.service.TradeService      : Putting 1000 trades of random size up to 1024 bytes
2021-01-02 09:16:56.992  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=0, cusip=AMZN, shares=81, price=87.76, createTime=1609615016593, updateTime=1609615016593, payloadLength=852)
2021-01-02 09:16:57.086  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=1, cusip=AMZN, shares=51, price=121.78, createTime=1609615016992, updateTime=1609615016992, payloadLength=848)
2021-01-02 09:16:57.195  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=2, cusip=AAPL, shares=65, price=725.25, createTime=1609615017086, updateTime=1609615017086, payloadLength=415)
2021-01-02 09:16:57.265  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=3, cusip=AVGO, shares=11, price=118.29, createTime=1609615017195, updateTime=1609615017195, payloadLength=85)
2021-01-02 09:16:57.801  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=4, cusip=PYPL, shares=36, price=783.20, createTime=1609615017265, updateTime=1609615017265, payloadLength=727)
...
2021-01-02 09:17:08.260  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=995, cusip=GOOGL, shares=87, price=764.92, createTime=1609615028257, updateTime=1609615028257, payloadLength=108)
2021-01-02 09:17:08.264  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=996, cusip=AMZN, shares=87, price=795.68, createTime=1609615028260, updateTime=1609615028260, payloadLength=806)
2021-01-02 09:17:08.268  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=997, cusip=JNJ, shares=85, price=675.74, createTime=1609615028264, updateTime=1609615028264, payloadLength=565)
2021-01-02 09:17:08.271  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=998, cusip=BUD, shares=1, price=662.95, createTime=1609615028268, updateTime=1609615028268, payloadLength=121)
2021-01-02 09:17:08.275  INFO 4940 --- [           main] example.client.service.TradeService      : Saved Trade(id=999, cusip=UPS, shares=97, price=647.42, createTime=1609615028271, updateTime=1609615028271, payloadLength=751)
```
### Calculate Parallel GatewaySender Queue Entry Sizes
Sample output from the *runclient.sh* script is:

```
./runclient.sh calculate-gateway-sender-queue-entry-sizes ny

> Task :client:bootRun

2021-01-02 09:19:43.921  INFO 5132 --- [           main] example.client.Client                    : Starting Client on ...
...
2021-01-02 09:19:47.825  INFO 5132 --- [           main] example.client.Client                    : Started Client in 4.299 seconds (JVM running for 4.862)
2021-01-02 09:19:47.992  INFO 5132 --- [           main] example.client.service.TradeService      : Calculated entry sizes for gatewaySenderId=ny; summaryOnly=false; groupByBucket=false; result=[{server-3=true, server-2=true, server-1=true}]
```
Each server's log file will contain a message for its primary and secondary queues like:

```
[info 2021/01/02 09:19:47.956 HST <ServerConnection on port 58395 Thread 4> tid=0x8d] 
Parallel GatewaySender ny contains the following 331 primary entries and sizes:

		key=114; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,408
		key=117; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=792
		key=119; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=752
		key=120; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,072
		key=122; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=744
		...

Parallel GatewaySender ny contains the following 335 secondary entries and sizes:

		key=118; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,248
		key=123; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=784
		key=126; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,408
		key=128; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,112
		key=133; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,000
		...

Parallel GatewaySender ny contains:

	331 primary entries consisting of 293,384 bytes
	335 secondary entries consisting of 294,272 bytes
	666 total entries consisting of 587,656 bytes
```
### Calculate Parallel GatewaySender Queue Entry Sizes Grouped by Bucket
Sample output from the *runclient.sh* script is:

```
./runclient.sh calculate-gateway-sender-queue-entry-sizes ny false true

> Task :client:bootRun

2021-01-02 09:21:04.252  INFO 5153 --- [           main] example.client.Client                    : Starting Client on ...
...
2021-01-02 09:21:08.406  INFO 5153 --- [           main] example.client.Client                    : Started Client in 4.497 seconds (JVM running for 4.933)
2021-01-02 09:21:08.523  INFO 5153 --- [           main] example.client.service.TradeService      : Calculated entry sizes for gatewaySenderId=ny; summaryOnly=false; groupByBucket=true; result=[{server-3=true, server-2=true, server-1=true}]
```
Each server's log file will contain a message for its primary and secondary queues like:

```
[info 2021/01/02 09:21:08.501 HST <ServerConnection on port 58395 Thread 5> tid=0x8f] 
Parallel GatewaySender ny contains the following 38 primary buckets consisting of 331 total entries and sizes grouped by bucket:

	Bucket 1 contains the following 9 entries and sizes:

		key=114; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,408
		key=227; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,328
		key=340; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,000
		key=453; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=536
		key=566; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,128
		key=679; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=744
		key=792; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=432
		key=905; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,216
		key=1018; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=440

	Bucket 1 contains 8,232 total bytes

	Bucket 4 contains the following 10 entries and sizes:

		key=117; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=792
		key=230; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=672
		key=343; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=640
		key=456; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=912
		key=569; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,208
		key=682; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=872
		key=795; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,360
		key=908; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=696
		key=1021; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=704
		key=1134; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,336

	Bucket 4 contains 9,192 total bytes

	Bucket 6 contains the following 7 entries and sizes:

		key=119; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=752
		key=232; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,192
		key=345; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=800
		key=458; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,120
		key=571; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,272
		key=684; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=928
		key=797; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,280

	Bucket 6 contains 7,344 total bytes

	Bucket 7 contains the following 7 entries and sizes:

		key=120; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,072
		key=233; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=496
		key=346; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,272
		key=459; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=728
		key=572; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,096
		key=685; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=448
		key=798; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,152

	Bucket 7 contains 6,264 total bytes

	Bucket 9 contains the following 5 entries and sizes:

		key=122; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=744
		key=235; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=488
		key=348; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,216
		key=461; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=488
		key=574; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,344

	Bucket 9 contains 4,280 total bytes

	...

Parallel GatewaySender ny contains:

	331 primary entries consisting of 293,384 bytes
	335 secondary entries consisting of 294,272 bytes
	666 total entries consisting of 587,656 bytes
```
### Calculate Serial GatewaySender Queue Entry Sizes
Sample output from the *runclient.sh* script is:

```
./runclient.sh calculate-gateway-sender-queue-entry-sizes nyserial

> Task :client:bootRun

2021-01-02 09:18:09.386  INFO 5034 --- [           main] example.client.Client                    : Starting Client on ...
...
2021-01-02 09:18:15.443  INFO 5034 --- [           main] example.client.Client                    : Started Client in 6.728 seconds (JVM running for 7.474)
2021-01-02 09:18:15.774  INFO 5034 --- [           main] example.client.service.TradeService      : Calculated entry sizes for gatewaySenderId=nyserial; summaryOnly=false; groupByBucket=false; result=[{server-3=true, server-2=true, server-1=true}]
```
The primary server's log file will contain a message like:

```
[info 2021/01/02 09:18:15.760 HST <ServerConnection on port 58546 Thread 2> tid=0x7e] 
Serial GatewaySender nyserial contains the following 1000 primary entries and sizes grouped by dispatcher:

	Dispatcher nyserial.0 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=808
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=624
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=856
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,224
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=720
		...
		
	Dispatcher nyserial.0 contains 171,608 total bytes

	Dispatcher nyserial.1 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=472
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=504
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=520
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=792
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,312
		...

	Dispatcher nyserial.1 contains 180,528 total bytes

	Dispatcher nyserial.2 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,120
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,264
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,104
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=792
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=968
		...

	Dispatcher nyserial.2 contains 177,848 total bytes

	Dispatcher nyserial.3 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,240
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,048
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,040
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,016
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,104
		...

	Dispatcher nyserial.3 contains 170,504 total bytes

	Dispatcher nyserial.4 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=1,240
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=728
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=848
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=912
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=GatewaySenderEventImpl; numBytes=496
		...

	Dispatcher nyserial.4 contains 178,424 total bytes

Serial GatewaySender nyserial contains 1000 primary entries entries consisting of 878,912 bytes

```
Any secondary server's log file will contain a message like:

```
[info 2021/01/02 09:18:15.729 HST <ServerConnection on port 58457 Thread 2> tid=0x89] 
Serial GatewaySender nyserial contains the following 1000 secondary entries and sizes grouped by dispatcher:

	Dispatcher nyserial.0 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=840
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=656
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=888
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,256
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=752
		...

	Dispatcher nyserial.0 contains 178,008 total bytes

	Dispatcher nyserial.1 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=504
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=536
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=552
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=824
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,344
		...

	Dispatcher nyserial.1 contains 186,928 total bytes

	Dispatcher nyserial.2 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,152
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,296
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,136
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=824
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,000
		...

	Dispatcher nyserial.2 contains 184,248 total bytes

	Dispatcher nyserial.3 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,272
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,080
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,072
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,048
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,136
		...

	Dispatcher nyserial.3 contains 176,904 total bytes

	Dispatcher nyserial.4 contains the following 200 entries:

		key=0; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=1,272
		key=1; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=760
		key=2; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=880
		key=3; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=944
		key=4; entryClass=VMThinDiskLRURegionEntryHeapLongKey; valueClass=VMCachedDeserializable; numBytes=528
		...

	Dispatcher nyserial.4 contains 184,824 total bytes

Serial GatewaySender nyserial contains 1000 secondary entries entries consisting of 910,912 bytes
```
### Shutdown Locator and Servers
Sample output from the *shutdownall.sh* script is:

```
./shutdownall.sh 

(1) Executing - connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=192.168.1.11, port=1099] ..
Successfully connected to: [host=192.168.1.11, port=1099]


(2) Executing - shutdown --include-locators=true

Shutdown is triggered
```
