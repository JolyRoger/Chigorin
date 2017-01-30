package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/25/15.
 */
public class Move {
    protected Square square;
    protected boolean isLegal;
    protected double evaluation;

    protected Move() {
        this.isLegal = false;
    }

    protected Move(Square square) {
        this.square = square;
        this.isLegal = false;
        this.evaluation = 0.00;
    }

    protected Move(Square square, boolean isLegal) {
        this.square = square;
        this.isLegal = isLegal;
        this.evaluation = 0.00;
    }


    protected boolean equals(Move m) {
        // need to add a condition for isLegal field
        return this.isLegal && m.isLegal && this.square.equals(m.square);
    }

    protected void set(Square square) {
        this.square = square;
    }

    protected void set(Square square, boolean isLegal) {
        this.square = square;
        this.isLegal = isLegal;
    }

    protected void set(boolean isLegal) {
        this.isLegal = isLegal;
    }
}
