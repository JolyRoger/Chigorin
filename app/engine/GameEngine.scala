package engine

import play.api.mvc.Session

object GameEngine {

  val ENGINES = "/home/torquemada/Softdev/workspace/Chess/public/engines/"
  val OWN_BOOK_PATH = "public/books/performance.bin"
  val STOCKFISH_PATH = "public/engines/stockfish_8_x32.exe"
  val UCINEWGAME = "ucinewgame"
  val POSITION_STARTPOS = "position startpos"
  val MOVES = " moves "
  val POSITION_FEN = "position fen "
  val BEST_MOVE = "bestmove"

  var idMap = emptyMap

  def createID(id: Session) = {
    idMap.get(id) match {
      case None =>
        val engine = new EngineInstance("Stockfish")
        idMap += id -> engine
      case Some(_) =>
    }
  }

  def deleteID(id: Session) = {
    if (idMap.contains(id)) {
      idMap(id).close()
      idMap -= id
    }
    "Success"
  }

  def newGame(id: Session) = {
    send(id, UCINEWGAME)
    "OK"
  }


  def send(id: Session, command: String): Unit = {
    idMap.get(id) match {
      case Some(engine) => engine.write(command)
      case None => println("there is no key " + id.hashCode() + " in the map!!!")
    }
  }

  def startAnalysis(id: Session, fen: String) = idMap(id).startAnalysis(fen)
  def stopAnalysis(id: Session) = idMap(id).stopAnalysis()
  def go(id: Session) = idMap(id).go("bestmove")
  def isAnalysisMode(id: Session) = idMap(id).analysisMode
  def getAnalysis(id: Session) = idMap(id).getAnalysis
  def changeEngine(id: Session, engine: String) = idMap(id).changeEngine(engine)
  def setPonderTime(id: Session, time: Int) = idMap(id).ponderTime(time)
  def setAnalysisLines(id: Session, lines: Int) = idMap(id).analysisLines(lines)
  def getFen(id: Session) = "Success"
  def isGamover(id: Session) = "PROCESS"
  def emptyMap = Map.empty[Session, EngineInstance]
  def exist(session: Session) = idMap(session) != null
  def setFromMoves(id: Session, position: String) = send(id, POSITION_STARTPOS + MOVES + position)
  def setFromFen(id: Session, fen: String) = send(id, POSITION_FEN + fen)
  def clearMap = idMap = emptyMap
  def getBestMove(id: Session) = idMap(id).getBestMove

  implicit def string2Int(s: String): Int = augmentString(s).toInt
}
