package com.example

import java.io.Closeable

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.utils.CloseableUtils
import org.apache.curator.x.discovery.details.JsonInstanceSerializer
import org.apache.curator.x.discovery.{ServiceDiscoveryBuilder, ServiceInstance, UriSpec}

/**
  * Created by shivansh on 9/5/16.
  */
class ExampleServer(client: CuratorFramework
                    , path: String, serviceName: String, description: String) extends Closeable {

  val uriSpec = new UriSpec("{scheme}://foo.com:{port}")
  // in a real application, you'd have a convention of some kind for the URI layout


  val thisInstance = ServiceInstance.builder()
    .name(serviceName)
    .payload(new InstanceDetails(description))
    .port((65535 * Math.random()).toInt) // in a real application, you'd use a common port
    .uriSpec(uriSpec)
    .build()

  // if you mark your payload class with @JsonRootName the provided JsonInstanceSerializer will work
  val serializer = new JsonInstanceSerializer[InstanceDetails](Class[InstanceDetails])

  val serviceDiscovery = ServiceDiscoveryBuilder.builder(Class[InstanceDetails])
    .client(client)
    .basePath(path)
    .serializer(serializer)
    .thisInstance(thisInstance)
    .build()

  def getThisInstance(): ServiceInstance[InstanceDetails] = return thisInstance

  def start(): Unit = {
    serviceDiscovery.start()
  }

  def close(): Unit = {
    CloseableUtils.closeQuietly(serviceDiscovery);
  }
}