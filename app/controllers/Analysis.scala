package controllers

import engine.GameEngine
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Analysis @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def getBestMove: Action[AnyContent] = Action.async { request =>
    Future(Ok(GameEngine.getBestMove(request.session)))
  }

  def analysis: Action[AnyContent] = Action { request =>
    Ok(GameEngine.getAnalysis(request.session))
  }

  def startAnalysis(fen: String): Action[AnyContent] = Action { request =>
    GameEngine.startAnalysis(request.session, fen)
    Ok
  }

  def stopAnalysis(doMove: Boolean): Action[AnyContent] = Action { request =>
    GameEngine.stopAnalysis(request.session)
    Ok
  }
}
