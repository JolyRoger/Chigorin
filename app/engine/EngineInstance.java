package engine;

import engine.chessman.com.example.johnmasiello.chessapp.ChessBoard;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineInstance {

    private BufferedReader reader;
    private BufferedWriter writer;
    private int ponderTime = 0;
    private String fen;
    private ChessBoard board;

    public void process(String pathTo, boolean withWine) {
        ProcessBuilder builder = new ProcessBuilder(withWine ? "wine" : "", pathTo);
        Process process = null;
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
//                    System.out.println("\tStdout: " + line);
                }
                return line;
            } catch (IOException e) {
                return null;
            }
        });
    }

    public void close() {
        System.out.println("close");
        write("quit");
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLegalMoves(String fen) {
        return "e2e3 e2e4";
    }


    public void setPonderTime(int ponderTime) {
        this.ponderTime = ponderTime;
    }

    public int getPonderTime() {
        return ponderTime;
    }
}