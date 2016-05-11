import javax.inject.Inject

import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GlobalStart @Inject()(lifecycle: ApplicationLifecycle) {

  //To be loaded on start of application
  // For this you have to start your service like this
  // ./activator "project service-a" -Dhttp.port=9000 "run 9000"
  println(s"This is the start of service-a and port is ${System.getProperty("http.port")}")

  //This will contain if you want to run some code on service stop (ctrl+D)
  lifecycle.addStopHook { () =>
    Future {
      println("-------This is the end of application lifecycle-----------------------")
    }
  }
}