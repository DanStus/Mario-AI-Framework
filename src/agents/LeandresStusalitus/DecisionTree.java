package agents.LeandresStusalitus;

import engine.core.MarioForwardModel;

public class DecisionTree {

    private final Node LEFT, RIGHT, DO_NOTHING, LEFT_JUMP, RIGHT_JUMP, FIRE, LEFT_FIRE, RIGHT_FIRE, RIGHT_JUMP_FIRE;

    public DecisionTree(){
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
        return new boolean[5];
    }
}
