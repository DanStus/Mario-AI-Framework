package agents.LeandresStusalitus;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

import java.util.Random;

public class Agent implements MarioAgent {

    private boolean[] action;

    private boolean jumpGap(int[] marioPos, int[][] level){
        // Check for gaps, starting at Mario's x position and looking ahead 2
        // Check from Mario's y pos down to the bottom of the level
        // If no blocks are found there must be a gap
        for (int x = marioPos[0]+1; x <= marioPos[0] + 2; x++){
            for(int y = marioPos[1]; y <= 15; y++){
                // If we find a block, then there is no gap
                // Skip to the next X coord
                if(level[x][y] != 0){
                    break;
                }
                else if (y == 15){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jumpEnemy(int[] marioPos, int[][] level){

        for(int x = marioPos[0]; x <= marioPos[0]+3; x++){
            for(int y = marioPos[1]-(x-marioPos[0]); y <= marioPos[1]+(x-marioPos[0]); y++){
                if(y >= 0 && y <= 15 && level[x][y] == 2){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        this.action = new boolean[MarioActions.numberOfActions()];
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {

        /*int[][] screen = model.getScreenCompleteObservation(0,0);
        int[] pos = model.getMarioScreenTilePos();
        int[][] level = model.getScreenSceneObservation(2);

        boolean obstacle = (level[pos[0]+1][pos[1]] != 0 || level[pos[0]+2][pos[1]] != 0 || level[pos[0]+3][pos[1]] != 0);
        boolean willJumpingDoAnything = (model.getMarioCanJumpHigher() || model.mayMarioJump());
        boolean isMarioFalling = (model.getMarioFloatVelocity()[1] > 0 && !model.mayMarioJump());

        if((jumpEnemy(pos, screen) || jumpGap(pos, level)  || obstacle) && willJumpingDoAnything && !isMarioFalling)
            return new boolean[]{false, true, false, true, true};

        if(jumpEnemy(pos, screen) && isMarioFalling)
            return new boolean[]{false, false, false, false, false};*/

        DecisionTree dt = new DecisionTree(model);
        return dt.eval(model);

        // Go right
        //return new boolean[]{false, true, false, true, false};
    }

    @Override
    public String getAgentName() {
        return "LeandresStusalitusAgent";
    }
}
