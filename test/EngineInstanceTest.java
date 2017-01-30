import engine.EngineInstance;
import org.junit.Test;
import static org.junit.Assert.*;

public class EngineInstanceTest {

    @Test
    public void process() {
        String ENGINES = "/home/torquemada/Softdev/workspace/Chess/public/engines/";
        EngineInstance engineInstance = new EngineInstance();
        engineInstance.process(ENGINES + "stockfish_8_x32.exe", true);
        engineInstance.write("quit\n");
    }
}
