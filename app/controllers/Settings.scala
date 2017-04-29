package controllers

import play.api.mvc._
import engine.GameEngine

object Settings extends Controller {

  implicit def string2Int(s: String): Int = augmentString(s).toInt

  def settings = Action {
    Ok
  }

  def newEngine(engine: String) = Action { request =>
    GameEngine.changeEngine(request.session, engine)
    Ok(engine)
  }

  def setPonderTime(time: Int) = Action { request =>
      GameEngine.setPonderTime(request.session, time)
	    Ok("setPonderTime success")
  }
}