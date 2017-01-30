package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/26/15.
 */
public class Queen extends Chessman {
    private int pinnedDirection;

    protected Queen(ChessBoard b, int rank, int file,
                    boolean w) {
        super(b, rank, file, w);
        super.setChessSymbolInt(super.isWhite ? 6 : 7);
        super.setRelativeValue();
        initializeMoves();
    }

    protected void resetRestrictedMovesFlags() { pinnedDirection = Bishop.NULL_PIN; }

    protected void updateRestrictedMovesFlags(int direction) { pinnedDirection = direction; }

    protected void findPsuedoLegalMoves() {
        Square square;
        Chessman chessman;
        Chessman pinnedChessman = this; // satifies initilize; otherwise, unneeded
        boolean isCheck, isPinned;
        numberOfMoves = 0;
        int[] direction = {-4, -3, -2, -1, 1, 2, 3, 4};
        //int rank, file;
        for (int aDirection : direction) {
            square = coordinate;
            isCheck = false;
            isPinned = false;

            while (true) {
                try {
                    square = generateSquare(aDirection, square);
                } catch (Square.InvalidSquareException e) {
                    break;
                }


                chessman = hostBoard.board[square.rank][square.file];

                // find pins or checks
                // ThreatenedSquare is incremented only if the 'move' has scope along the line
                if (isCaptureMaterial(chessman)) {
                    if (isCheck) { // forms a line through king, giving check, terminates on
                        // a capturable chessman
                        hostBoard.updateThreatenedSquare(square, isWhite);
                        break;
                    } else if (chessman instanceof King) { // forms a line through king
                        if (isPinned) { // pin; otherwise check
                            pinnedChessman.updateRestrictedMovesFlags(aDirection);
                            break;
                        }
                        //update the king's state to reflect this chessman's check on it
                        ((King)chessman).checkPiece = this;
                        chessman.updateRestrictedMovesFlags(aDirection);
                        isCheck = true;
                    } else if (isPinned) { // forms a line through a capturable chessman, and
                        //terminates on another capturable chessman
                        break;
                    } else { // forms a line through a capturable chessman
                        hostBoard.updateThreatenedSquare(square, isWhite);
                        pinnedChessman = chessman;
                        isPinned = true;
                    }
                } else if (isCapture(chessman)) { // an empty square
                    if (isCheck) {
                        hostBoard.updateThreatenedSquare(square, isWhite);
                        continue;
                    } else if (isPinned) {
                        continue;
                    } else
                        hostBoard.updateThreatenedSquare(square, isWhite);
                } else if (isPinned) { // terminates on same color chessman
                    break;
                } else { // terminates on same color chessman and is in either state check or
                    // neither
                    hostBoard.updateThreatenedSquare(square, isWhite);
                    break;
                }

                try {
                    ((LineMove) legalMove.get(numberOfMoves++)).set(square, aDirection);
                } catch (ArrayIndexOutOfBoundsException e) {
                    legalMove.add(new LineMove(square, aDirection));
                }
            }
        }
    }

    private void initializeMoves() {
        for (int i = 0; i < legalMove.capacity(); i++)
            legalMove.add(i, new LineMove());
    }

    // it is recommended that blanket conditions are first checked to bypass this:
    // this.isInPlay && this.isWhite == this.hostBoard.turnisWhite && the king is not in
    // double check
    protected void findLegalMoves() {

        LineMove move;
        for (int i = 0; i < numberOfMoves; i++) {
            move = (LineMove) legalMove.get(i);
            move.isLegal = (pinnedDirection == Bishop.NULL_PIN ||
                    move.direction == pinnedDirection ||
                    move.direction == -pinnedDirection) &&
                    defendsKing(move.square);
        }
    }
}
