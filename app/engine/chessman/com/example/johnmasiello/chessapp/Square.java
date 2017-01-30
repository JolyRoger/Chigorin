package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/24/15.
 */
public class Square {
    protected final int rank;
    protected final int file;


    protected Square() {
            rank = Chessman.BOARD_LENGTH;
            file = Chessman.BOARD_LENGTH;
    }

    protected Square(int x, int y) throws InvalidSquareException{
        if (y < 1 || y > Chessman.BOARD_LENGTH ||
                x < 1 || x > Chessman.BOARD_LENGTH) {
                throw new InvalidSquareException();
        }
        rank = y;
        file = x;
    }

    protected Square(String alphaNumericSquare) {
        rank = Square.getRankFromANS(alphaNumericSquare);
        file = Square.getFileFromANS(alphaNumericSquare);
    }

    //uses: outside of this class, ChessBoard.setUpBoard -setting ep flag
    protected static int getRankFromANS(String alphaNumericSquare) {
        // returns the lowest 4 digits binary to signify rank
        return alphaNumericSquare.charAt(1) & 15;
    }

    //uses: outside of this class, ChessBoard.setUpBoard -setting ep flag
    protected static int getFileFromANS(String alphaNumericSquare) {
        // returns the lowest 4 digits binary to signify file
        return alphaNumericSquare.charAt(0) & 15;
    }

    protected String getAlphaNumeral() {
        return Character.toString((char)((int)'a'+this.file-1))+
                this.rank;
    }

//    protected Square clone() {
//        try {
//            return new Square(this.file, this.rank);
//        } catch (InvalidSquareException e) {
//        //will not throw InvalidSquareException, by the invariant, this square is valid
//            return null;
//        }
//    }

    protected boolean equals(Square s2) {
        return this.rank == s2.rank && this.file == s2.file;
    }

    protected class InvalidSquareException extends Exception{
        protected InvalidSquareException() {
            super();
        }
    }
}
