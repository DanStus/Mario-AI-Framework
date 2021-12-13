package agents.LeandresStusalitus;

import engine.core.MarioForwardModel;
import engine.sprites.Mario;

import java.util.Arrays;

public class DecisionTree {

    private final Node LEFT, RIGHT, DO_NOTHING, LEFT_JUMP, RIGHT_JUMP, FIRE, RIGHT_FIRE, RIGHT_JUMP_FIRE;

    private MarioForwardModel model;

    private boolean[] lastAction;
    private int lastActionCount;

    public DecisionTree(MarioForwardModel model){

        this.model = model;
        lastAction = new boolean[]{false, false, false ,false, false};
        lastActionCount = 0;

        LEFT = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{true,false,false,true,false};
            }
        };

        RIGHT = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{false,true,false,true,false};
            }
        };

        DO_NOTHING = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{false,false,false,true,false};
            }
        };

        LEFT_JUMP = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{true,false,false,true,true};
            }
        };

        RIGHT_JUMP = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{false,true,false,true,true};
            }
        };

        FIRE = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{false,false,true,true,false};
            }
        };

        RIGHT_FIRE= new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{false,true,true,true,false};
            }
        };

        RIGHT_JUMP_FIRE = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{false,true,true,true,true};
            }
        };
    }

    public boolean[] eval(MarioForwardModel model){
        this.model = model;
        //TODO add a node for checking if enemies are going to fall onto us
        //TODO find a way to dd human error via random nodes
        //TODO move these into the constructor I guess?
        CanJumpNode canJump = new CanJumpNode(RIGHT_JUMP, DO_NOTHING);
        JumpEnemyNode jumpEnemy = new JumpEnemyNode(canJump, RIGHT);
        JumpGapNode jumpGap = new JumpGapNode(canJump, jumpEnemy);
        ObstacleNode obstacleNode = new ObstacleNode(canJump, jumpGap);
        boolean[] newAction = obstacleNode.eval();

        double errorChance = 0.8 - 0.16*lastActionCount;

        // This if statement is a mess and needs to be refactored as I spent
        // 30 minutes trying to solve a problem here when the problem was
        // actually in the Agent class. Basically, there are better ways to
        // to do this but they weren't working because of an error in Agent
        // and they'll work now
        if(Arrays.equals(newAction, lastAction)){
            lastActionCount = 0;
            return newAction;
        }
        else {
            boolean[] finalAction = (new RandomNode(errorChance, new Node(){
                @Override
                public boolean[] eval() {
                    return lastAction;
                }
            }, new Node() {
                @Override
                public boolean[] eval() {
                    return newAction;
                }
            })).eval();
            if(Arrays.equals(finalAction, lastAction)){
                lastActionCount++;
            }
            else {
                this.lastAction = newAction;
                lastActionCount = 0;
            }
            return finalAction;
        }
    }

    class JumpEnemyNode extends DecisionNode{
        public JumpEnemyNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            int[] marioPos = model.getMarioScreenTilePos();
            int[][] level = model.getScreenCompleteObservation(0,0);

            for(int x = marioPos[0]; x <= marioPos[0]+3; x++){
                for(int y = marioPos[1]-(x-marioPos[0]); y <= marioPos[1]+(x-marioPos[0]); y++){
                    if(y >= 0 && y <= 15 && level[x][y] == 2){
                        return this.getLeaves()[0].eval();
                    }
                }
            }

            return this.getLeaves()[1].eval();
        }
    }

    class JumpGapNode extends DecisionNode{
        //TODO change definition of gap to include the pillars in lvl 2
        int[] marioPos = model.getMarioScreenTilePos();
        int[][] level = model.getScreenSceneObservation(2);

        public JumpGapNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            for (int x = marioPos[0]+1; x <= marioPos[0] + 2; x++){
                for(int y = marioPos[1]; y <= 15; y++){
                    // If we find a block, then there is no gap
                    // Skip to the next X coord
                    if(level[x][y] != 0){
                        break;
                    }
                    else if (y == 15){
                        return this.getLeaves()[0].eval();
                    }
                }
            }
            return this.getLeaves()[1].eval();
        }
    }

    class ObstacleNode extends DecisionNode{
        public ObstacleNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            int[] marioPos = model.getMarioScreenTilePos();
            int[][] level = model.getScreenSceneObservation(2);
            if(level[marioPos[0]+1][marioPos[1]] != 0 || level[marioPos[0]+2][marioPos[1]] != 0 ||
                    level[marioPos[0]+3][marioPos[1]] != 0)
                return this.getLeaves()[0].eval();
            else
                return this.getLeaves()[1].eval();
        }
    }

    class CanJumpNode extends DecisionNode{
        public CanJumpNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            if(model.getMarioFloatVelocity()[1] > 0 && !model.mayMarioJump())
                return this.getLeaves()[1].eval();
            else
                return this.getLeaves()[0].eval();
        }
    }
    /*class JumpEnemyNode extends DecisionNode{
        public JumpEnemyNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){

        }
    }*/
}
