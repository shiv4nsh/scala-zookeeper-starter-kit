package services

import com.google.inject.Inject
import com.knoldus.{WSHelper, ZookeeperServiceDiscovery}
import play.api.libs.json.JsValue

import scala.concurrent.Future

/**
  * Created by shivansh on 12/5/16.
  */
class CallService @Inject()(ws: WSHelper, zookeeperServiceDiscovery: ZookeeperServiceDiscovery) {

  def call: Future[JsValue] ={
    val url = zookeeperServiceDiscovery.getServiceBaseUrl("service-b")
    ws.get(url)
  }

}
