package controllers

import play.api._
import play.api.libs.json.JsString
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class Application extends Controller {

  def index = Action.async {
   Future( Ok(JsString("Hey I am B")))
  }

}
