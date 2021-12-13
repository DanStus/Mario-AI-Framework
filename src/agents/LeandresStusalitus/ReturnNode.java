package agents.LeandresStusalitus;

public class ReturnNode extends Node{
    private boolean[] action;

    public ReturnNode(boolean[] action){
        this.action = action;
    }

    @Override
    public boolean[] eval() {
        return action;
    }
}
