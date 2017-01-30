package engine.chessman.com.example.johnmasiello.chessapp;

/**
 * Created by john on 5/25/15.
 */
public class PawnPromote extends LineMove {
    protected int promote;

    protected PawnPromote() {
        super();
    }

    protected PawnPromote(int promote) { this.promote = promote;}

    protected boolean equals(PawnPromote pawnPromote) {
        return promote==pawnPromote.promote && super.equals(pawnPromote);
    }

    protected void set(Square square, int direction, int promote) {
        super.set(square);
        this.direction = direction;
        this.promote = promote;
    }

    protected void set(Square square, int promote) {
        super.set(square);
        this.promote = promote;
    }

    protected void set(boolean isLegal, int promote) {
        super.set(isLegal);
        this.promote = promote;
    }
}
