package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/26/15.
 */
// pawn uses segmentation of the LegalMoves Vector<PawnPromote|LineMoves>; ie,
    // different index ranges for different types.
    // the index ranges are fixed. Thus,
    // 0-11 promotion : 12 en passant : 13 advance (2) : 14+ regular
    // Move.isLegal is modified by findPsuedoLegalMoves to short for findLegalMoves



public class Pawn extends Chessman {
    protected boolean hasPromote;
    private int pinnedDirection;
    private  int[] promotePiece;
    private final int home, promote, ep, ep2;
    private final int advance, diag1, diag2, left;
    private static final int[] PROMOTE_PIECE_W = {0, 2, 4, 6};
    private static final int[] PROMOTE_PIECE_B = {1, 3, 5, 7};
    protected static final int PROMOTE_SEGMENT_LENGTH = 12;

    protected Pawn(ChessBoard b, int rank, int file,
                   boolean w) {
        super(b, rank, file, w);
        super.setChessSymbolInt(super.isWhite ? 8 : 9);
        super.setRelativeValue();

        if (isWhite) {
            promotePiece = PROMOTE_PIECE_W;
            home = 2;
            promote = 7;
            ep = 3; // set the ep flag by advance by 2
            ep2 = 5; // capture via en passant
            advance = 1;
            diag1 = -4;
            diag2 = 3;
            left = -2;
        } else {
            promotePiece = PROMOTE_PIECE_B;
            home = 7;
            promote = 2;
            ep = 6; // set the ep flag by advance by 2
            ep2 = 4; // capture via en passant
            advance = -1;
            diag1 = 4;
            diag2 = -3;
            left = 2;
        }

        initializeMoves();
    }

    // post condition: LegalMoves has size at least 13; LegalMoves is segmented
    private void initializeMoves() {
        // pawn promotion moves
        for (int aPromotePiece : promotePiece)
            for (int j = 0; j < 3; j++)
                legalMove.add(new PawnPromote(aPromotePiece));

        // pawn en passant
        legalMove.add(new LineMove());

        // pawn advance by (2)
        legalMove.add(new LineMove());

        // pawn regular moves
        for (int i = PROMOTE_SEGMENT_LENGTH + 2; i < legalMove.capacity(); i++)
            legalMove.add(new LineMove());
    }

    protected void resetRestrictedMovesFlags() { pinnedDirection = Bishop.NULL_PIN; }

    // post condition: hasPromote is true in the PsuedoLegal sense
    // modifies: LegalMoves.Move.isLegal
    protected void findPsuedoLegalMoves() {
        findPsuedoLegalMoves_Diagonal();
        findPsuedoLegalMoves_Forward();
    }

    protected void updateRestrictedMovesFlags(int direction) { pinnedDirection = direction; }

    // usages: call before findPsuedoLegalMoves_Forward
    private void findPsuedoLegalMoves_Diagonal() {
        resetNumberOfMoves();
        hasPromote = false;

        boolean en_passant = false;

        // isLegal in the front segment of legalMoves is a filter for "legal moves"
        // Thus it is reset, so that later it can be updated
        resetMovesIsLegal();

        //test for diagonal moves: 1.captures  2.en passant
        Square square;
        Chessman chessman;
        int[] direction = {diag1, diag2};
        int[] lateral = {left, -left};

        for (int i = 0; i < 2; i++) {
            try {
                square = generateSquare(direction[i], coordinate);
                chessman = hostBoard.board[square.rank][square.file];

                if (isCaptureMaterial(chessman)) {

                    // the pawn is capturing a Chessman
                    if (chessman instanceof King) {
                        ((King) chessman).checkPiece = this;
                        chessman.updateRestrictedMovesFlags(direction[i]);
                    } else hostBoard.updateThreatenedSquare(square, isWhite);
                } else if (hostBoard.ep != null && square.equals(hostBoard.ep)) {

                    //en passant
                    // check for a lateral pin penetrating both pawns
                    Square s1;
                    try {
                        s1 = new Square(hostBoard.ep.file, coordinate.rank);
                    } catch (Square.InvalidSquareException e) {
                        throw new RuntimeException("faulty logic: en passant");
                    }

                    if ((lateralPin(lateral[i], s1) *
                            lateralPin(-lateral[i], coordinate)) == -1) continue;
                    else en_passant = true;
                } else {

                    // the pawn is attacking air
                    hostBoard.updateThreatenedSquare(square, isWhite);
                    continue;
                }

            } catch (Square.InvalidSquareException e) { continue;}


            // add the move to legalMoves
            if (coordinate.rank == promote) {
                hasPromote = true;
                for (int j = 0; j < promotePiece.length; j++)
                    ((PawnPromote) legalMove.get(i + 3 * j)).
                            set(square, true, direction[i]);
            } else if (en_passant) ((LineMove) legalMove.get(PROMOTE_SEGMENT_LENGTH)).
                    set(square, true, direction[i]);

            else {
                try {
                    ((LineMove) legalMove.get(numberOfMoves++)).set(square, direction[i]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    legalMove.add(new LineMove(square, direction[i]));
                }
            }
        }
    }

    // precondtion: findPsuedoLegalMoves_Diagonal()
    private void findPsuedoLegalMoves_Forward() {
        boolean isOnHome = coordinate.rank == home;

        //test for forward moves: advance (1) square, advance (2) square
        Square square = coordinate;
        Chessman chessman;

        for (int i = 0; i < (isOnHome ? 2 : 1); i++) {
            try {
                square = generateSquare(advance, square);
                chessman = hostBoard.board[square.rank][square.file];

                if (chessman == null) {

                    // the square in front of the pawn is vacant
                    // add the move to legalMoves
                    if (i == 0) {
                        if (coordinate.rank == promote) {
                            hasPromote = true;
                            for (int j = 0; j < promotePiece.length; j++)
                                ((PawnPromote) legalMove.get(2 + 3 * j)).
                                        set(square, true, advance);
                        } else {
                            try {
                                ((LineMove) legalMove.get(numberOfMoves++)).set(square, advance);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                legalMove.add(new LineMove(square, advance));
                            }
                        }
                    } else if (coordinate.rank == home) {

                        // advance the pawn by (2) squares
                        ((LineMove) legalMove.get(numberOfMoves++)).set(square, true, advance);
                    }
                } else break;

            } catch (Square.InvalidSquareException e) { }
        }
    }

    // result: Let x = the adjacent chessman to square in the direction 'direction'
    //   then [x is a line piece of opposite color] -> 1,
    //   [x is a king of same color] -> -1,
    //   [x is neither of the above cases] -> 0
    //
    // usage: intended for en passant, since normal pins are set by the other Chessmans' calls to
    //          findPsuedoLegalMoves()
    private int lateralPin(int direction, Square square) {
        Chessman chessman;

        while (true) {
            try {
                square = generateSquare(direction, square);
                chessman = hostBoard.board[square.rank][square.file];
                if (chessman == null) continue;

                switch (chessman.getChessSymbolInt()) {
                    case 0:
                    case 1:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        return this.isWhite != chessman.isWhite ? 1 : 0;
                    case 10:
                    case 11:
                        return this.isWhite == chessman.isWhite ? -1 : 0;
                    default:
                        return 0;
                }
            } catch (Square.InvalidSquareException e) { return 0; }
        }
    }


    private void resetNumberOfMoves() {

        // promotions + ep + adv(2) allocated
        numberOfMoves = PROMOTE_SEGMENT_LENGTH + 2;
    }

    // usage: used by findPsuedoLegalMoves
    private void resetMovesIsLegal() {

        for (int i = 0; i < PROMOTE_SEGMENT_LENGTH + 2; i++)
            legalMove.get(i).set(false);
    }

    // it is recommended that blanket conditions are first checked to bypass this:
    // this.isInPlay && this.isWhite == this.hostBoard.turnisWhite && the king is not in
    // double check
    public void findLegalMoves() {

        // compute isLegal for pawn promotions
        if (hasPromote) {

            // reset hasPromote based on a legal promotion
            hasPromote = false;

            PawnPromote promote;
            for (int i = 0; i < PROMOTE_SEGMENT_LENGTH; i++) {
                promote = (PawnPromote)legalMove.get(i);
                if (promote.isLegal) {
                    if ( (pinnedDirection == Bishop.NULL_PIN ||
                            promote.direction == pinnedDirection ||
                            promote.direction == -pinnedDirection) &&
                            defendsKing(promote.square))
                        hasPromote = true;
                    else
                        promote.isLegal = false;
                }
            }
        }

        LineMove move;

        // compute isLegal for en passant
        // compute isLegal for advance by 2
        for (int i = PROMOTE_SEGMENT_LENGTH; i < PROMOTE_SEGMENT_LENGTH + 2; i++) {
            move = (LineMove) legalMove.get(i);
            if (move.isLegal)
                move.isLegal = (pinnedDirection == Bishop.NULL_PIN ||
                        move.direction == pinnedDirection ||
                        move.direction == -pinnedDirection) &&
                        defendsKing(move.square);
        }

        // compute isLegal for regular moves
        // move.isLegal is preset to false, as with the general case
        for (int i = PROMOTE_SEGMENT_LENGTH + 2; i < numberOfMoves; i++) {
            move = (LineMove) legalMove.get(i);
            move.isLegal = (pinnedDirection == Bishop.NULL_PIN ||
                    move.direction == pinnedDirection ||
                    move.direction == -pinnedDirection) &&
                    defendsKing(move.square);
        }
    }

    protected String printMove(int moveIndex) {
        if (moveIndex < PROMOTE_SEGMENT_LENGTH)
            return super.printMove(moveIndex) + Character.toString(
                    hostBoard.CHESSMAN_SYMBOL.charAt(
                            ((PawnPromote) legalMove.get(moveIndex)).promote)).toUpperCase();
        else
            return super.printMove(moveIndex);
    }

    protected void playMove(int moveIndex) {
        // updates the coordinate of this to the square of the move
        super.playMove(moveIndex);

        Chessman[][] board = hostBoard.board;

        if (moveIndex < PROMOTE_SEGMENT_LENGTH) {

            // promotion update
            isInPlay = false;
            board[coordinate.rank][coordinate.file] = hostBoard.generateChessman(
                    ((PawnPromote)legalMove.get(moveIndex)).promote,
                    coordinate.rank,
                    coordinate.file);

        } else {
            switch (moveIndex) {
                case PROMOTE_SEGMENT_LENGTH:

                    // en passant
                    board[this.ep2][coordinate.file].isInPlay = false;
//                    board[coordinate.file][this.ep2] = null;

                    break;
                case PROMOTE_SEGMENT_LENGTH + 1:

                    // advance by 2
                    try {
                        hostBoard.ep = new Square(coordinate.file, this.ep);
                    } catch (Square.InvalidSquareException e) {}

                    break;
            }
        }
    }
}