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
    // TODO: All new code must be in our generator package, do we need to move the chunks there as well?
    private String folder = "./././levels/markovChainPieces/";

    public LevelGenerator(LevelType type){
        this.type = type;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        Random rand = new Random();
        model.clearMap();

        double d = rand.nextDouble();

        if(type != LevelType.RANDOM){
            switch (type) {
                case REGULAR -> folder += "regular/";
                case CEILING -> folder += "ceiling/";
                case PLATFORM -> folder += "platform/";
            }
        }
        else if(d < 0.5){
            folder += "regular/";
            type = LevelType.REGULAR;
        }
        else if(d > 0.75){
            folder += "ceiling/";
            type = LevelType.CEILING;
        }
        else {
            folder += "platform/";
            type = LevelType.PLATFORM;
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
            int level;
            switch(type){
                case REGULAR:
                    level = (int)(d*41+1);
                    if(level < 12){
                        currentPiece = "lvl1-" + level;
                    }
                    else if(level < 19){
                        currentPiece = "lvl4-" + (level-11);
                    }
                    else if(level < 24){
                        currentPiece = "lvl5-" + (level-18);
                    }
                    else if(level < 28){
                        currentPiece = "lvl7-" + (level - 23);
                    }
                    else if(level < 32){
                        currentPiece = "lvl9-" + (level - 27);
                    }
                    else if(level == 32){
                        currentPiece = "lvl11-1";
                    }
                    else if(level < 37){
                        currentPiece = "lvl12-" + (level-32);
                    }
                    else if(level < 40){
                        currentPiece = "lvl14-" + (level-36);
                    }
                    else if(level == 40){
                        currentPiece = "lvl15-1";
                    }
                    else {
                        currentPiece = "finish";
                    }
                    break;
                case CEILING:
                    level = (int)(d*33+1);
                    if(level < 15){
                        currentPiece = "lvl2-" + level;
                    }
                    else if(level < 32){
                        currentPiece = "lvl8-" + (level-14);
                    }
                    else {
                        currentPiece = "finish" + (level-31);
                    }
                    break;
                case PLATFORM:
                    level = (int)(d*26+1);
                    if(level < 10){
                        currentPiece = "lvl3-" + level;
                    }
                    else if(level < 17){
                        currentPiece = "lvl6-" + (level-9);
                    }
                    else if(level < 20){
                        currentPiece = "lvl10-" + (level-16);
                    }
                    else if(level < 26){
                        currentPiece = "lvl13-" + (level-19);
                    }
                    else {
                        currentPiece = "finish";
                    }
                    break;
                default:
                    System.out.println("Something went wrong and we hit a default case");
                    currentPiece = "finish";
                    break;
            }
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
