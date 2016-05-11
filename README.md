### Scala with Zookeeper for Microservices Discovery 

This is a basic project for the microservices discovery through zookeeper

First download the Apache zookeper's latest stable release from there official site.

Now unzip the tar file using the command 

tar -xvf zookeeper-3.4.8.tar.gz

Now add the zoo.cfg file inside the conf folder of zookeeper

zoo.cfg file should be like 

tickTime=2000
initLimit=10
syncLimit=5
dataDir=/home/{path}/zookeeperData
clientPort=2181

and now go to sbin and start the zookeeper using the following command

sudo ./zkServer.sh start-foreground

Now connect the Cli with the server using the command

sudo ./zkCli.sh 

Now start your Microservices using the following command respectively in different terminals

./activator "project service-a" clean -Dhttp.port=9000 "run 9000"

./activator "project service-b" clean -Dhttp.port=9001 "run 9001"