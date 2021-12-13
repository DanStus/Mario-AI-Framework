package agents.LeandresStusalitus;

import java.util.Random;

public class RandomNode extends DecisionNode{

    private double chanceYes;

    public RandomNode(double chanceYes, Node yesNode, Node noNode){
        super(yesNode, noNode);
        this.chanceYes = chanceYes;
    }

    @Override
    public boolean[] eval() {
        Random r = new Random();
        double d = r.nextDouble();
        if(d <= chanceYes)
            return this.getLeaves()[0].eval();
        else
            return this.getLeaves()[1].eval();
    }
}
