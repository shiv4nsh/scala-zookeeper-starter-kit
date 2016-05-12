import javax.inject.Inject

import com.knoldus.ZookeeperClient
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GlobalStart @Inject()(lifecycle: ApplicationLifecycle,zookeeperClient: ZookeeperClient) {

  //To be loaded on start of application
  // For this you have to start your service like this
  // ./activator "project service-b" -Dhttp.port=9001 "run 9001"
  println(s"This is the start of service-b and port is ${System.getProperty("http.port")}")
  val port = System.getProperty("http.port").toInt
  println(s"This is the start of service-a and port is ${}")
  zookeeperClient.registerInZookeeper("service-b","localhost",port)
  //This will contain if you want to run some code on service stop (ctrl+D)
  lifecycle.addStopHook { () =>
    Future {
      println("-------This is the end of application lifecycle-----------------------")
    }
  }
}