package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineInstance {

    private final static String STOCKFISH_PATH = "public/engines/stockfish_8_x64";
    private final static String RYBKA_PATH = "public/engines/Rybka 4 x64.exe";
    private final static String STOCKFISH_MODERN_PATH = "public/engines/stockfish_8_x64_modern";
    private final static String MEDIOCRE_PATH = "public/engines/mediocre_v0.5.jar";
    private final static String GO_INFINITE = "go infinite";
    private final static String GO_MOVETIME = "go movetime ";

    private int ponderTime = 1000;
    private boolean analysisMode;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Process process = null;
    private String currentEngine = "Stockfish";
    private Map<String, String[]> engineMap = new HashMap<>(2);
    private InfoProcessor processor;

    public EngineInstance(String engine) {
        engineMap.put("Mediocre", new String[] {"java", "-jar", MEDIOCRE_PATH});
        engineMap.put("Stockfish", new String[] {STOCKFISH_PATH});
        engineMap.put("Rybka", System.getProperty("os.name").contains("Linux") ? new String[] {"wine", RYBKA_PATH} :
                new String[] {RYBKA_PATH});
        engineMap.put("Stockfish Modern", new String[] {STOCKFISH_MODERN_PATH});
        processor = new InfoProcessor();
        process(engineMap.get(engine));
    }

    private void initCommand() throws IOException, ExecutionException, InterruptedException {
        write("uci ");
        read("uciok").get();
        write("isready");
        read("readyok").get();
    }

    @SneakyThrows
    public void process(String... pathTo) {
        ProcessBuilder builder = new ProcessBuilder(pathTo);
        process = builder.start();
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
        initCommand();
    }

    public void write(String command) throws IOException {
        System.out.println(command);
        writer.write(command.trim());
        writer.newLine();
        writer.flush();
    }

    public Future<String> read(String condition) {
        return Executors.newCachedThreadPool().submit(() -> {
                String line = null;
                while (((line = reader.readLine()) != null && !line.contains(condition))) {
                    if (analysisMode) processLine(line);
//                    line = reader.readLine();
                    System.out.println("\t" + line);
                }
                System.out.println("\t" + (line == null ? "" : line));
                return line;
        });
    }

    private void processLine(String line) {
        processor.process(line);
    }

    public void ponderTime(int time) throws IOException {
        analysisMode = time <= 0;
        ponderTime = time * 1000;
        int mpvVal = analysisMode ? 3 : 1;
        setOption("MultiPV", mpvVal + "");
    }

    public void close() throws IOException {
        write("quit");
        reader.close();
        writer.close();
    }

    public void changeEngine(String engine) throws IOException {
        if (currentEngine.equals(engine)) return;
        currentEngine = engine;
        close();
        process(engineMap.get(engine));
    }

    public String go(String conditionToAnswer) throws IOException, ExecutionException, InterruptedException {
        if (analysisMode) write(GO_INFINITE);
        else write(GO_MOVETIME + ponderTime);
        return read(conditionToAnswer).get();
    }

    public void startAnalysis() throws IOException {
        analysisMode = true;
        setOption("MultiPV", 3 + "");
        write(GO_INFINITE);
        read("bestmove");
    }

    public void setOption(String name, String value) throws IOException {
        write("setoption name " + name + " value " + value);
    }

//    public Map<Integer, InfoStructure> getAnalysis() throws JsonProcessingException {
    public String getAnalysis() throws JsonProcessingException {
        Map<Integer, InfoStructure> structureMap = processor.getStructureMap();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(structureMap);
//        return structureMap;
    }
}
