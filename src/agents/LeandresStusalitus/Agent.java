package agents.LeandresStusalitus;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

import java.util.Random;

public class Agent implements MarioAgent {

    private boolean[] action;

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        this.action = new boolean[MarioActions.numberOfActions()];
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        // Potentially useful methods
        // getEnemiesFloatPos
        // getMarioScreenTilePos
        // getScreenCompleteObservation
        // getScreenEnemiesObservation
        int[][] screen = model.getScreenCompleteObservation(0,0);
        int[] pos = model.getMarioScreenTilePos();
        if(screen[pos[0]+1][pos[1]] == 2 || screen[pos[0]+2][pos[1]] == 2)
            return new boolean[]{false, true, false, true, true};

        int[][] level = model.getScreenSceneObservation(2);
        if(level[pos[0]+1][pos[1]] != 0 || level[pos[0]+2][pos[1]] != 0)
            return new boolean[]{false, true, false, true, true};

        return new boolean[]{false, true, false, true, false};
    }

    @Override
    public String getAgentName() {
        return "LeandresStusalitusAgent";
    }
}
