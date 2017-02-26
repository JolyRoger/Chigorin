package engine;

import engine.chessman.com.example.johnmasiello.chessapp.ChessBoard;
import play.Play;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineInstance {

//    private final static String STOCKFISH_PATH = "public/engines/stockfish_8_x32.exe"

    private BufferedReader reader;
    private BufferedWriter writer;
    private int ponderTime = 0;
    private String fen;
    private ChessBoard board;
    private Process process = null;
    private String currentEngine = "Stockfish";

//    public void stockfishProcess() {
//        process(Play.application().getFile(STOCKFISH_PATH).getAbsolutePath(), System.getProperty("os.name").contains("Linux"));
//    }

    public void process(String pathTo, boolean withWine) {
        ProcessBuilder builder = null;

        if (withWine)  builder = new ProcessBuilder("wine", pathTo);
        else builder = new ProcessBuilder(pathTo);

        if (process != null) close();

        try {
            process = builder.start();
            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stdout));
            writer = new BufferedWriter(new OutputStreamWriter(stdin));
            board = new ChessBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String command) {
        try {
            writer.write(command.trim());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Future<String> read(String condition) {
        return Executors.newCachedThreadPool().submit(() -> {
            try {
                String line = null;
                while (((line = reader.readLine()) != null && !line.contains(condition))) {
//                    line = reader.readLine();
//                    System.out.println("\t" + line);
                }
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
            e.printStackTrace();
        }
    }

    public void changeEngine(String engine) {
        System.out.println("current: " + currentEngine);
        if (currentEngine.equals(engine)) return;
        currentEngine = engine;
        close();
        System.out.println("EngineInstance::engine: " + engine);
    }

    public void setPonderTime(int ponderTime) {
        this.ponderTime = ponderTime;
    }

    public int getPonderTime() {
        return ponderTime;
    }
}