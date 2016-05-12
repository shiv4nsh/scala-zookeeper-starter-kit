
import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WSHelper @Inject()(ws: WSClient) extends Controller {

  val timeOut: Int = 15000

  def post(url: String, body: Map[String, Seq[String]]): Future[JsValue] = {
    ws.url(url).withRequestTimeout(timeOut).post(body).map { resp =>
      resp.json
    }
  }

  def get(url: String): Future[JsValue] = {
    ws.url(url).withRequestTimeout(timeOut).get() map{ resp =>
      resp.json
    }
  }
}
