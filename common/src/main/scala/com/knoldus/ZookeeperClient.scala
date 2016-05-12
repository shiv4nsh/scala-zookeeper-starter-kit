package com.knoldus

import org.apache.curator.framework._
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.curator.retry.{ExponentialBackoffRetry, RetryNTimes}
import org.apache.curator.x.discovery.{ServiceDiscoveryBuilder, ServiceInstance, UriSpec}
import org.slf4j.LoggerFactory

class ZookeeperClient {

  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def registerInZookeeper(address:String, serviceName:String,port: Integer) = {
    val curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(5, 1000))
    curatorFramework.start()
    val serviceInstance: ServiceInstance[Void] = ServiceInstance.builder()
      .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
      .address(address)
      .port(port)
      .name(serviceName)
      .build()
    ServiceDiscoveryBuilder.builder[Void](Void.TYPE).basePath("scala-zookeeper-starterkit")
      .client(curatorFramework)
      .thisInstance(serviceInstance)
      .build()
      .start()
  }

}
