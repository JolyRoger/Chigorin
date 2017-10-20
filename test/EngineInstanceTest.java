import engine.EngineInstance;
import engine.InfoProcessor;
import engine.InfoStructure;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class EngineInstanceTest {

    @Test
    public void process() {
        EngineInstance engineInstance = new EngineInstance("Stockfish");
        engineInstance.write("quit");
    }

    @Test
    public void infoProcessorTest() {
        String[] strarr = {
                "info depth 19 seldepth 29 multipv 1 score cp -20 nodes 5884629 nps 1171536 hashfull 995 tbhits 0 time 5023 pv d7d5 d2d4 c8f5 c2c4 e7e6 b1c3 b8c6 c1g5 f8e7 c4d5 e6d5 g5e7 g8e7 e2e3 e8g8 f1e2 c6a5 e1g1 c7c6 f3e5 f7f6 e5f3",
                "info depth 19 seldepth 29 multipv 2 score cp -33 nodes 5884629 nps 1171536 hashfull 995 tbhits 0 time 5023 pv g8f6 d2d4 e7e6 c2c4 d7d5 b1c3 f8b4 e2e3 e8g8 f1d3 c7c5 a2a3 c5d4 e3d4 d5c4 d3c4 b4d6 e1g1 b8c6",
                "info depth 18 seldepth 29 multipv 3 score cp -37 nodes 5884629 nps 1171536 hashfull 995 tbhits 0 time 5023 pv e7e6 d2d4 g8f6 c2c4 d7d5 b1c3 f8b4 e2e3 c7c5 a2a3 b4c3 b2c3 b8d7 f1d3 e8g8 e1g1 d5c4 d3c4 d7b6 f3e5 b6c4 e5c4",
                "info depth 19 currmove e7e6 currmovenumber 3",
                "info depth 19 seldepth 29 multipv 1 score cp -20 nodes 6116057 nps 1171882 hashfull 995 tbhits 0 time 5219 pv d7d5 d2d4 c8f5 c2c4 e7e6 b1c3 b8c6 c1g5 f8e7 c4d5 e6d5 g5e7 g8e7 e2e3 e8g8 f1e2 c6a5 e1g1 c7c6 f3e5 f7f6 e5f3",
                "info depth 19 seldepth 29 multipv 2 score cp -33 nodes 6116057 nps 1171882 hashfull 995 tbhits 0 time 5219 pv g8f6 d2d4 e7e6 c2c4 d7d5 b1c3 f8b4 e2e3 e8g8 f1d3 c7c5 a2a3 c5d4 e3d4 d5c4 d3c4 b4d6 e1g1 b8c6",
                "info depth 19 seldepth 29 multipv 3 score mate 0 upperbound nodes 6116057 nps 1171882 hashfull 995 tbhits 0 time 5219 pv e7e6 d2d4 g8f6 c2c4 d7d5 b1c3 f8b4 e2e3 c7c5 a2a3 b4c3 b2c3 b8d7 f1d3 e8g8 e1g1 d5c4 d3c4 d7b6 f3e5 b6c4 e5c4"
        };

        InfoProcessor proc = new InfoProcessor();
        for (String line : strarr) {
            proc.process(line);
        }
        Set<InfoStructure> info = proc.getStructure();
        for (InfoStructure infoStructure : info) {
            System.out.print(infoStructure.multipv + " ");
            System.out.print(infoStructure.scoreType + " ");
            System.out.println(infoStructure.score + " ");
            System.out.print(infoStructure.pv);
            System.out.println();
        }
        assertTrue(info.size() == 3);
    }
}
