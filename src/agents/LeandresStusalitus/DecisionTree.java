package agents.LeandresStusalitus;

import engine.core.MarioForwardModel;

import java.util.Arrays;

public class DecisionTree {

    private final ReturnNode LEFT, RIGHT, DO_NOTHING, LEFT_JUMP, RIGHT_JUMP, FIRE, RIGHT_FIRE, RIGHT_JUMP_FIRE;
    private final DecisionNode areWeFalling, jumpEnemy, jumpGap, obstacleNode;

    private MarioForwardModel model;

    private boolean[] lastAction;
    private int lastActionCount;

    public DecisionTree(MarioForwardModel model){

        //Initialize some variables
        this.model = model;
        lastAction = new boolean[]{false, false, false ,false, false};
        lastActionCount = 0;

        // Always running([3] == true) unless specified as walk(ing) or doing nothing
        LEFT = new ReturnNode(new boolean[]{true,false,false,true,false});
        RIGHT = new ReturnNode(new boolean[]{false,true,false,true,false});
        DO_NOTHING = new ReturnNode(new boolean[]{false,false,false,true,false});
        LEFT_JUMP = new ReturnNode(new boolean[]{true,false,false,true,true});
        RIGHT_JUMP = new ReturnNode(new boolean[]{false,true,false,true,true});
        FIRE = new ReturnNode(new boolean[]{false,false,true,true,false});
        RIGHT_FIRE= new ReturnNode(new boolean[]{false,true,true,true,false});
        RIGHT_JUMP_FIRE = new ReturnNode(new boolean[]{false,true,true,true,true});

        //TODO add a node for checking if enemies are going to fall onto us
        areWeFalling = new FallingNode(DO_NOTHING, RIGHT_JUMP);
        jumpEnemy = new JumpEnemyNode(areWeFalling, RIGHT);
        jumpGap = new JumpGapNode(areWeFalling, jumpEnemy);
        obstacleNode = new ObstacleNode(areWeFalling, jumpGap);
    }

    public boolean[] eval(MarioForwardModel model){
        // Update the model to reflect the current state of the game
        this.model = model;
        boolean[] newAction = obstacleNode.eval();

        // If we're doing the same action as before, nothing special
        if(Arrays.equals(newAction, lastAction)){
            lastActionCount = 0;
            return newAction;
        }
        // If we're trying to do a new action
        else {

            // We use a RandomNode, set to use 2 random numbers, to decide if we're holding the
            // last action for another frame by accident or not. The average of 2 random numbers
            // tends towards 0.5, so this exaggerates probabilities as they get further from 0.5.
            // (i.e. If errorChance > 0.5, actual chance to repeat inputs is greater than errorChance,
            // and actual chance is lower if errorChance < 0.5) This is a more accurate simulation
            // of human error than using just 1 random number would be.


            // Average number of extra frames the old action is held for = 2.054

            double errorChance = 0.8 - 0.15*lastActionCount;
            boolean[] finalAction = (new RandomNode(errorChance, true, new ReturnNode(lastAction), new ReturnNode(newAction))).eval();
            // If we are repeating lastAction, increment counter to adjust errorChance
            if(Arrays.equals(finalAction, lastAction)){
                lastActionCount++;
            }
            // If not, then update variables to reflect the new action we are taking
            else {
                this.lastAction = newAction;
                lastActionCount = 0;
            }
            return finalAction;
        }
    }

    // This node checks if there is an enemy we need to jump over
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

    // This node checks if there is a hole/gap in the ground we
    // would die if we fell into that we need to jump over
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

    // This node checks if there is an obstacle in our way that we need to jump over
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

    // This node checks if we are falling and holding jump is useless
    // If we are falling, execute yesNode/yesBranch
    // If we can hold jump to perform a jump or increase the height
    // of our jump, then execute noNode/noBranch
    class FallingNode extends DecisionNode{
        public FallingNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            if(model.getMarioFloatVelocity()[1] > 0 && !model.mayMarioJump())
                return this.getLeaves()[0].eval();
            else
                return this.getLeaves()[1].eval();
        }
    }

    // This node is a work in progress, intended to have us slow down when
    // we are approaching enemies to reduce deaths to "human error"
    class SlowDownBeforeEnemyNode extends DecisionNode{
        public SlowDownBeforeEnemyNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            //TODO implement this
            return new boolean[5];
        }
    }

    //Code in comments to copy and paste so I don't get arthritis by the time I'm 30
    /*class JumpEnemyNode extends DecisionNode{
        public JumpEnemyNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){

        }
    }*/
}
