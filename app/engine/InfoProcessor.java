package engine;

import lombok.Getter;
import java.util.*;

public class InfoProcessor {

    private Map<Integer, InfoStructure> structureMap;

    public InfoProcessor() {
        structureMap = new HashMap<>();
    }

    public void process(String line) {
        int multipv = 0;
        Score score = Score.cp;
        String[] quants = line.split(" ");
        for (int i = 0; i < quants.length; i++) {
            InfoStructure info  = null;
            String quant = quants[i];

            switch(quant) {
                case "multipv" :
                    multipv = Integer.parseInt(quants[i+1]);
                    break;
                case "score" :
                    score = getScore(quants[i+1], quants[i+2]);
                    break;
                case "pv" :
                    String[] best = Arrays.copyOfRange(quants, i+1, quants.length);
                    info = structureMap.getOrDefault(multipv, new InfoStructure());
                    info.pv = Arrays.toString(best);
                    info.scoreType = score;
                    info.score = score.val;
                    info.multipv = multipv;
                    structureMap.put(multipv, info);
                    break;
            }
        }
    }

    private Score getScore(String type, String value) {
        Score out = null;
        switch(type) {
            case "mate":
                out = Score.mate;
                break;
            case "cp":
                out = Score.cp;
                break;
        }
        out.val = Integer.parseInt(value);
        return out;
    }

    public Set<InfoStructure> getStructure() {
        Set<InfoStructure> info = new TreeSet<>((v1, v2) -> {
            int res = v1.score - v2.score;
            return res == 0 ? -1 : res;
        });
        info.addAll(structureMap.values());
        return info;
    }
}
