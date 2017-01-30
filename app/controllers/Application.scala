package controllers

import play.api._
import play.api.libs.ws.{WS, WSRequestHolder}
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import java.text.SimpleDateFormat
import java.util.Calendar
import scalax.io.Resource
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

  def newPosition = Action { request =>
    val status = "status" -> "OK"
//    val fen = "fen" -> "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    val whiteIsUp = "whiteIsUp" -> "false"
    if (GameEngine.exist(request.session)) {
      GameEngine.newGame(request.session)
      Ok(Json.toJson( Map(status, whiteIsUp
        /*"legal" -> "d2d3 d2d4"*/)))
    } else {
      val id = routes.Application.initEngine(5).toString
      Ok(Json.toJson( Map(status, whiteIsUp
        /*"legal" -> /*GameEngine.getLegalMoves(request.session, "")))).withSession("ID" -> id)*/ "c2c3 c2c4"*/)))
    }
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
//    var oldlegal = ""
//    var oldfen = ""
    request.body.asJson match {
      case Some(json) =>
        val history = (json \ "history").as[String]
        val fen = (json \ "fen").as[String]

        val promiseOfString: Future[String] = Future {
          if (playWithFen == "false") {
            GameEngine.setFromMoves(request.session, (json \ "history").as[String])
          } else {
            //                println("next: " + fen)
            GameEngine.setFromFen(request.session, history, fen)
          }

//          oldlegal = "" //Ok(routes.Settings.getLegalMoves(/*request.session, */fen)).body

          val gamover = GameEngine.isGamover(request.session)
          if (gamover != "PROCESS") {
            gamover
          } else {
            GameEngine.go(request.session)
          }
        }
//        val encodedFen = play.utils.UriEncoding.encodePathSegment(fen, "UTF-8")
//        val req: WSRequestHolder = WS.url("http://localhost:9000/getLegalMoves/" + encodedFen) /*.withQueryString("fen" -> fen)*/
        promiseOfString.map(res =>
/*
          val legalMoves = req.get.value match {
            case Some(res) => res
            case None => "Nothing"
          }
          println("legal moves: " + legalMoves)
          //              map( response => {
          //                println("req get: " + response.body)
          //              })
*/
          Ok(Json.toJson(Map("status" -> res,
            "newfen" -> GameEngine.getFen(request.session),
            "bestmove" -> res,
//            "oldlegal" -> oldlegal,
//            "newlegal" -> GameEngine.getLegalMoves(request.session, fen),
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

  def saveGame = Action(parse.json) { request =>
    (request.body \ "fen").asOpt[String].map { fen =>
	    (request.body \ "history").asOpt[String].map { history =>
		    (request.body \ "legal").asOpt[String].map { legal =>
            (request.body \ "moves").asOpt[JsArray].map { moves =>
              println("path: " + path)
			      val f = new File(path + "public/saves/" + request.session.hashCode + "_" + new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(Calendar.getInstance.getTime) +
			          ".chess")
              println("file: " + f.getAbsolutePath)
			      if (f.createNewFile) {
			    	  val output = Resource fromFile f.getAbsolutePath

              val moveLst = for {
                move <- moves.value
              } yield "\r\t<move note=" + (move \ "note") + ">\n\t\t<fen>" + (move \ "fen").as[String] + "</fen>\n\t\t<legal>"  +
//                  (move \ "legal") +
                  (move \ "legal").as[JsArray].value.map(Json.stringify(_).replace("\"", "")).reduce((x,y)=>x+" "+y) +
                  "</legal>\n\t</move>"

              val gameLst = List("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n",
                "<game>\n",
                "<fen>" + fen + "</fen>\n",
                "<history>" + history + "</history>\n",
                "<legal>" + legal + "</legal>\n")

              output.writeStrings(gameLst:::"<moves>"::moveLst.toList:::List("\r</moves>\n"):::List("</game>"))(scalax.io.Codec.UTF8)

			      }
			      Ok(f.getName)
            }.getOrElse { BadRequest("Missing parameter [moves]") }
		    }.getOrElse { BadRequest("Missing parameter [legal]") }
	    }.getOrElse { BadRequest("Missing parameter [history]") }
    }.getOrElse { BadRequest("Missing parameter [fen]") }
  }

  def loadGame = Action(parse.multipartFormData) { request =>
    request.body.file("gamefile").map { gamefile =>
    import java.io.File
    val filename = gamefile.filename 
    val contentType = gamefile.contentType
    val f = new File("/tmp/" + new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(Calendar.getInstance().getTime) + ".xml")
    gamefile.ref.moveTo(f)
    try {
    	val game = scala.xml.XML.load(scala.xml.Source.fromFile(f))
		val fen = (game \ "fen").text
		val history = (game \ "history").text
		val legal = (game \ "legal").text
    val moves = game \ "moves" \\ "move"

      moves.foreach((z)=>println(z + "\nELEMENT"))
      val moves2 = Json.arr(
        moves.map((move) => {
        Json.obj(
          "note" -> move.attribute("note").get.toString,
          "fen" -> (move \ "fen").text.toString,
          "legal" -> (move \ "legal").text.toString
        )
        }
        )
      )

    val moves3 = Nil

    val notes = moves
      if (GameEngine.exist(request.session)) {
        GameEngine.newGame(request.session)
        f.delete
          Ok(Json.toJson(
            Map("status" -> "OK",
                "data" -> "File uploaded",
                "error" -> "",
                "fen" -> fen,
                "history" -> history,
                "moves" -> Json.stringify(moves2),
                "newlegal" -> legal)))
      } else {
    		Ok(Json.toJson( Map("status" -> "Unsuccess",
				    "data" -> "Id not found",
					"error" -> "ID not found")))
    	}
	} catch {
      case e : Exception =>
        println("Exception: " + e.getMessage())
        Ok(Json.toJson(
          Map("status" -> "Unsuccess",
              "data" -> "File doesn't uploaded",
            "error" -> e.getMessage())))
      }
      }.getOrElse {
        Ok(Json.toJson(
            Map("status" -> "Unsuccess",
                "data" -> "File not found",
              "error" -> "File not found")))
      }
  }


  def stopPonder = Action { request =>
    GameEngine.send(request.session, "stop")
    Ok("Stop ponder")
  }
}
