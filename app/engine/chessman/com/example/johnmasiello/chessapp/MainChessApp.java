package engine.chessman.com.example.johnmasiello.chessapp;


/**
 * Created by john on 5/24/15.
 */
public class MainChessApp {

    public static String getString() {
        /*
        //String move = "1.e4";
        //String move = Integer.toString('H' & 15);
        String move = Character.toString((char)('A' | 32));
        //int i = move.length();
        Square s;
        try {
            s = new Square(18, 1);
        }
        catch (Square.InvalidSquareException E) {
            s = new Square("c3");
        }
        String move = s.getAlphaNumeral();

        ChessBoard b1 = new ChessBoard();
        return b1.makeAMove(move) ? move : null;*/
        //return Boolean.toString(new Object() instanceof Object);
        /*Object m;String m2 = "";
        try {
            m = (Object)(new Square());
        } catch (Exception e) {
            return "cannot cast";
        } try {

            m2 = "instance of check is valid: " + Boolean.toString(m instanceof Square);
            return m2;
        } catch (Exception e) {
            return "instance of test failed";
        } finally {
            return m2;//"everything is ok";
        }*/


        //char a = 'a';


        //a |= 32;


        //return Character.toString(a);
        //return "" + ('3'-'2');


        //boolean ans = true || false ? false : true; //expression left of '?' evaluated first -> ans=false


        // the other way -> ans=true
        //return "" + ans; // compiles to false. Thus the operator to the left of '?' has higher precedence


        /*Vector<Vector<String>> i = new Vector();
        i.add(new Vector());
        //String o = "vector size " + i.get(0).size();
        i.get(0).add(0,"hello");
        i.get(0).set(0,"bye");*/

        //o = o+" vector size " + i.get(0).size();
        //return "outer vector size " + i.size() + " inner vector size " + i.get(0).size();
        //return ""+i.get(0).get(0).length();


        //Chessman c = new Chessman();


        //return "" + c.isCapture(null);
        //return "" + (null instanceof Object);
        /*int[] b = new int[3];
        int j = -1;
        try {
            int d = b[j++];
        } catch (Exception e) {
            return "" + j;
        }
        return "";*/

        //testing polymorphism
        /*Chessman k = new Knight();
        String o = ((Knight)k).printdirection();
        k.updateRestrictedMoveDirection(3);
        o = o+((Knight)k).printdirection();
        ((Knight)k).updateRestrictedMoveDirection(2);
        o = o+((Knight)k).printdirection();
        return o;*/

        ChessBoard b1 = new ChessBoard();
        if (!b1.setTheBoardUp())
            return "error setting up the board";

        b1.generateMoves();
        if (!b1.makeAMove("g1f3")) return "1";
        b1.generateMoves();
        return " That was the end of the debug position";

//        b1.generateMoves();
//        if (!b1.makeAMove("oo")) return "1";
//
//        b1.generateMoves();
//        if (b1.makeAMove("oo")) return "2"; // should not make the illegal move
//
//        if (!b1.makeAMove("ooo")) return "3";
//
//        b1.generateMoves();

//        b1.generateMoves();
//    switch (b1.boardState()) {
//        case 1:
//            return "checkmate";
//        case 0:
//            return "stalemate";
//        default:
//            //return "keep playing";
//    }
//        if (!b1.makeAMove("c7d8n")) return "1";
//
//        b1.generateMoves();
//        if (!b1.makeAMove("c8d8")) return "2";
//
//        // get the result, or keep playing
//        b1.generateMoves();
//        switch (b1.boardState()) {
//            case 1:
//                return "Checkmate, better luck next time";
//            case 0:
//                return "Stalemate, count your blessings";
//            default:
//                //return "What have you done to defeat my app?";
//        }
//
//        if (!b1.makeAMove("d7e8q")) return "3";
//
//        b1.generateMoves();
////        if (!b1.makeAMove("ooo")) return "4";
////
////        b1.generateMoves();
////        if (!b1.makeAMove("g6h6")) return "4";
////
////        b1.generateMoves();
//        return " That was the end of the debug position";
}
//        b1.generateMoves();
//        if (!b1.makeAMove("e4e5"))
//            return "correctly did not make an illegal move";
//        else
//            return "check with debugger on the correctness of last move";
}
