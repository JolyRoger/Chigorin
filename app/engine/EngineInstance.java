package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineInstance {

    private static final String STOCKFISH_PATH = "public/engines/stockfish_8_x64";
    private static final String STOCKFISH_WINDOWS_PATH = "public/engines/stockfish_8_x32.exe";
    private static final String KOMODO_PATH = "public/engines/komodo-14.1-linux";
    private static final String KOMODO_WINDOWS_PATH = "public/engines/komodo-14.1-64bit.exe";
    private static final String STOCKFISH_MODERN_PATH = "public/engines/stockfish_8_x64_modern";
    private static final String MEDIOCRE_PATH = "public/engines/mediocre_v0.5.jar";
    private static final String GO_INFINITE = "go infinite";
    private static final String GO_MOVETIME = "go movetime ";
    private static final boolean IS_LINUX = System.getProperty("os.name").contains("Linux");
    private int ponderTime = 1000;
    private int analysisLines = 1;
    public boolean analysisMode;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String currentEngine = "Stockfish";
    private String fen;
    private String[] pathTo;
    private final Map<String, String[]> engineMap = new HashMap<>(2);
    private final InfoProcessor processor;
    private Future<String> bestmove;

    public EngineInstance(String engine) {
        engineMap.put("Mediocre", new String[] {"java", "-jar", MEDIOCRE_PATH});
        engineMap.put("Stockfish", new String[] {IS_LINUX ? STOCKFISH_PATH : STOCKFISH_WINDOWS_PATH});
        engineMap.put("Komodo", new String[] {IS_LINUX ? KOMODO_PATH : KOMODO_WINDOWS_PATH});
        engineMap.put("Stockfish Modern", new String[] {STOCKFISH_MODERN_PATH});
        processor = new InfoProcessor();
        process(engineMap.get(engine));
    }

    private void initCommand() throws ExecutionException, InterruptedException {
        write("uci ");
        read("uciok").get();
        write("isready");
        read("readyok").get();
    }

    @SneakyThrows
    public void process(String... pathTo) {
        this.pathTo = pathTo;
        ProcessBuilder builder = new ProcessBuilder(pathTo);
        Process process = builder.start();
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
        initCommand();
    }

    public void write(String command) {
        System.out.println(command);
        try {
            writer.write(command.trim());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            process(pathTo);
            write(command);
        }
    }

    public Future<String> read(String condition) {
        return Executors.newCachedThreadPool().submit(() -> {
            String line = null;
            while (((line = reader.readLine()) != null && !line.contains(condition))) {
                if (analysisMode) processLine(line);
//                    line = reader.readLine();
//                    System.out.println("\t" + line);
            }
            System.out.println("\t" + (line == null ? "" : line));
            return line;
        });
    }

    private void processLine(String line) {
        processor.process(line);
    }

    public void analysisLines(int lines) {
        if (analysisMode) setOption("MultiPV", lines + "");
        analysisLines = lines;
    }

    public void ponderTime(int time) {
        ponderTime = time * 1000;
    }

    public void close() throws IOException {
        write("quit");
        reader.close();
        writer.close();
    }

    public void changeEngine(String engine) throws IOException {
        if (currentEngine.equals(engine)) return;
        currentEngine = engine;
        boolean continueAnal = false;
        if (analysisMode) {
            stopAnalysis();
            continueAnal = true;
        }
        close();
        process(engineMap.get(engine));
        if (continueAnal) {
            startAnalysis(fen);
        }
    }

    public String go(String conditionToAnswer) throws ExecutionException, InterruptedException {

        write(ponderTime < 0 || analysisMode ? GO_INFINITE : GO_MOVETIME + ponderTime);
        return read(conditionToAnswer).get();
    }

    public void startAnalysis(String fen) {
        write("position fen " + fen);
        this.fen = fen;
        analysisMode = true;
        setOption("MultiPV", analysisLines + "");
        write(GO_INFINITE);
        bestmove = read("bestmove");
    }

    public void stopAnalysis() {
        write("stop");
        analysisMode = false;
        setOption("MultiPV", 1 + "");
        processor.clearMap();
    }

    public String getBestMove() throws ExecutionException, InterruptedException {
        write("stop");
        return bestmove.get();
    }

    private void setOption(String name, String value) {
        write("setoption name " + name + " value " + value);
        processor.clearMap();
    }

    public String getAnalysis() throws JsonProcessingException {
        var structureMap = processor.getStructure();
        var mapper = new ObjectMapper();
        return mapper.writeValueAsString(structureMap);
    }
}
