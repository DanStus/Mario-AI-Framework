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
    private final int levelType;
    private String folder = "./././levels/markovChainPieces/";

    public LevelGenerator(int levelType){
        this.levelType = levelType;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        this.rand = new Random();
        model.clearMap();

        String lastPiece = "start";
        int currentWidth = 0;
        double d = rand.nextDouble();

        if(levelType != 0){
            switch (levelType) {
                case 1 -> folder += "regular/";
                case 2 -> folder += "ceiling/";
                case 3 -> folder += "platform/";
            }
        }
        else if(d < 0.5){
            folder += "regular/";
        }
        else if(d > 0.75){
            folder += "ceiling/";
        }
        else {
            folder += "platform/";
        }

        while(!lastPiece.startsWith("finish")){
            currentWidth = copyChunkToLevel(model, lastPiece, currentWidth);
            d = rand.nextDouble();
            int level;
            switch(folder.substring(folder.length()-9)){
                case "/regular/":
                    level = (int)(d*41+1);
                    if(level < 12){
                        lastPiece = "lvl1-" + level;
                    }
                    else if(level < 19){
                        lastPiece = "lvl4-" + (level-11);
                    }
                    else if(level < 24){
                        lastPiece = "lvl5-" + (level-18);
                    }
                    else if(level < 28){
                        lastPiece = "lvl7-" + (level - 23);
                    }
                    else if(level < 32){
                        lastPiece = "lvl9-" + (level - 27);
                    }
                    else if(level == 32){
                        lastPiece = "lvl11-1";
                    }
                    else if(level < 37){
                        lastPiece = "lvl12-" + (level-32);
                    }
                    else if(level < 40){
                        lastPiece = "lvl14-" + (level-36);
                    }
                    else if(level == 40){
                        lastPiece = "lvl15-1";
                    }
                    else {
                        lastPiece = "finish";
                    }
                    break;
                case "/ceiling/":
                    level = (int)(d*33+1);
                    if(level < 15){
                        lastPiece = "lvl2-" + level;
                    }
                    else if(level < 32){
                        lastPiece = "lvl8-" + (level-14);
                    }
                    else {
                        lastPiece = "finish" + (level-31);
                    }
                    break;
                case "platform/":
                    level = (int)(d*26+1);
                    if(level < 10){
                        lastPiece = "lvl3-" + level;
                    }
                    else if(level < 17){
                        lastPiece = "lvl6-" + (level-9);
                    }
                    else if(level < 20){
                        lastPiece = "lvl10-" + (level-16);
                    }
                    else if(level < 26){
                        lastPiece = "lvl13-" + (level-19);
                    }
                    else {
                        lastPiece = "finish";
                    }
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
