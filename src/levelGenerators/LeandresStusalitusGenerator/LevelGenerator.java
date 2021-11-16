package levelGenerators.LeandresStusalitusGenerator;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class LevelGenerator implements MarioLevelGenerator {

    Random rand;
    private String folder = "./././levels/markovChainPieces/";

    public LevelGenerator(){}

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        this.rand = new Random();
        model.clearMap();

        String lastPiece = "initialChunk";
        int currentWidth = 0;

        while(!lastPiece.startsWith("finish")){
            currentWidth = copyChunkToLevel(model, lastPiece, currentWidth);
            double d = rand.nextDouble();
            switch(lastPiece){
                case "initialChunk":
//                    if(d < 0.5){
//                        folder += "regular/";
//                    }
//                    else if(d > 0.75){
//                        folder += "ceiling/";
//                    }
//                    else {
//                        folder += "platform/";
//                    }
                    folder += "ceiling/";
                    lastPiece = "start";
                    break;
                case "start":
                    lastPiece = "finish1";
                    break;
                default:
                    break;
            }
        }

        copyChunkToLevel(model, lastPiece, currentWidth);

        return model.getMap();
    }

    /**
     * Copies string containing a level portion to the current model
     *
     * @param model Target map to copy the string to
     * @param chunkName Name of the string to be copied
     * @param currentWidth Width of map that has already been filled in
     * @return Updated value for currentWidth after copying
     */
    private int copyChunkToLevel(MarioLevelModel model, String chunkName, int currentWidth) {
        String chunk="";
        try {
            chunk = new String(Files.readAllBytes(Paths.get(folder + chunkName+".txt")));
        } catch (IOException e) {
        }
        chunk = chunk.replaceAll("\r\n", "\n");
        int width = chunk.indexOf('\n');
        model.copyFromString(currentWidth, 0, 0, 0, width, 16, chunk);
        return currentWidth+width;
    }

    @Override
    public String getGeneratorName() {
        return "LeandresStusalitusLevelGenerator";
    }
}
