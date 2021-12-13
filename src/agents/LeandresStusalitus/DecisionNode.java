package agents.LeandresStusalitus;

public class DecisionNode extends Node{

    private Node[] leaves;

    public DecisionNode(Node yesNode, Node noNode){
        this.leaves = new Node[]{yesNode, noNode};
    }

    @Override
    public boolean[] eval() {
        return new boolean[0];
    }

    public Node[] getLeaves(){
        return leaves;
    }
}
