package agents.LeandresStusalitus;

import engine.core.MarioForwardModel;

import java.util.Arrays;
import java.util.Random;

public class DecisionTree {

    private final ReturnNode LEFT, RIGHT, DO_NOTHING, LEFT_JUMP, RIGHT_JUMP, FIRE, RIGHT_FIRE, RIGHT_JUMP_FIRE, WALK_LEFT;
    private final DecisionNode stall, stallOrJump, shouldWeHoldJump, enemyBelow, enemyInFront, jumpGap, obstacleNode;
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
        WALK_LEFT = new ReturnNode(new boolean[]{true, false, false, false, false});

        //TODO add a node for checking if enemies are going to fall onto us

        stall = new Alternator(WALK_LEFT, DO_NOTHING);
        stallOrJump = new FallingNode(stall, RIGHT_JUMP);
        shouldWeHoldJump = new FallingNode(RIGHT, RIGHT_JUMP);
        obstacleNode = new ObstacleNode(shouldWeHoldJump, RIGHT);
        jumpGap = new JumpGapNode(shouldWeHoldJump, obstacleNode);
        enemyBelow = new EnemyBelowNode(stallOrJump, jumpGap);
        enemyInFront = new EnemyInFrontNode(stallOrJump, enemyBelow);
    }

    public boolean[] eval(MarioForwardModel model){
        // Update the model to reflect the current state of the game
        this.model = model;
        // Get what we want to do next
        boolean[] newAction = Arrays.copyOf(enemyInFront.eval(), 5);

        // If left, right, and jump stay the same, then nothing happens
        // Otherwise apply a bit of human error
        if(newAction[0] == lastAction[0] && newAction[1] == lastAction[1] && newAction[4] == lastAction[4]){
            lastActionCount = 0;
            lastAction = Arrays.copyOf(newAction, 5);
            return newAction;
        }
        else {

            // We use a RandomNode, set to use 2 random numbers, to decide if we're holding the
            // last action for another frame(?) by accident or not. The average of 2 random numbers
            // tends towards 0.5, so this exaggerates probabilities as they get further from 0.5.
            // (i.e. If errorChance > 0.5, actual chance to repeat inputs is greater than errorChance,
            // and actual chance is lower if errorChance < 0.5) This is a more accurate simulation
            // of human error than using just 1 random number would be.

            double errorChance = 0.8 - 0.18*lastActionCount;
            // If we're changing directions then reduce the amount of lag we should expect
            if(newAction[0] != lastAction[0] || newAction[1] != lastAction[1])
                errorChance = errorChance - 0.18;
            // We don't care about down and speed for human error, so make sure they get updated
            lastAction[2] = newAction[2];
            lastAction[3] = newAction[3];
            boolean[] finalAction = (new RandomNode(errorChance, true, new ReturnNode(lastAction), new ReturnNode(newAction))).eval();
            // If we are repeating lastAction, increment counter to adjust errorChance
            if(finalAction[0] == lastAction[0] && finalAction[1] == lastAction[1] && finalAction[4] == lastAction[4]){
                lastActionCount++;
                System.out.println(lastActionCount);
            }
            // If not, then reset the counter
            else {
                lastActionCount = 0;
            }
            this.lastAction = Arrays.copyOf(finalAction, 5);
            return finalAction;
        }
    }

    class EnemyBelowNode extends DecisionNode{
        public EnemyBelowNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){

            float[] pos = model.getMarioFloatPos();
            pos[0] = pos[0]/16;
            pos[1] = pos[1]/16;
            float[] enemies = model.getEnemiesFloatPos();

            for(int  i = 0; i < enemies.length; i += 3){
                //int enemy = (int)enemies[i];
                float x = enemies[i+1]/16;
                float y = enemies[i+2]/16;
                // Are they below and ahead of us?
                // If not then we don't care
                if(x > pos[0]+0.5 && y > pos[1]+1){
                    // If they are reasonably more below us than in front, then who cares about them
                    // If not, then we stall to land in front
                    if(y - pos[1] < x - pos[0] - 0.6){
                        return this.getLeaves()[0].eval();
                    }
                }
            }

            return this.getLeaves()[1].eval();
        }
    }

    class EnemyInFrontNode extends DecisionNode{
        public EnemyInFrontNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){

            float[] pos = model.getMarioFloatPos();
            pos[0] = pos[0]/16;
            pos[1] = pos[1]/16;
            float[] enemies = model.getEnemiesFloatPos();

            for(int  i = 0; i < enemies.length; i += 3){
                //int enemy = (int)enemies[i];
                float x = enemies[i+1]/16;
                float y = enemies[i+2]/16;
                // Are they ahead of us and not above or below?
                // If not then we don't care
                if(x > pos[0] && y >= pos[1]-0.5 && y <= pos[1]+0.5){
                    // If they are ahead, we're only going to do anything when they get kind of close
                    if(x - pos[0] <= 4){
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
        // Make Mario react to pits earlier by using floats not ints

        public JumpGapNode(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            int[] marioPos = model.getMarioScreenTilePos();
            int[][] level = model.getScreenSceneObservation(2);
            for (int x = marioPos[0]+1; x <= marioPos[0] + 3; x++){
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
            if(marioPos[1] < 16 && (level[marioPos[0]+1][marioPos[1]] != 0 || level[marioPos[0]+2][marioPos[1]] != 0 ||
                    level[marioPos[0]+3][marioPos[1]] != 0))
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

    class Alternator extends DecisionNode{
        private int flag = 0;

        public Alternator(Node yesNode, Node noNode){
            super(yesNode, noNode);
        }
        @Override
        public boolean[] eval(){
            if(flag == 0){
                Random r = new Random();
                double d = r.nextDouble();
                if(d > 0.5)
                    flag = 1;
                else
                    flag = -1;
            }

            if(flag == 1){
                flag = -1;
                return this.getLeaves()[0].eval();
            }
            else {
                flag = 1;
                return this.getLeaves()[1].eval();
            }
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
