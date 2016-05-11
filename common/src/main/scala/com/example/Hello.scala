package com.example

import org.apache.curator.framework._
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.curator.retry.{ExponentialBackoffRetry, RetryNTimes}
import org.apache.curator.x.discovery.{ServiceDiscoveryBuilder, ServiceInstance, UriSpec}
import org.slf4j.LoggerFactory

/*object Hello {
  def main(args: Array[String]): Unit = {
    new ZookeeperClient
    println("Hello, world!")
  }
}*/
case class InstanceDetails(desc: String)

class ZookeeperClient {

  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def main(args: Array[String]) = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val curatorZookeeperClient = CuratorFrameworkFactory.newClient("localhost:2181,localhost:2182,localhost:2183", retryPolicy)
    curatorZookeeperClient.start
    curatorZookeeperClient.getZookeeperClient.blockUntilConnectedOrTimedOut
    val znodePath = "/test_node"
    val originalData = new String(curatorZookeeperClient.getData.forPath(znodePath)) // This should be "Some data"

    /* Zookeeper NodeCache service to get properties from ZNode */
    val nodeCache = new NodeCache(curatorZookeeperClient, znodePath)
    nodeCache.getListenable.addListener(new NodeCacheListener {
      @Override
      def nodeChanged = {
        try {
          val dataFromZNode = nodeCache.getCurrentData
          val newData = new String(dataFromZNode.getData) // This should be some new data after it is changed in the Zookeeper ensemble
        } catch {
          case ex: Exception => logger.error("Exception while fetching properties from zookeeper ZNode, reason " + ex.getCause)
        }
      }

      nodeCache.start
    })
  }

  def registerInZookeeper(port: Integer) = {
    val curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(5, 1000))
    curatorFramework.start()
    val serviceInstance: ServiceInstance[Void] = ServiceInstance.builder()
      .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
      .address("localhost")
      .port(port)
      .name("worker")
      .build()
    ServiceDiscoveryBuilder.builder[Void](Void.TYPE).basePath("load-balancing-example")
      .client(curatorFramework)
      .thisInstance(serviceInstance)
      .build()
      .start()
  }

}
