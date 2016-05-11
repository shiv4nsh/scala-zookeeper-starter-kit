import javax.inject.Inject

import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GlobalStart @Inject()(lifecycle: ApplicationLifecycle) {

  //To be loaded on start of application
  // For this you have to start your service like this
  // ./activator "project service-b" -Dhttp.port=9001 "run 9001"
  println(s"This is the start of service-b and port is ${System.getProperty("http.port")}")

  //This will contain if you want to run some code on service stop (ctrl+D)
  lifecycle.addStopHook { () =>
    Future {
      println("-------This is the end of application lifecycle-----------------------")
    }
  }
}