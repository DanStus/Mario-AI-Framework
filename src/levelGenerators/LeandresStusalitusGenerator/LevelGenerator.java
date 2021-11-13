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

        char lastPiece = 'a';
        int currentWidth = 0;

        while(lastPiece != 'd'){
            System.out.print(lastPiece);
            currentWidth = copyChunkToLevel(model, lastPiece, currentWidth);
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
        copyChunkToLevel(model, lastPiece, currentWidth);
        System.out.println(lastPiece);

        return model.getMap();
    }

    private int copyChunkToLevel(MarioLevelModel model, char lastPiece, int currentWidth) {
        String chunk="";
        try {
            chunk = new String(Files.readAllBytes(Paths.get("./././levels/markovChainPieces/" + lastPiece+".txt")));
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
