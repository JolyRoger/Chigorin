package engine.chessman.com.example.johnmasiello.chessapp;

public class Main {

    public static void main(String[] args) {
        System.out.println(MainChessApp.getString());
        ChessBoard board = new ChessBoard();
        board.setTheBoardUp();
        board.generateMoves();
        String allMoves = board.toString();
        System.out.println(allMoves);
    }
}
