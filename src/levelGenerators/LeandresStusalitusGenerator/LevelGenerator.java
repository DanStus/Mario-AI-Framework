package levelGenerators.LeandresStusalitusGenerator;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class LevelGenerator implements MarioLevelGenerator {

    public enum LevelType {
        RANDOM,
        REGULAR,
        CEILING,
        PLATFORM
    }

    private LevelType type;
    private double difficulty;
    // TODO: All new code must be in our generator package, do we need to move the chunks there as well?
    private String folder = "./././levels/markovChainPieces/";

    public LevelGenerator(LevelType type, double difficulty){
        this.type = type;
        if(difficulty >= 0 && difficulty <= 2)
            this.difficulty = difficulty;
        else
            this.difficulty = 1;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        Random rand = new Random();
        model.clearMap();

        double d;

        // This is the number of levels we have to choose from for a given type
        int max = 1;
        int numFin = 1;

        if(type == LevelType.RANDOM){
            d = rand.nextDouble();
            if(d < 0.5){
                type = LevelType.REGULAR;
            }
            else if(d > 0.75){
                type = LevelType.CEILING;
            }
            else {
                type = LevelType.PLATFORM;
            }
        }

        switch (type) {
            case REGULAR -> {
                folder += "regular/";
                max = 41;
            }
            case CEILING -> {
                folder += "ceiling/";
                max = 33;
                numFin = 2;
            }
            case PLATFORM -> {
                folder += "platform/";
                max = 26;
            }
        }

        // This is the next item we are going to add to the Markov Chain
        // Or the item we just added, depending on where we are in the code
        String currentPiece = "start";

        // This is the width of the level already filled in by chunks
        // AKA the offset we place new chunks at
        int currentWidth = 0;

        // Add chunks to the level until we add a terminal chunk to end the level
        while(!currentPiece.startsWith("finish")){
            currentWidth = copyChunkToLevel(model, currentPiece, currentWidth);

            // Here we choose our next chunk to be added to the level
            // The chunk chosen depends on the level type we picked earlier
            // And on the random value d
            d = rand.nextDouble();
            int level = (int)(d * max + 1);
            // If we have a chunk that does not contain a chunk the name is just a number
            // Otherwise the name is finish + a number
            if(level < max-numFin) {
                currentPiece = String.valueOf(level);
                if(difficulty > 1){
                    d = rand.nextDouble() + 1;
                    if(d > difficulty)
                        currentPiece = currentPiece + "-h";
                }
                else if(difficulty < 1){
                    d = rand.nextDouble();
                    if(d > difficulty)
                        currentPiece = currentPiece + "-e";
                }
            }
            else
                currentPiece = "finish" + (level+numFin-max);
        }

        copyChunkToLevel(model, currentPiece, currentWidth);

        return model.getMap();
    }

    /**
     * Copies string containing a level chunk to the current model
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
