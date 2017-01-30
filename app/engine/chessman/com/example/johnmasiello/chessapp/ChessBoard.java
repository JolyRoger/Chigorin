package engine.chessman.com.example.johnmasiello.chessapp;

import java.util.Vector;

/**
 * Created by john on 5/24/15.
 */


// invariant: chessman.isInPlay && chessman.isWhite == turnIsWhite implies
    // chessman.legalMove.get.isLegal is valid, for all chessman in chessArmy

public class ChessBoard {
    protected boolean turnIsWhite;
    protected Chessman[][] board;
    protected int ThreatenedSquare[][][];
    private Vector<Chessman> chessArmy;
    protected boolean chessmanNotInitialized;
    protected Square ep;
    protected boolean isInCheck;
    protected Square[] checkSquare;
    protected int checkSquareSize;
    private King searchableChessman; // used to make searches on Chessman
    private Move searchableMove;
    private PawnPromote searchablePromotion; // uses Move as base class
    private int indexOfChessman;
    private int indexOfMove;
    private double evaluation;
    private boolean resign;
    private Rook K, Q, k, q;

     /*A fen record contains six fields. The separator between fields is a space. The fields
     are:

    Piece placement (from white's perspective). Each rank is described, starting with rank 8
    and ending with rank 1; within each rank, the contents of each square are described from
    file "a" through file "h". Following the Standard Algebraic Notation (SAN), each piece is
    identified by a single letter taken from the standard English names (pawn = "P", knight =
    "N", bishop = "B", rook = "R", queen = "Q" and king = "K").[1] White pieces are designated
    using upper-case letters ("PNBRQK") while black pieces use lowercase ("pnbrqk"). Empty
    squares are noted using digits 1 through 8 (the number of empty squares), and "/" separates
    ranks.
    Active color. "w" means White moves next, "b" means Black.
    Castling availability. If neither side can castle, this is "-". Otherwise, this has one or
     more letters: "K" (White can castle kingside), "Q" (White can castle queenside), "k"
     (Black can castle kingside), and/or "q" (Black can castle queenside).
    En passant target square in algebraic notation. If there's no en passant target square,
    this is "-". If a pawn has just made a two-square move, this is the position "behind" the
    pawn. This is recorded regardless of whether there is a pawn in position to make an en
    passant capture.[2]
    Halfmove clock: This is the number of halfmoves since the last capture or pawn advance.
    This is used to determine if a draw can be claimed under the fifty-move rule.
    Fullmove number: The number of the full move. It starts at 1, and is incremented after
    Black's move.
    */
    private String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//    private final String fen = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
//     private final String fen = "8/1q6/p2p4/K1pPq3/P3k3/8/8/8 w - c6 0 1";
//    private final String fen = "8/1q6/p2p4/K1pPn3/P3k3/8/8/8 w - c6 0 1";
//     private final String fen = "2rqn3/1PPP1k2/2B5/8/4K3/8/8/8 w - - 0 1";
//    private final String fen = "8/8/2k3rR/3q2P1/6N1/5Q2/6K1/7r b - - 0 1";
//     private final String fen = "5q2/5k2/8/8/4b2N/2P1b3/1P1P4/R1KR4 w KQ - 0 1";
    protected final String CHESSMAN_SYMBOL = "BbNnRrQqPpKk";
    protected final int[] RELATIVE_VALUE =
            {-3, 3, -3, 3, -5, 5, -9, 9, -1, 1, -100, 100};
    private final char TURN_LITERAL = 'w';

    public ChessBoard() {
        board = new Chessman[Chessman.BOARD_LENGTH + 1]
                [Chessman.BOARD_LENGTH + 1];
        ThreatenedSquare = new int[Chessman.BOARD_LENGTH + 1]
                [Chessman.BOARD_LENGTH + 1][2];
        chessArmy = new Vector<>(Chessman.MAX_NUMBER_OF_CHESSMAN);
        // more initialization
        searchableChessman = new King();
        searchableMove = new Move(new Square(), true);
        searchablePromotion = new PawnPromote();
        searchablePromotion.set(true); // set isLegal to true
        resign = false;
    }

    public boolean setTheBoardUp() { return setUpBoard();}

    private boolean setUpBoard() {
        int endOf_Field = fen.indexOf(' ');
        String firstField = fen.substring(0, endOf_Field);
        Chessman chessman;

        // make sure there is the correct number of kings
        boolean exactly1King_W, exactly1King_B;
        exactly1King_W = exactly1King_B = false;

        // find the rooks that correspond to the castle flag
        // Rook K, Q, k, q;
        K = Q = k = q = null;

        // flag is thrown true if the Chessman Type constructor fails
        chessmanNotInitialized = false;

        char c;
        for (int i=0, rank = Chessman.BOARD_LENGTH, file = 1; i < firstField.length(); i++) {
            c = firstField.charAt(i);
            if (c > '0' && c < '9') {
                file += c - '0';
                continue;
            }
            else if (c == '/') {
                rank--;
                file = 1;
                continue;
            }
            int chessmanInt = CHESSMAN_SYMBOL.indexOf(c);
            switch (chessmanInt) {
                case 0:
                case 1:
                    chessman = new Bishop(this, rank, file, (chessmanInt % 2) == 0);
                    break;
                case 2:
                case 3:
                    chessman = new Knight(this, rank, file, (chessmanInt % 2) == 0);
                    break;
                case 4:
                    chessman = new Rook(this, rank, file, true);

                    // get pointers for the rook, to aid in castling
                    if (rank == 1) {
                        if (Q == null) Q = (Rook)chessman;
                        else K = (Rook)chessman;
                    }

                    break;
                case 5:
                    chessman = new Rook(this, rank, file, false);

                    // get pointers for the rook, to aid in castling
                    if (rank == Chessman.BOARD_LENGTH) {
                        if (q == null) q = (Rook)chessman;
                        else k = (Rook)chessman;
                    }

                    break;
                case 6:
                case 7:
                    chessman = new Queen(this, rank, file, (chessmanInt % 2) == 0);
                    break;
                case 8:
                case 9:
                    chessman = new Pawn(this, rank, file, (chessmanInt % 2) == 0);
                    break;
                case 10:
                    // there should only be one of each king
                    if (exactly1King_W) return false;
                    else exactly1King_W = true;

                    chessman = new King(this, rank, file, true);
                    break;
                case 11:
                    // there should only be one of each king
                    if (exactly1King_B) return false;
                    else exactly1King_B = true;

                    chessman = new King(this, rank, file, false);
                    break;
                default:
                    return false;
            }
            //add chessman to chessArmy, upholding kings are ordered first in the vector
            if (chessmanNotInitialized)
                return false;
            if (chessmanInt < 10)
                chessArmy.add(chessman); // append chessman
            else if (chessmanInt == 10)
                chessArmy.add(0, chessman); //insert at beginning chessman
            else
                chessArmy.add(chessArmy.size()!=0 && chessArmy.firstElement() instanceof King ?
                        1: 0, chessman); // insert at beginning or immediately after white king


            board[rank][file++] = chessman;
        }

        if (!exactly1King_W || !exactly1King_B)
            return false;


        // load the second field of fen: turn
        endOf_Field++;
        turnIsWhite = (fen.charAt(endOf_Field) | 32 ) == TURN_LITERAL;
        endOf_Field++;

        // set castle flag
        int startOf_Field = ++endOf_Field;
        endOf_Field = fen.indexOf(' ', startOf_Field);
        String thirdField = fen.substring(startOf_Field, endOf_Field);
        setCastleStates(thirdField); //, K, Q, k, q);

        // set en passant flag
        startOf_Field = ++endOf_Field;
        endOf_Field = fen.indexOf(' ', startOf_Field);
        String fourthField = fen.substring(startOf_Field, endOf_Field);
        ep = fourthField.length() == 1 ? null : new Square(fourthField);
        return true;
    }

    private void setCastleStates(String flags) { //, Rook K, Rook Q, Rook k, Rook q) {

        /*
        // check for faulty strings with more than 1 instance of the same castle
        boolean K_instance, Q_instance, k_instance, q_instance;
        K_instance=Q_instance=k_instance=q_instance = false;
        */


        for (int i = 0; i < flags.length(); i++) {

            try {
                switch (flags.charAt(i)) {
                    case 'K':
                        K.initializeCastle((King)chessArmy.get(0), true);
                        ((King)chessArmy.get(0)).initializeCastle(K, true);
                        break;
                    case 'Q':
                        Q.initializeCastle((King)chessArmy.get(0), false);
                        ((King)chessArmy.get(0)).initializeCastle(Q, false);
                        break;
                    case 'k':
                        k.initializeCastle((King)chessArmy.get(1), true);
                        ((King)chessArmy.get(1)).initializeCastle(k, true);
                        break;
                    case 'q':
                        q.initializeCastle((King)chessArmy.get(1), false);
                        ((King)chessArmy.get(1)).initializeCastle(q, false);
                        break;
                    default:
                }
            } catch (NullPointerException e) {}
        }
    }

    private String getCastleFlagString() {
        Rook[] r = { K, Q, k, q};
        char[] flag = {'K', 'Q', 'k', 'q'};
        String c = "";

        for (int i = 0; i < 4; i++)
            c += r[i] != null && r[i].canCastle() ? flag[i] : "";

        return c;
    }

    protected void resetChessBoard() {
        resetBoard();
        chessArmy = new Vector<Chessman>(Chessman.MAX_NUMBER_OF_CHESSMAN);
        // more initialization
        chessmanNotInitialized = false;
        resign = false;
        if (!setUpBoard()) throw new RuntimeException("Failed to set up the board;" +
                "possible faulty FEN string");
    }

    private void resetBoard() {

        for (int i = 1; i < Chessman.BOARD_LENGTH; i++)
            for (Chessman chessman : board[i]) chessman = null;
    }

    protected boolean makeAMove(String move) {
        if (locateMove(move)) {

            // proceed with making the move
            playMove();
            return true;
        } else if (resign) {
            // game termination code
        }
        return false;
    }

    // precondtion: isALegalMove == true
    // HINT : -> indexOfChessman, indexOfMove is correct
    protected double getMoveEvaluation() {
        return chessArmy.elementAt(indexOfChessman).
                legalMove.elementAt(indexOfMove).evaluation;
    }

    protected String makeAnEngineMove() {

        // select the move, using an evaluation for each legal move
        // code goes here

        String getTheMoveString = chessArmy.get(indexOfChessman).printMove(indexOfMove);
        playMove();
        return getTheMoveString;
    }

    // sets both indexOfChessman, indexOfMove
    // returns moveAtoB was found in the legalMoves of a chessman
    private boolean locateMove(String moveAtoB) {
        switch (moveAtoB.length()) {
            case 2: //kingside castle: oo
                // kings are the the start of the vector
                indexOfChessman = turnIsWhite ? 0 : 1;

                if (chessArmy.elementAt(indexOfChessman).legalMove.
                        elementAt(0).isLegal) {

                    indexOfMove = 0;
                    return true;
                } else {
                    indexOfMove = indexOfChessman = -1;
                    return false;
                }
            case 3: //queenside castle: ooo

                indexOfChessman = turnIsWhite ? 0 : 1;

                if (chessArmy.elementAt(indexOfChessman).legalMove.
                        elementAt(1).isLegal) {

                    indexOfMove = 1;
                    return true;
                } else {
                    indexOfMove = indexOfChessman = -1;
                    return false;
                }
            case 4: //regular move: example: a1d1 -meaning 'from a1 to d1'
                Square searchFrom = new Square(moveAtoB.substring(0,2));
                Square searchTo = new Square(moveAtoB.substring(2,4));
                indexOfChessman = locateChessman(searchFrom);
                indexOfMove = locateLegalMove(searchTo);
                return indexOfMove != -1;
            case 5: //pawn promotion: example: a7a8Q -meaning from a7 to a8 promotes to queen'
                searchFrom = new Square(moveAtoB.substring(0,2));
                searchTo = new Square(moveAtoB.substring(2,4));
                char promotion = moveAtoB.charAt(4);
                indexOfChessman = locateChessman(searchFrom);
                indexOfMove = ((Pawn)chessArmy.get(indexOfChessman)).hasPromote ?
                        locateLegalPromotion(searchTo, promotion) : -1;
                return indexOfMove != -1;
            case 6: //resign: 'resign'
                resign = true;
                return false;
            default:
                    return false;
        }

    }

    private int locateChessman(Square search) {
        searchableChessman.coordinate = search;

        int foundIndex = chessArmy.indexOf(searchableChessman);

        // maintains the invariant that this chessman has color of turn -> move.isLegal is valid
        // the other part of the invariant, specifically chessman.isInPlay, is maintained by
        // chessman.equals()
        return foundIndex == -1 ? -1 :
                (chessArmy.get(foundIndex).isWhite == turnIsWhite ? foundIndex : -1);
    }

    private int locateLegalMove(Square search) {
        if (indexOfChessman != -1) {
            searchableMove.square = search;
            Chessman chessman = chessArmy.elementAt(indexOfChessman);
            Vector<Move> L = chessman.legalMove;
            int index = -1;
            for (int i = 0; i < chessman.numberOfMoves; i++) {
                if (searchableMove.equals(L.get(i))) {
                    index = i;
                    break;
                }
            }
            return index;
        } else {
            return -1;
        }
    }

    private int locateLegalPromotion(Square search, char promotion) {
        if (indexOfChessman == -1) {
            return -1;
        }
        // send (user) input promotion to acceptable 'FORSYTH' notation
        promotion |= 32; //bitwise inclusive OR on 32 -the same as lowercase
        if(turnIsWhite)
            promotion ^= 32; //bitwise exlusive OR on 32 -the same as inverting
        // the case, in this instance, from lower to upper
        int promoteIndex = CHESSMAN_SYMBOL.indexOf(promotion);

        if (promoteIndex != -1) {
            searchablePromotion.set(search, promoteIndex);

            Chessman chessman = chessArmy.elementAt(indexOfChessman);
            Vector<Move> L = chessman.legalMove;
            int indexOfMatch = -1;
            for (int i = 0; i < Pawn.PROMOTE_SEGMENT_LENGTH; i++) {
                if (searchablePromotion.equals((PawnPromote)L.get(i))) {
                    indexOfMatch = i;
                    break;
                }
            }

            return indexOfMatch;
        } else {
            return -1;
        }
    }

    private void resetThreatenedSquare() {
        for (int i = 1; i< Chessman.BOARD_LENGTH + 1; i++)
            for (int j = 1; j< Chessman.BOARD_LENGTH + 1; j++) {
                ThreatenedSquare[i][j][0] = 0; // !Chessman.isWhite -> slice '0
                ThreatenedSquare[i][j][1] = 0; // Chessman.isWhite -> slice '1
            }
    }

    protected void updateThreatenedSquare(Square square, boolean isWhite) {
        ThreatenedSquare[square.rank][square.file][isWhite ? 1 : 0]++;
    }

    public void generateMoves() { getLegalMoves();}

    // invariant: chessman.isInPlay && chessman.isWhite == turnIsWhite implies
    // chessman.legalMove.isLegal is equivalent to the move is legal.
    private void getLegalMoves() {

        resetThreatenedSquare();

        // First reset the restricted moves flags
        for (Chessman chessman : chessArmy)
            if(chessman.isInPlay)
                chessman.resetRestrictedMovesFlags();

        // Then compute Psuedo legal moves. In reason, this gets checks, pins, and attacked squares
        for (Chessman chessman : chessArmy)
            if(chessman.isInPlay)
                chessman.findPsuedoLegalMoves();

        // get the squares that stop check
        King king = ((King)chessArmy.get(turnIsWhite ? 0 : 1));
        isInCheck = king.inCheck;
        checkSquare = king.checkSquare;
        checkSquareSize = king.checkSquareSize;

        if (king.inCheck2x) {
            king.findLegalMoves();

            Chessman chessman;
            for (int i = 2; i < chessArmy.size(); i++) {

                chessman = chessArmy.get(i);
                if (chessman.isInPlay && chessman.isWhite == turnIsWhite)
                    chessman.resetMovesToNotLegal();
            }

            return;
        }

        // Next compute the legal moves by validating the psuedo legal moves
        // Improve the calculation by Filter the moves by:
        // 1. is in play
        // 2. is the color of side that moves this turn
        // 3. is state is not double check
        for (Chessman chessman : chessArmy)
            if(chessman.isInPlay && chessman.isWhite == turnIsWhite)
                chessman.findLegalMoves();
    }

    // modifies: the chessman @ chessArmy.get(indexOfChessman)
    // modifies: ChessBoard
    private void playMove() {

        chessArmy.get(indexOfChessman).playMove(indexOfMove);
        turnIsWhite = !turnIsWhite;
    }

    protected Chessman generateChessman(int chessmanInt, int rank, int file) {

        Chessman chessman;
        switch (chessmanInt) {
            case 0:
            case 1:
                chessman = new Bishop(this, rank, file, (chessmanInt % 2) == 0);
                break;
            case 2:
            case 3:
                chessman = new Knight(this, rank, file, (chessmanInt % 2) == 0);
                break;
            case 4:
            case 5:
                chessman = new Rook(this, rank, file, (chessmanInt % 2) == 0);
                break;
            case 6:
            case 7:
                chessman = new Queen(this, rank, file, (chessmanInt % 2) == 0);
                break;
            default:
                return null;
        }

        chessArmy.add(chessman);
        return chessman;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }
    public String toString() {
        String moves = "";
        String movesPerChessman;

        // concatenate all of the legal moves into String 'moves'
        for (Chessman chessman : chessArmy)
            if (chessman.isInPlay) {

                movesPerChessman = "";
                for (int i = 0; i < chessman.numberOfMoves; i++)
                    if (chessman.legalMove.get(i).isLegal &&
                            chessman.isWhite == turnIsWhite)
                        movesPerChessman += chessman.printMove(i) + " ";

                if (movesPerChessman.length() != 0) {
                    movesPerChessman = movesPerChessman.substring(0 ,
                            movesPerChessman.length() - 1);
                    moves += movesPerChessman + " ";
                }
            }
        return moves.trim();
    }

    // returns 1 for checkmate, 0 for stalemate, -1 for neither
    protected int boardState() {
        return numberOfLegalMoves() == 0 ? (isInCheck ? 1 : 0) : -1;
    }

    // precondition: getLegalMoves()
    private int numberOfLegalMoves() {
        int n = 0;
        for (Chessman chessman : chessArmy)
            if (turnIsWhite == chessman.isWhite && chessman.isInPlay)
                n += chessman.getNumberOfLegalMoves();

        return  n;
    }
}
