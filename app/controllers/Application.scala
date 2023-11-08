package controllers

import engine.GameEngine
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._

import java.io.File
import scala.concurrent.{ExecutionContext, Future, Promise}
import org.apache.pekko.actor.ActorSystem

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class Application @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  implicit def string2Int(s: String): Int = augmentString(s).toInt

  def index: Action[AnyContent] = Action {
    Redirect(routes.Application.game)
  }

  def game: Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def initEngine(time: Int, lines: Int): Action[AnyContent] = Action { request =>
    GameEngine.createID(request.session)
    GameEngine.setPonderTime(request.session, time)
    GameEngine.setAnalysisLines(request.session, lines)
    Ok("Success")
  }

  def newPosition(fen: String): Action[AnyContent] = Action { request =>
    val whiteIsUp = "whiteIsUp" -> "false"
    var status: String = null
    if (GameEngine.exist(request.session)) status = GameEngine.newGame(request.session)
    else status = routes.Application.initEngine(3, 3).toString
    Ok(Json.toJson(Map("status" -> status, whiteIsUp)))
  }

  def deleteID(): Action[AnyContent] = Action { request =>
    Ok(GameEngine.deleteID(request.session))
  }

  def start: Action[AnyContent] = Action.async {
    getFutureMessage(1.second).map { msg => Ok("Got result: " + msg) }
  }

  def next: Action[AnyContent] = Action.async { request => {
    request.body.asJson match {
      case Some(json) =>
        val analysis = (json \ "analysis").as[Boolean]

        val promiseOfString: Future[String] = Future {
          if (analysis) {
            GameEngine.getBestMove(request.session)
          } else {
            val fen = (json \ "fen").as[String]
            val time = (json \ "time").as[Int]
            GameEngine.setPonderTime(request.session, time)
            GameEngine.setFromFen(request.session, fen)
            GameEngine.go(request.session)
          }
        }
        promiseOfString.map(res =>
          Ok(res.split(" ")(1))
        )
      case None => Future(BadRequest("Unknown JSON"))
    }
  }
  }

  def rate: Action[AnyContent] = Action {
    for {
      files <- Option(new File("public/saves").listFiles)
      file <- files if file.getName.startsWith("20_")
    } {
      file.delete
    }
    Ok
  }

  def update(move: String): Action[AnyContent] = Action { request => {
    request.body.asJson.map { json =>
      (json \ "name").asOpt[String].map { name =>
        Ok("Hello " + name)
      }.getOrElse {
        BadRequest("Missing parameter [name]")
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }
  }

  def stopPonder: Action[AnyContent] = Action { request =>
    GameEngine.send(request.session, "stop")
    Ok("Stop ponder")
  }

  private def getFutureMessage(delayTime: FiniteDuration): Future[String] = {
    val promise: Promise[String] = Promise[String]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success("Success")
    }(actorSystem.dispatcher) // run scheduled tasks using the actor system's dispatcher
    promise.future
  }
}
