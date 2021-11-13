package levelGenerators.LeandresStusalitusGenerator;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

import java.util.Random;

public class LevelGenerator implements MarioLevelGenerator {

    Random rand;

    public LevelGenerator(){}

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        this.rand = new Random();
        model.clearMap();

        char lastPiece = 'a';

        while(lastPiece != 'd'){
            System.out.print(lastPiece);
            double d = rand.nextDouble();
            switch(lastPiece){
                case 'a':
                    lastPiece = 'b';
                    break;
                case 'b':
                    if(d >0.5)
                        lastPiece = 'c';
                    else
                        lastPiece = 'b';
                    break;
            }
        }

        return null;
    }

    @Override
    public String getGeneratorName() {
        return "LeandresStusalitusLevelGenerator";
    }
}
