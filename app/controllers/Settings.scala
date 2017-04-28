package controllers

import play.api.mvc._
import engine.GameEngine

object Settings extends Controller {
  implicit def string2Int(s: String): Int = augmentString(s).toInt
    def settings = Action {
    	Ok("")
    }

  def analysis = Action { request =>
    Ok(GameEngine.getAnalysis(request.session))
  }

  def startAnalysis(fen: String) = Action { request =>
    GameEngine.startAnalysis(request.session, fen)
    Ok
  }

  def stopAnalysis(doMove: Boolean) = Action { request =>
    Ok(GameEngine.stopAnalysis(request.session, doMove).split(" ")(1))
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