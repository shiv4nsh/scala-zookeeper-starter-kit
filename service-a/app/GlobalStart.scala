import javax.inject.Inject

import com.knoldus.ZookeeperClient
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GlobalStart @Inject()(lifecycle: ApplicationLifecycle, zookeeperClient: ZookeeperClient) {

  //To be loaded on start of application
  // For this you have to start your service like this
  // ./activator "project service-a" -Dhttp.port=9000 "run 9000"
  val port = System.getProperty("http.port").toInt
  println(s"This is the start of service-a and port is ${}")
  zookeeperClient.registerInZookeeper("service-a","localhost",port)
  //This will contain if you want to run some code on service stop (ctrl+D)
  lifecycle.addStopHook { () =>
    Future {
      println("-------This is the end of application lifecycle-----------------------")
    }
  }
}