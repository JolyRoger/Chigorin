package controllers

import engine.chessman.com.example.johnmasiello.chessapp.ChessBoard
import play.api.libs.json.Json
import play.api.mvc._
import engine.GameEngine
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

object Settings extends Controller {
  implicit def string2Int(s: String): Int = augmentString(s).toInt
    def settings = Action {
    	Ok("")
    }

  def getLegalMovesAsString(fen: String) = {
    try {
      val board = new ChessBoard()
      board.setFen(fen)
      board.setTheBoardUp
      board.generateMoves
      Some(board.toString)
    } catch {
      case e: Exception => None
    }
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
/*
    val res = GameEngine.stopAnalysis(request.session)
    Ok(Json.toJson(Map("status" -> res,
      "bestmove" -> res.split(" ")(1),
      "gamover" -> GameEngine.isGamover(request.session))))
*/
  }

  def getLegalMoves(fen: String) = Action { request =>
    val legal = getLegalMovesAsString(fen)
    legal match {
      case Some(leg) => Ok(leg)
      case None => BadRequest("Bad FEN")
    }
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