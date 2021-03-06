package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import java.io.File
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import engine.GameEngine

object Application extends Controller {

  implicit def string2Int(s: String): Int = augmentString(s).toInt

  def path = if (Play.isProd) "target/"+ new File("target").listFiles.filter(_.getName.startsWith("scala"))(0).getName +"/classes/"
  else { "" }

  def index = Action {
    Redirect(routes.Application.game)
  }

  def game = Action {
    Ok(views.html.index())
  }

  def initEngine(time: Int, lines: Int) = Action { request =>
    GameEngine.createID(request.session)
    GameEngine.setPonderTime(request.session, time)
    GameEngine.setAnalysisLines(request.session, lines)
    Ok("Success")
  }

  def newPosition(fen: String) = Action { request =>
    val whiteIsUp = "whiteIsUp" -> "false"
    var status: String = null
    if (GameEngine.exist(request.session)) status = GameEngine.newGame(request.session)
    else status = routes.Application.initEngine(3,3).toString
    Ok(Json.toJson( Map("status" -> status, whiteIsUp)))
  }

  def deleteID = Action { request =>
    Ok(GameEngine.deleteID(request.session))
  }

  def start = Action.async {
    val promiseOfString: Future[String] = Future("success")
    promiseOfString.map( i => Ok("Got result: " + i))
  }

  def next = Action.async { request => {
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
    } } }

  def rate = Action {
    for {
      files <- Option(new File("public/saves").listFiles)
      file <- files if file.getName.startsWith("20_")
    } {
      file.delete
    }
    Ok
  }

  def update(move: String) = Action { request =>
  {
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

  def stopPonder = Action { request =>
    GameEngine.send(request.session, "stop")
    Ok("Stop ponder")
  }
}
