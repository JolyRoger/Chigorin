package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/29/15.
 */
public class LineMove extends Move {
    protected int direction;

    protected LineMove() {
        super();
    }

    protected LineMove(Square square, int direction) {
        super(square);
        this.direction = direction;
    }

    protected void set(Square square, int direction) {
        super.set(square);
        this.direction = direction;
    }

    protected void set(Square square, boolean isLegal, int direction) {
        super.set(square, isLegal);
        this.direction = direction;
    }
}
