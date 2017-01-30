package engine

import play.api.mvc.Session


object GameEngine {

  val ENGINES = "/home/torquemada/Softdev/workspace/Chess/public/engines/"
  val OWN_BOOK_PATH = "public/books/performance.bin"
  val STOCKFISH_PATH = "public/engines/stockfish_8_x32.exe"
  val UCI = "uci "
	val UCINEWGAME = "ucinewgame"
	val GO_INFINITE = "go infinite "
	val POSITION_STARTPOS = "position startpos"
	val MOVES = " moves "
	val POSITION_FEN = "position fen "
	val GO_MOVETIME = "go movetime "
	val BEST_MOVE = "bestmove"
	val STOP = "stop"

	var movetime = 5000

  var idMap = emptyMap

  def createID(session: Session) = {
    idMap.get(session) match {
      case None =>
        val engine = new EngineInstance()
        idMap += session -> engine
        engine.process(STOCKFISH_PATH, true)
        engine.write("uci")
        engine.read("uciok").get
        engine.write("isready")
        engine.read("readyok").get
    }
	}

	def deleteID(id: Session) = {
    idMap -= id
    "Success"
	}

	def clearMap {
    idMap = emptyMap
	}

	def newGame(id: Session) = {
    "Success"
	}

	def setFromMoves(id: Session, position: String /*, idMap: Map[Int, Uci]*/) {
	  send(id, POSITION_STARTPOS + MOVES + position)
	}
	def setFromFen(id: Session, history: String, fen: String/*, idMap: Map[Int, Uci]*/) {
		send(id, POSITION_FEN + fen + MOVES + history/*.drop(history.lastIndexOf(' ')+1)*/)
	}

	def send(id: Session, command: String) {
    idMap.get(id) match {
      case Some(engine) => engine.write(command)
      case None => println("there is no key " + id.hashCode() + " in the map!!!")
    }
	}

	def go(id: Session) = {
    idMap(id).write(GO_MOVETIME + " 5000")
    idMap(id).read("bestmove").get
	}

	def setPonderTime(id: Session, time: Int): Unit = {
    idMap(id).setPonderTime(time * 1000)
	}

	def getLegalMoves(id: Session, fen: String) = {
    idMap(id).getLegalMoves(fen)
  }
	def getFen(id: Session) = {
    "Success"
  }

	def isGamover(id: Session) = {
    "PROCESS"
  }

  def emptyMap = {
    Map.empty[Session, EngineInstance]
  }

  def exist(session: Session) = idMap(session) != null

	def print {
    "Success"
  }

  implicit def string2Int(s: String): Int = augmentString(s).toInt
}
