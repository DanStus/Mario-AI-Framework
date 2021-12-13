package agents.LeandresStusalitus;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;

public class Agent implements MarioAgent {

    private DecisionTree dt;


    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        dt = new DecisionTree(model);
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        return dt.eval(model);
    }

    @Override
    public String getAgentName() {
        return "LeandresStusalitusAgent";
    }
}
