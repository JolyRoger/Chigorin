package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/26/15.
 */
public class King extends Chessman {
    protected boolean inCheck, inCheck2x;
    protected Square[] checkSquare;
    protected int checkSquareSize;
    protected Chessman checkPiece;
    // castling fields
    protected boolean canCastle_Kingside;
    protected boolean canCastle_Queenside;
    protected Rook rook_Kingside;
    protected Rook rook_Queenside;
    protected static final int CASTLE_SEGMENT_LENGTH = 2;
//    protected static final int NULL_PATH = 0;
    // castle kingside direction map, supporting fischer-random castling
    // direction = CASTLE_K_DIRECTION[index <- position]
//    private static final int[] CASTLE_K_DIRECTION = {
//        NULL_PATH,
//        NULL_PATH, 1, 1, 1, 1, 1, NULL_PATH, NULL_PATH };
//
//    // castle queenside direction map, supporting fischer-random castling
//    private static final int[] CASTLE_Q_DIRECTION = {
//            NULL_PATH,
//            NULL_PATH, 1, NULL_PATH, -1, -1, -1, -1, NULL_PATH };
    private static final int CASTLE_FILE_K = 7;
    private static final int CASTLE_FILE_Q = 3;


    // inteded only to create a searchable Chessman instance
    public King() {

        this.isInPlay = true;
    }

    protected King(ChessBoard b, int rank, int file,
                    boolean w) {
        super(b, rank, file, w);
        super.setChessSymbolInt(super.isWhite ? 10 : 11);
        super.setRelativeValue();
        checkSquare = new Square[BOARD_LENGTH];
        initializeMoves();

        // set castling parameters
        resetDefaultCastleState();
    }

    private void resetDefaultCastleState() {
        canCastle_Kingside = canCastle_Queenside = false;
        rook_Kingside = rook_Queenside = null;
    }

    protected void resetRestrictedMovesFlags() {
        inCheck = inCheck2x = false;
        checkSquareSize = 0;
    }

    // precondition: checkPiece should be set
    protected void updateRestrictedMovesFlags(int direction) {
        if (inCheck2x);
        else if (inCheck) inCheck2x = true;
        else {
            inCheck = true;
            Square square = checkPiece.coordinate;
            do {
                checkSquare[checkSquareSize++] = square;

                // update the square in the direction towards the king
                try { square = checkPiece.generateSquare(direction, square); }
                catch (Square.InvalidSquareException e) {
                    throw new RuntimeException("invalid \"checking\" piece");
                }
            } while (!square.equals(this.coordinate));
        }
    }

    private void initializeMoves() {
        // castling moves
        for (int i = 0; i < CASTLE_SEGMENT_LENGTH; i++)
            legalMove.add(i, new Move());

        // king regular moves
        for (int i = CASTLE_SEGMENT_LENGTH; i < legalMove.capacity(); i++)
            legalMove.add(new Move());
    }

    protected void initializeCastle(Rook rook, boolean isKingside) {
        if(isKingside) {
            canCastle_Kingside = true;
            rook_Kingside = rook;
        } else {
            canCastle_Queenside = true;
            rook_Queenside = rook;
        }
    }

    // finds all moves excluding castle
    protected void findPsuedoLegalMoves() {
        resetNumberOfMoves();

        Square square;
        Chessman chessman;
        int[] direction = {-4, -3, -2, -1, 1, 2, 3, 4};

        for (int aDirection : direction) {
            square = coordinate;

            try {
                square = generateSquare(aDirection, square);
            } catch (Square.InvalidSquareException e) {
                continue;
            }
            hostBoard.updateThreatenedSquare(square, isWhite);

            chessman = hostBoard.board[square.rank][square.file];
            if (isCapture(chessman)) { // 'loose' definition, meaning null or opposing color
                try {
                    legalMove.get(numberOfMoves++).set(square);
                } catch (ArrayIndexOutOfBoundsException e) {
                    legalMove.add(new Move(square));
                }
            }
        }
    }

    private void resetNumberOfMoves() {

        numberOfMoves = CASTLE_SEGMENT_LENGTH;
    }

    protected void breakCastle() {
         canCastle_Kingside = false;
         canCastle_Queenside = false;
    }

    protected void findLegalMoves() {

        // find the kingside castle
        legalMove.get(0).isLegal = false;
        if (canCastle_Kingside && squareIsSafe(coordinate)) findKingsideCastle();


        // find the queenside castle
        legalMove.get(1).isLegal = false;
        if (canCastle_Queenside && squareIsSafe(coordinate)) findQueensideCastle();


        // find isLegal for regular moves
        Move move;
        for (int i = CASTLE_SEGMENT_LENGTH; i < numberOfMoves; i++) {
            move = legalMove.get(i);
            move.isLegal = squareIsSafe(move.square);
        }
    }

    // finds the castle according to fischer random chess
    // note standard chess is a subset o frc
    private void findQueensideCastle() {

        int kingsFile = coordinate.file;

        if (kingsFile > CASTLE_FILE_Q) {
            for (int i = kingsFile - 1; i >= CASTLE_FILE_Q; i--)
                if (!squareIsSafe(i) || !isVacant(i, rook_Queenside)) return;

            if (rook_Queenside.coordinate.file == 1 &&
                    !isVacant(CASTLE_FILE_Q - 1, rook_Queenside)) return;
        }
        else if (kingsFile == CASTLE_FILE_Q) {

            if (!isVacant(CASTLE_FILE_Q - 1, rook_Queenside) ||
                    !isVacant(Rook.CASTLE_FILE_Q, rook_Queenside)) return;
        }

        else {

            // case kingsFile == CASTLE_FILE_Q - 1
            if (!squareIsSafe(CASTLE_FILE_Q) ||
                    !isVacant(CASTLE_FILE_Q, rook_Queenside) ||
                    !isVacant(Rook.CASTLE_FILE_Q, rook_Queenside)) return;
        }

        // add the castle to legalMove, and set isLegal to true (using the setter)
        try {
            legalMove.get(1).set(new Square(CASTLE_FILE_Q, coordinate.rank), true);
        } catch (Square.InvalidSquareException e) {} // is never thrown

    }

    // finds the castle according to fischer random chess
    // note standard chess is a subset o frc
    private void findKingsideCastle() {

        int kingsFile = coordinate.file;

        if (kingsFile < CASTLE_FILE_K) {
            for (int i = kingsFile + 1; i <= CASTLE_FILE_K; i++)
                if (!squareIsSafe(i) || !isVacant(i, rook_Kingside)) return;
        }

        else {
            // case kingsFile == CASTLE_FILE_K
            if (!isVacant(Rook.CASTLE_FILE_K, rook_Kingside)) return;
        }

        // add the castle to legalMove, and set isLegal to true (using the setter)
        try {
            legalMove.get(0).set(new Square(CASTLE_FILE_K, coordinate.rank), true);
        } catch (Square.InvalidSquareException e) {} // is never thrown

    }

    // used for checking any king square
    private boolean squareIsSafe(Square square) {
        return hostBoard.ThreatenedSquare[square.rank][square.file][isWhite ? 0 : 1] == 0;
    }

    // used for checking squares along the castle
    private boolean squareIsSafe(int file ) {
        return hostBoard.ThreatenedSquare[coordinate.rank][file][isWhite ? 0 : 1] == 0;
    }

    // the square located at the file is any of vacant, the king, the rook
    private boolean isVacant(int file, Rook rook) {
        return hostBoard.board[coordinate.rank][file] == null ||
                file == coordinate.file ||
                file == rook.coordinate.file;
    }

    protected String printMove(int moveIndex) {
        if (moveIndex < CASTLE_SEGMENT_LENGTH) {
            char ver = isWhite ? '1' : '8';
            return "e" + ver + (moveIndex == 0 ? 'g' : 'c') + ver;
        } else return super.printMove(moveIndex);
    }

    protected void playMove(int moveIndex) {
        super.playMove(moveIndex);

        breakCastle();

        Chessman [][] board = hostBoard.board;
        Rook rook;
        int rank; int file, castleFile;

        // move the rook if a castling move
        if (moveIndex < CASTLE_SEGMENT_LENGTH) {
            if (moveIndex == 0) {
                rook = rook_Kingside;
                castleFile = Rook.CASTLE_FILE_K;
            } else {
                rook = rook_Queenside;
                castleFile = Rook.CASTLE_FILE_Q;
            }

            rank = rook.coordinate.rank; file = rook.coordinate.file;
            board[rank][file] = null;
            board[rank][castleFile] = rook;

            // in fischer-random castling, there is the slight possiblity super.playMove
            // sets rook.isInPlay to false. That is corrected here, to invariably true
            rook.isInPlay = true;

            try {
                rook.coordinate = new Square(castleFile, rank);
            } catch (Square.InvalidSquareException e) {}
        }
    }
}
