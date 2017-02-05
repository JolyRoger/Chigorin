package controllers

import engine.chessman.com.example.johnmasiello.chessapp.ChessBoard
import play.api.mvc._
import engine.GameEngine
import play.api.libs.json.Json

object Settings extends Controller { 
  implicit def string2Int(s: String): Int = augmentString(s).toInt
    def settings = Action {
    	Ok("")
    }

  def getLegalMoves(fen: String) = Action { request =>
    val board = new ChessBoard()
    board.setFen(fen)
    board.setTheBoardUp
    board.generateMoves
    val legalMoves = board.toString
    Ok(legalMoves)
  }

  def getLegalMovesOld(playWithFen: String) = Action { request =>
	    request.body.asJson.map { json =>
		  if (playWithFen == "false") {
			GameEngine.setFromMoves(request.session, (json \ "history").as[String])
		  } else {		// playWithFen == "true"
        println("GLM: " + (json \ "fen").as[String])
			GameEngine.setFromFen(request.session, (json \ "history").as[String], (json \ "fen").as[String])
		  }
		  
		   Ok(Json.toJson( Map( "fen" -> GameEngine.getFen(request.session),
				   				"legal" -> GameEngine.getLegalMoves(request.session, "") ) ) )
						  
//		  Ok(GameEngine.getLegalMoves(id))
	    }.getOrElse(BadRequest("Need the JSON data"))
  }
  
  def setPonderTime(time: Int) = Action { request =>
      GameEngine.setPonderTime(request.session, time)
	    Ok("setPonderTime success")
  }
}