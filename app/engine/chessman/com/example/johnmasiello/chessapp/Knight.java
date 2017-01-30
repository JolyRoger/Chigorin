package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/26/15.
 */

public class Knight extends Chessman {
    private boolean isPinned;

    protected Knight(ChessBoard b, int rank, int file,
                     boolean w) {
        super(b, rank, file, w);
        super.setChessSymbolInt(super.isWhite ? 2 : 3);
        super.setRelativeValue();
        initializeMoves();
    }

    protected void findPsuedoLegalMoves() {
        Square square;
        Chessman chessman;
        numberOfMoves = 0;
        for (int i = 1; i < 9; i++) {
            try {
                square = this.generateSquare(i, coordinate);
            } catch (Square.InvalidSquareException e) {
                continue;
            }


            hostBoard.updateThreatenedSquare(square, isWhite);

            // finds check
            chessman = hostBoard.board[square.rank][square.file];
            if (isCapture(chessman)) { // 'loose' definition, meaning null or opposing color
                if (chessman instanceof King) {
                    //update the king's state to reflect this chessman's check on it
                    ((King)chessman).checkPiece = this;
                         chessman.updateRestrictedMovesFlags(i);
                }
            } else continue; // bypass the remaining iteration code, to get to the next 'hop'

            try {
                legalMove.get(numberOfMoves++).set(square);
            } catch (ArrayIndexOutOfBoundsException e) {
                legalMove.add(new Move(square));
            }
        }
    }

    protected Square generateSquare(int hop, Square square)
            throws Square.InvalidSquareException {
        int rank = square.rank; int file = square.file;
        switch (hop) {
            case 1:
                file++;
                rank += 2;
                break;
            case 2:
                file += 2;
                rank++;
                break;
            case 3:
                file += 2;
                rank--;
                break;
            case 4:
                file++;
                rank -= 2;
                break;
            case 5:
                file--;
                rank += 2;
                break;
            case 6:
                file -= 2;
                rank++;
                break;
            case 7:
                file--;
                rank -= 2;
                break;
            case 8:
                file -= 2;
                rank--;
        }
        return new Square(file, rank);
    }

    protected void resetRestrictedMovesFlags() { isPinned = false; }

    protected void updateRestrictedMovesFlags(int direction) { isPinned = true; }

    private void initializeMoves() {
        for (int i = 0; i < legalMove.capacity(); i++)
            legalMove.add(i, new Move());
    }

    // it is recommended that blanket conditions are first checked to bypass this:
    // this.isInPlay && this.isWhite == this.hostBoard.turnisWhite && the king is not in
    // double check
    protected void findLegalMoves() {

        Move move;
        for (int i = 0; i < numberOfMoves; i++) {
            move = legalMove.get(i);
            move.isLegal = !isPinned && defendsKing(move.square);
        }
    }
}
