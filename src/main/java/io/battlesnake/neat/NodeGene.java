package io.battlesnake.neat;

public class NodeGene extends Gene {

	public enum Type {
		Input,
		Hidden,
		Output;
	}
	
	private Type type;

	
	public NodeGene(Type type, int innovationNumber) {
		super(innovationNumber);
		this.type = type;
	}

	public Type getType() {
		return type;
	}
	
	
}
