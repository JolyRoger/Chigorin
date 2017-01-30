package engine.chessman.com.example.johnmasiello.chessapp;

import java.util.Vector;

/**
 * Created by john on 5/24/15.
 */
public abstract class Chessman {
    protected static final int BOARD_LENGTH = 8;
    protected static final int MAX_NUMBER_OF_CHESSMAN = BOARD_LENGTH ^ 2;

    /* theoretical number of most moves:
                queen 27
                rook 14
                bishop 13
                pawn 12
                king 9
                knight 8
                */
    // Hence, MAX_NUMBER_OF_MOVES = 27
    protected static final int MAX_NUMBER_OF_MOVES = 27;
    protected ChessBoard hostBoard;
    protected Square coordinate;
    protected boolean isWhite;
    protected boolean isInPlay;
    protected Vector<Move> legalMove;
    protected int numberOfMoves;
    private double relativeValue;
    private int chessSymbolInt;


    protected Chessman() {
    }

    // modifes hostBoard.chessmanNotInitialized
    protected Chessman(ChessBoard b, int rank, int file,
                       boolean w) {
        hostBoard = b;
        try {
            coordinate = new Square(file, rank);
        } catch (Square.InvalidSquareException E) {
            coordinate = new Square();
            hostBoard.chessmanNotInitialized = true;
        }
        isWhite = w;
        isInPlay = true;
        legalMove = new Vector<Move>(MAX_NUMBER_OF_MOVES);
        numberOfMoves = 0;
        //initializeMoves();
        //chessSymbolInt = 0; // a valid index to toString does not throw an outOfBounds
    }

    public boolean equals(Object obj) {
        Chessman chessman = (Chessman)obj;
        return chessman.isInPlay && this.isInPlay && this.coordinate.equals(chessman.coordinate);
    }

    public String toString() {
        return Character.toString(hostBoard.CHESSMAN_SYMBOL.charAt(chessSymbolInt));
    }

    protected void setChessSymbolInt(int symbolInt) {
        this.chessSymbolInt = symbolInt;
    }

    protected int getChessSymbolInt() { return chessSymbolInt; }

    //precondition: setChessSymbolInt()
    protected void setRelativeValue() {
        relativeValue = hostBoard.RELATIVE_VALUE[chessSymbolInt];
    }

    protected double getRelativeValue() {
        return relativeValue;
    }

    // this would be an extension method in Playable, using java 8 -not supported by android
    protected Square generateSquare(int direction, Square square)
            throws Square.InvalidSquareException {
        int rank = square.rank;
        int file = square.file;
        switch (direction) {
            case 3:
                file++;
            case 1:
                rank++;
                break;
            case 4:
                rank--;
            case 2:
                file++;
                break;
            case -3:
                file--;
            case -1:
                rank--;
                break;
            case -4:
                rank++;
            case -2:
                file--;
        }
        return new Square(file, rank);
    }

    protected boolean isCapture(Chessman c) { return c == null || this.isWhite != c.isWhite;}

    protected boolean isCaptureMaterial(Chessman c) {
        return c != null && this.isWhite
                != c.isWhite;
    }

    protected boolean defendsKing(Square square) {
        if (hostBoard.isInCheck) {
            for (int i = 0; i < hostBoard.checkSquareSize; i++)
                if (square.equals(hostBoard.checkSquare[i])) return true;
            return false;
        }

        // vacuous defense
        return true;
    }

//    protected boolean isVacant(Chessman c) { return c == null; }

    protected abstract void findPsuedoLegalMoves();

    protected abstract void updateRestrictedMovesFlags(int direction);

    protected abstract void resetRestrictedMovesFlags();

    protected abstract void findLegalMoves();

    protected String printMove(int moveIndex) {
        return coordinate.getAlphaNumeral() +
                legalMove.get(moveIndex).square.getAlphaNumeral();
    }

    protected void playMove(int moveIndex) {
        Move move = legalMove.get(moveIndex);
        Square square = move.square;
        Chessman[][] board = hostBoard.board;
        Chessman chessman;

        // This move is generally not a pawn advance
        hostBoard.ep = null;

        // This move captures a chessman
        chessman = board[square.rank][square.file];
        if(chessman != null) {
            chessman.isInPlay = false;
            if (chessman instanceof Rook)
                ((Rook)chessman).breakCastle();
        }

        // This move moves the chessman off of its square
        board[coordinate.rank][coordinate.file] = null;
        coordinate = square; //square.clone(); all fields of square are final, so clone is unneeded
        board[coordinate.rank][coordinate.file] = this;
    }

    protected int getNumberOfLegalMoves() {
        int n = 0;
        for (int i = 0; i < numberOfMoves; i++)
                if (legalMove.get(i).isLegal)
                    n++;
        return n;
    }

    // usage: used by ChessBoard.getLegalMoves
    protected void resetMovesToNotLegal() {

        for (int i = 0; i < numberOfMoves; i++)
            legalMove.get(i).set(false);
    }
}
