package controllers

import engine.GameEngine
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Analysis extends Controller {

  def getBestMove = Action.async { request =>
    Future(Ok(GameEngine.getBestMove(request.session)))
  }

  def analysis = Action { request =>
    Ok(GameEngine.getAnalysis(request.session))
  }

  def startAnalysis(fen: String) = Action { request =>
    GameEngine.startAnalysis(request.session, fen)
    Ok
  }

  def stopAnalysis(doMove: Boolean) = Action { request =>
    GameEngine.stopAnalysis(request.session)
    Ok
  }
}
