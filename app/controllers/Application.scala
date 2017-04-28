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

  def initEngine(time: Int) = Action { request =>
    GameEngine.createID(request.session)
    GameEngine.setPonderTime(request.session, time)
    Ok("Success")
  }

  def newPosition(fen: String) = Action { request =>
      val whiteIsUp = "whiteIsUp" -> "false"
      var status: String = null
      if (GameEngine.exist(request.session)) status = GameEngine.newGame(request.session)
      else status = routes.Application.initEngine(3).toString
      Ok(Json.toJson( Map("status" -> status, whiteIsUp)))
  }

  def deleteID = Action { request =>
    for {
      files <- Option(new File(path + "public/saves").listFiles)
      file <- files if file.getName.startsWith((request.session.hashCode + "_").toString)
    } file.delete()
    Ok(GameEngine.deleteID(request.session))
  }

  def start = Action.async {
    val promiseOfString: Future[String] = Future("success")
    promiseOfString.map( i => Ok("Got result: " + i))
  }

  def next(playWithFen: String) = Action.async { request => {
    request.body.asJson match {
      case Some(json) =>
        val fen = (json \ "fen").as[String]

        val promiseOfString: Future[String] = Future {
          if (playWithFen == "false") {
            GameEngine.setFromMoves(request.session, (json \ "history").as[String])
          } else {
            GameEngine.setFromFen(request.session, fen)
          }
          val gamover = GameEngine.isGamover(request.session)
          if (gamover != "PROCESS") {
            gamover
          } else {
            GameEngine.go(request.session)
          }
        }
        promiseOfString.map(res =>
          Ok(Json.toJson(Map("status" -> res,
            "newfen" -> GameEngine.getFen(request.session),
            "bestmove" -> res.split(" ")(1),
            "gamover" -> GameEngine.isGamover(request.session))))
        )
      case None => Future(BadRequest("Unknown JSON"))
    }
  }  }

  def rate = Action {
    for {
      files <- Option(new File("public/saves").listFiles)
      file <- files if file.getName.startsWith("20_")
    } {
    	file.delete
    }
    Ok("")
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
