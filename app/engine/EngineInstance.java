package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineInstance {

    private final static String STOCKFISH_PATH = "public/engines/stockfish_8_x64";
    private final static String STOCKFISH_WINDOWS_PATH = "public/engines/stockfish_8_x32.exe";
    private final static String RYBKA_PATH = "public/engines/Rybka 4 x64.exe";
    private final static String STOCKFISH_MODERN_PATH = "public/engines/stockfish_8_x64_modern";
    private final static String MEDIOCRE_PATH = "public/engines/mediocre_v0.5.jar";
    private final static String GO_INFINITE = "go infinite";
    private final static String GO_MOVETIME = "go movetime ";
    private final static boolean IS_LINUX = System.getProperty("os.name").contains("Linux");
    private int ponderTime = 1000;
    public boolean analysisMode;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Process process = null;
    private String currentEngine = "Stockfish";
    private Map<String, String[]> engineMap = new HashMap<>(2);
    private InfoProcessor processor;
    private Future<String> bestmove;

    public EngineInstance(String engine) {
        engineMap.put("Mediocre", new String[] {"java", "-jar", MEDIOCRE_PATH});
        engineMap.put("Stockfish", new String[] {IS_LINUX ? STOCKFISH_PATH : STOCKFISH_WINDOWS_PATH});
        engineMap.put("Rybka", IS_LINUX ? new String[] {"wine", RYBKA_PATH} : new String[]{RYBKA_PATH});
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
        close();
        process(engineMap.get(engine));
    }

    public String go(String conditionToAnswer) throws IOException, ExecutionException, InterruptedException {
        write(ponderTime < 0 || analysisMode ? GO_INFINITE : GO_MOVETIME + ponderTime);
        return read(conditionToAnswer).get();
    }

    public void startAnalysis() throws IOException, ExecutionException, InterruptedException {
        analysisMode = true;
        setOption("MultiPV", 3 + "");
        write(GO_INFINITE);
        bestmove = read("bestmove");
    }

    public String stopAnalysis() throws IOException, ExecutionException, InterruptedException {
        analysisMode = false;
        setOption("MultiPV", 1 + "");
        return bestmove.get();
    }

    public void setOption(String name, String value) throws IOException {
        write("setoption name " + name + " value " + value);
    }

    public String getAnalysis() throws JsonProcessingException {
        Set<InfoStructure> structureMap = processor.getStructure();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(structureMap);
    }
}
