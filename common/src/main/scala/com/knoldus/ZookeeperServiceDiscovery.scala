package com.knoldus

import com.google.inject.Singleton
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder

import scala.collection.JavaConversions._

/**
  * Created by shivansh on 12/5/16.
  */
@Singleton
class ZookeeperServiceDiscovery {
  val curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(5, 1000))
  curatorFramework.start()
  val serviceDiscovery = ServiceDiscoveryBuilder
    .builder(Void.TYPE)
    .basePath("scala-zookeeper-kit")
    .client(curatorFramework).build()
  serviceDiscovery.start()

  def getServiceBaseUrl(serviceName: String): String = {
    val serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).build()
    serviceProvider.start()
    lazy val allInstances = serviceProvider.getAllInstances.toList
    val instance = serviceProvider.getInstance()
    val url = instance.buildUriSpec()
    println(s"URL:::::$url")
    url
  }
}
