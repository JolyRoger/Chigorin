package controllers

import play.api.libs.json._
import engine.chessman.com.example.johnmasiello.chessapp.ChessBoard
import play.api.mvc._
import engine.GameEngine

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

  def analysis() = Action { request =>
//    val analysis = GameEngine.getAnalysis(request.session)
    Ok(Json.toJson( Map("multipv" -> "1", "cp" -> "0.5", "mate" -> "0", "best" ->
      "d7d5 d2d4 c8f5 c2c4 e7e6 b1c3 b8c6 c1g5 f8e7 c4d5 e6d5 g5e7 g8e7 e2e3 e8g8 f1e2 c6a5 e1g1 c7c6 f3e5 f7f6 e5f3")))
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