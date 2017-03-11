package engine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineInstance {

    private final static String STOCKFISH_PATH = "public/engines/stockfish_8_x64";
    private final static String STOCKFISH_MODERN_PATH = "public/engines/stockfish_8_x64_modern";
    private final static String MEDIOCRE_PATH = "public/engines/mediocre_v0.5.jar";

    private BufferedReader reader;
    private BufferedWriter writer;
    private int ponderTime = 1000;
    private Process process = null;
    private String currentEngine = "Stockfish";
    private Map<String, String[]> engineMap = new HashMap<>(2);


    public EngineInstance(String engine) {
        engineMap.put("Mediocre", new String[] {"java", "-jar", MEDIOCRE_PATH});
        engineMap.put("Stockfish", new String[] {STOCKFISH_PATH});
        engineMap.put("Stockfish Modern", new String[] {STOCKFISH_MODERN_PATH});
        process(engineMap.get(engine));
    }

    private void initCommand() throws ExecutionException, InterruptedException, IOException {
        write("uci ");
        read("uciok").get();
        write("isready");
        read("readyok").get();
    }

    public void process(String... pathTo) {
        ProcessBuilder builder = new ProcessBuilder(pathTo);

        try {
            process = builder.start();
            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stdout));
            writer = new BufferedWriter(new OutputStreamWriter(stdin));
            initCommand();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void write(String command) throws IOException {
        System.out.println(command);
        writer.write(command.trim());
        writer.newLine();
        writer.flush();
    }

    public Future<String> read(String condition) {
        return Executors.newCachedThreadPool().submit(() -> {
            try {
                String line = null;
                while (((line = reader.readLine()) != null && !line.contains(condition))) {
//                    line = reader.readLine();
                    System.out.println("\t" + line);
                }
                System.out.println("\t" + (line == null ? "" : line));
                return line;
            } catch (IOException e) {
                return null;
            }
        });
    }

    public void close() {
        try {
            write("quit");
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.err.println("Stream is already closed");
        }
    }

    public void changeEngine(String engine) {
        if (currentEngine.equals(engine)) return;
        currentEngine = engine;
        close();
        process(engineMap.get(engine));
    }

    public void setPonderTime(int ponderTime) {
        this.ponderTime = ponderTime;
    }

    public int getPonderTime() {
        return ponderTime;
    }
}