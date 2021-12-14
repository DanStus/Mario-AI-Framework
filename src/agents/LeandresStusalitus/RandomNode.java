package agents.LeandresStusalitus;

import java.util.Random;

public class RandomNode extends DecisionNode{

    private final double chanceYes;
    private final boolean doubleRandom;

    public RandomNode(double chanceYes, boolean doubleRandom, Node yesNode, Node noNode){
        super(yesNode, noNode);
        this.chanceYes = chanceYes;
        this.doubleRandom = doubleRandom;
    }

    @Override
    public boolean[] eval() {
        Random r = new Random();
        double d;
        if(doubleRandom){
            double d1 = r.nextDouble();
            double d2 = r.nextDouble();
            d = (d1+d2)/2;
        }
        else {
            d = r.nextDouble();
        }

        if(d <= chanceYes)
            return this.getLeaves()[0].eval();
        else
            return this.getLeaves()[1].eval();
    }
}
