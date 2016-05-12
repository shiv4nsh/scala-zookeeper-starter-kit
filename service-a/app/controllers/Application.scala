package controllers

import com.google.inject.Inject
import play.api._
import play.api.mvc._
import services.CallService
import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject()(callService: CallService) extends Controller {

  def index = Action.async {
    callService.call map {data =>
      Ok(data)
    }
  }

}
