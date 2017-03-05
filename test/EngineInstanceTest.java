import controllers.Settings;
import engine.EngineInstance;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class EngineInstanceTest {

    @Test
    public void process() {
        EngineInstance engineInstance = new EngineInstance("Stockfish");
        try {
            engineInstance.write("quit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void legalMoves() {
    }
}
