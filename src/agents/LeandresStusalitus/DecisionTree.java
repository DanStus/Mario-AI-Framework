package agents.LeandresStusalitus;

import engine.core.MarioForwardModel;
import engine.sprites.Mario;

public class DecisionTree {

    private final Node LEFT, RIGHT, DO_NOTHING, LEFT_JUMP, RIGHT_JUMP, FIRE, LEFT_FIRE, RIGHT_FIRE, RIGHT_JUMP_FIRE;

    private MarioForwardModel model;

    public DecisionTree(MarioForwardModel model){

        this.model = model;

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

        LEFT_FIRE = new Node() {
            @Override
            public boolean[] eval() {
                return new boolean[]{true,false,true,true,false};
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
        CanJumpNode canJump = new CanJumpNode(RIGHT_JUMP, RIGHT);
        JumpEnemyNode jumpEnemy = new JumpEnemyNode(canJump, RIGHT);
        JumpGapNode jumpGap = new JumpGapNode(canJump, jumpEnemy);
        ObstacleNode obstacleNode = new ObstacleNode(canJump, jumpGap);
        return obstacleNode.eval();
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
