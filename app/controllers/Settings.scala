package controllers

import engine.GameEngine
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}

@Singleton
class Settings @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  implicit def string2Int(s: String): Int = augmentString(s).toInt

  def settings: Action[AnyContent] = Action {
    Ok
  }

  def newEngine(engine: String): Action[AnyContent] = Action { request =>
    GameEngine.changeEngine(request.session, engine)
    Ok(engine)
  }

  def setPonderTime(time: Int): Action[AnyContent] = Action { request =>
    GameEngine.setPonderTime(request.session, time)
    Ok("setPonderTime success")
  }

  def setAnalysisLines(lines: Int): Action[AnyContent] = Action { request =>
    GameEngine.setAnalysisLines(request.session, lines)
    Ok("setAnalysisLines success")
  }
}