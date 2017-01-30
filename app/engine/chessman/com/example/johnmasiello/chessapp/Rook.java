package engine.chessman.com.example.johnmasiello.chessapp;

import engine.chessman.com.example.johnmasiello.chessapp.Bishop;

/**
 * Created by john on 5/26/15.
 */
public class Rook extends Chessman {
    private int pinnedDirection;
    private boolean isKingside;
    protected King castlingKing;
//    private static final int NULL_PATH = King.NULL_PATH;
    // castle kingside direction map, supporting fischer-random castling
    // direction = CASTLE_K_DIRECTION[index <- position]
//    protected static final int[] CASTLE_K_DIRECTION = {
//            NULL_PATH,
//            NULL_PATH, NULL_PATH, 1, 1, 1, NULL_PATH, -1, -1 };
//
//    // castle queenside direction map, supporting fischer-random castling
//    protected static final int[] CASTLE_Q_DIRECTION = {
//            NULL_PATH,
//            1, 1, 1, NULL_PATH, -1, -1, NULL_PATH, NULL_PATH };
    protected static final int CASTLE_FILE_K = 6;
    protected static final int CASTLE_FILE_Q = 4;

    protected Rook(ChessBoard b, int rank, int file,
                   boolean w) {
        super(b, rank, file, w);
        super.setChessSymbolInt(super.isWhite ? 4 : 5);
        super.setRelativeValue();
        initializeMoves();

        // set castling parameters
        resetDefaultCastleState();
    }

    private void resetDefaultCastleState() {
        castlingKing = null;
    }

    protected void resetRestrictedMovesFlags() { pinnedDirection = Bishop.NULL_PIN; }

    protected void updateRestrictedMovesFlags(int direction) { pinnedDirection = direction; }

    protected void findPsuedoLegalMoves() {
        Square square;
        Chessman chessman;
        Chessman pinnedChessman = this; // satifies initilize; otherwise, unneeded
        boolean isCheck, isPinned;
        numberOfMoves = 0;
        int[] direction = {-2, -1, 1, 2};
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

    protected void initializeCastle(King king, boolean isKingside) {
        castlingKing = king;
        this.isKingside = isKingside;
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

    protected void breakCastle() {
        try {
            if (isKingside)
                castlingKing.canCastle_Kingside = false;
            else
                castlingKing.canCastle_Queenside = false;
        } catch (NullPointerException e) {} // this rook is not a castling rook
    }

    // precondition: castlingKing != null
    protected boolean canCastle() {
        try {
            if (isKingside)
                return castlingKing.canCastle_Kingside;
            else
                return castlingKing.canCastle_Queenside;
        } catch (NullPointerException e) {
            throw new RuntimeException("This rook is not a castling rook");
        }
    }

    protected void playMove(int moveIndex) {
        super.playMove(moveIndex);
        breakCastle();
    }
}
