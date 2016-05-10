package com.example

/**
  * Created by shivansh on 9/5/16.
  */

import java.io.{BufferedReader, InputStreamReader}
import java.util
import java.util.List

import com.google.common.base.Predicate
import com.google.common.collect.{Iterables, Lists, Maps}
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.test.TestingServer
import org.apache.curator.utils.CloseableUtils
import org.apache.curator.x.discovery._
import org.apache.curator.x.discovery.details.JsonInstanceSerializer
import org.apache.curator.x.discovery.strategies.RandomStrategy

import scala.collection.JavaConversions._

case class InstanceDetails(description: String)

class DiscoveryExample {
  val PATH = "/discovery/example"

  def main(args: Array[String]) = {
    // This method is scaffolding to get the example up and running

    val server = new TestingServer()
    var client: CuratorFramework = null
    var serviceDiscovery: ServiceDiscovery[InstanceDetails] = null
    val providers:Map[String, ServiceProvider[InstanceDetails]]= Maps.newHashMap[String, ServiceProvider[InstanceDetails]]().toMap
    try {
      client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3))
      client.start();

      val serializer = new JsonInstanceSerializer[InstanceDetails](Class[InstanceDetails])
      serviceDiscovery = ServiceDiscoveryBuilder.builder(Class[InstanceDetails]).client(client).basePath(PATH).serializer(serializer).build()
      serviceDiscovery.start()

      processCommands(serviceDiscovery, providers, client);
    }
    finally {
      providers.values.map { cache =>
        CloseableUtils.closeQuietly(cache)
      }

      CloseableUtils.closeQuietly(serviceDiscovery)
      CloseableUtils.closeQuietly(client)
      CloseableUtils.closeQuietly(server)
    }
  }

  def processCommands(serviceDiscovery: ServiceDiscovery[InstanceDetails], providers: Map[String, ServiceProvider[InstanceDetails]], client: CuratorFramework) = {
    // More scaffolding that does a simple command line processor
    printHelp()
    val servers = Lists.newArrayList[ExampleServer]()
    try {
      val in = new BufferedReader(new InputStreamReader(System.in))
      var done = false
      while (!done) {
        System.out.print("> ")
        val line = in.readLine()
        if (line == null) {
          done = true
        }

        val command = line.trim();
        val parts = command.split("\\s")
        if (parts.length == 0) {

        } else {
          val operation = parts(0)
          val args = util.Arrays.copyOfRange(parts, 1, parts.length)

          if (operation.equalsIgnoreCase("help") || operation.equalsIgnoreCase("?")) {
            printHelp()
          }
          else if (operation.equalsIgnoreCase("q") || operation.equalsIgnoreCase("quit")) {
            done = true
          }
          else if (operation.equals("add")) {
            addInstance(args, client, command, servers)
          }
          else if (operation.equals("delete")) {
            deleteInstance(args, command, servers)
          }
          else if (operation.equals("random")) {
            listRandomInstance(args, serviceDiscovery, providers, command)
          }
          else if (operation.equals("list")) {
            listInstances(serviceDiscovery)
          }
        }
      }
    }
    finally {
      servers.map { server =>
        CloseableUtils.closeQuietly(server);
      }
    }
  }

  def listRandomInstance(args: Array[String], serviceDiscovery: ServiceDiscovery[InstanceDetails], providers: Map[String, ServiceProvider[InstanceDetails]], command: String) = {
    // this shows how to use a ServiceProvider
    // in a real application you'd create the ServiceProvider early for the service(s) you're interested in

    if (args.length != 1) {
      System.err.println("syntax error (expected random <name>): " + command)
    } else {

      val serviceName = args(0)
      var provider= providers.get(serviceName).get
      if (provider == null) {
        provider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).providerStrategy(new RandomStrategy[InstanceDetails]()).build()
        providers.put(serviceName, provider)
        provider.start()

        Thread.sleep(2500); // give the provider time to warm up - in a real application you wouldn't need to do this
      }

     val instance = provider.getInstance()
      if (instance == null) {
        System.err.println("No instances named: " + serviceName)
      }
      else {
        outputInstance(instance)
      }
    }
  }


  def listInstances(serviceDiscovery: ServiceDiscovery[InstanceDetails]) = {
    // This shows how to query all the instances in service discovery

    try {
      val serviceNames = serviceDiscovery.queryForNames()
      println(serviceNames.size() + " type(s)")
      serviceNames.toList.map { serviceName =>
        val instances = serviceDiscovery.queryForInstances(serviceName).toList
        instances.map(instance => outputInstance(instance))
      }
    }
    finally {
      CloseableUtils.closeQuietly(serviceDiscovery);
    }
  }

  def outputInstance(instance: ServiceInstance[InstanceDetails]) = {
    println("\t" + instance.getPayload().description + ": " + instance.buildUriSpec())
  }

  def deleteInstance(args: Array[String], command: String, servers: List[ExampleServer]) = {
    // simulate a random instance going down
    // in a real application, this would occur due to normal operation, a crash, maintenance, etc.

    if (args.length != 1) {
      System.err.println("syntax error (expected delete <name>): " + command)
    } else {
      val serviceName = args(0)
      val newPre = new Predicate[ExampleServer]() {
        @Override
        def apply(server: ExampleServer): Boolean = server.getThisInstance().getName().endsWith(serviceName)
      }
      val server = Iterables.find(servers, newPre)
      if (Option(server).isEmpty) {
        System.err.println("No servers found named: " + serviceName)
      } else {
        servers.remove(server)
        CloseableUtils.closeQuietly(server)
        println("Removed a random instance of: " + serviceName)
      }
    }
  }

  private def addInstance(args: Array[String], client: CuratorFramework, command: String, servers: List[ExampleServer]): Unit = {
    // simulate a new instance coming up
    // in a real application, this would be a separate process

    if (args.length < 2) {
      System.err.println("syntax error (expected add <name> <description>): " + command)
    } else {

      val description = new StringBuilder()
      val newDescription = args.map { a =>
        description.append(a)
      }.mkString(" ")

      val serviceName = args(0)
      val server = new ExampleServer(client, PATH, serviceName, newDescription)
      servers.add(server)
      server.start()

      println(serviceName + " added")
    }
  }

  def printHelp() = {
    println("An example of using the ServiceDiscovery APIs. This example is driven by entering commands at the prompt:\n")
    println("add <name> <description>: Adds a mock service with the given name and description")
    println("delete <name>: Deletes one of the mock services with the given name")
    println("list: Lists all the currently registered services")
    println("random <name>: Lists a random instance of the service with the given name")
    println("quit: Quit the example")
    println()
  }
}
