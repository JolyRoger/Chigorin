package engine

import play.api.mvc.Session
import play.Play


object GameEngine {

  val ENGINES = "/home/torquemada/Softdev/workspace/Chess/public/engines/"
  val OWN_BOOK_PATH = "public/books/performance.bin"
  val STOCKFISH_PATH = "public/engines/stockfish_8_x32.exe"
  val UCI = "uci "
  val UCIOK = "uciok"
  val ISREADY = "isready"
  val READYOK = "readyok"
	val UCINEWGAME = "ucinewgame"
	val GO_INFINITE = "go infinite "
	val POSITION_STARTPOS = "position startpos"
	val MOVES = " moves "
	val POSITION_FEN = "position fen "
	val GO_MOVETIME = "go movetime "
	val BEST_MOVE = "bestmove"
	val STOP = "stop"
  var idMap = emptyMap

  def createID(session: Session) = {
    idMap.get(session) match {
      case None =>
        val engine = new EngineInstance()
        idMap += session -> engine
        engine.process(Play.application.getFile(STOCKFISH_PATH).getAbsolutePath, System.getProperty("os.name").contains("Linux"))
//        engine.stockfishProcess()
        engine.write(UCI)
        engine.read(UCIOK).get
        engine.write(ISREADY)
        engine.read(READYOK).get
    }
	}

	def deleteID(id: Session) = {
    idMap(id).close()
    idMap -= id
    "Success"
	}

	def clearMap {
    idMap = emptyMap
	}

	def newGame(id: Session) = {
    send(id, UCINEWGAME)
    "OK"
	}

	def setFromMoves(id: Session, position: String) {
	  send(id, POSITION_STARTPOS + MOVES + position)
	}
	def setFromFen(id: Session, history: String, fen: String) {
		send(id, POSITION_FEN + fen)
	}

	def send(id: Session, command: String) {
    idMap.get(id) match {
      case Some(engine) => engine.write(command)
      case None => println("there is no key " + id.hashCode() + " in the map!!!")
    }
	}

	def go(id: Session) = {
    idMap(id).write(GO_MOVETIME + " " + idMap(id).getPonderTime)
    idMap(id).read("bestmove").get
	}

	def changeEngine(id: Session, engine: String) {
    idMap(id).changeEngine(engine)
  }

	def setPonderTime(id: Session, time: Int): Unit = {
    idMap(id).setPonderTime(time * 1000)
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

  def exist(session: Session) = {
    idMap(session) != null
  }

	def print {
    "Success"
  }

  implicit def string2Int(s: String): Int = augmentString(s).toInt
}
