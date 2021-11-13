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

    public LevelGenerator(){}

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        this.rand = new Random();
        model.clearMap();
        // Optional variable to store list of chunks used to generate the level
        String stringRep = "";

        char lastPiece = 'a';
        int currentWidth = 0;

        while(lastPiece != 'd'){
            stringRep += lastPiece;
            currentWidth = copyChunkToLevel(model, String.valueOf(lastPiece), currentWidth);
            double d = rand.nextDouble();
            switch(lastPiece){
                case 'a':
                    lastPiece = 'b';
                    break;
                case 'b':
                    if(d >0.8)
                        lastPiece = 'd';
                    else if(d >0.6)
                        lastPiece = 'c';
                    else
                        lastPiece = 'b';
                    break;
                case 'c':
                    if(d >0.8)
                        lastPiece = 'd';
                    else if(d >0.3)
                        lastPiece = 'c';
                    else
                        lastPiece = 'b';
                    break;
                default:
                    break;
            }
        }
        stringRep += lastPiece;
        System.out.println(stringRep);

        copyChunkToLevel(model, String.valueOf(lastPiece), currentWidth);

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
            chunk = new String(Files.readAllBytes(Paths.get("./././levels/markovChainPieces/" + chunkName+".txt")));
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
