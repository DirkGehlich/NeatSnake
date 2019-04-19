package io.battlesnake.neat;

public class NodeGene {

	public enum Type {
		Input,
		Hidden,
		Output;
	}
	
	private Type type;

	
	public NodeGene(Type type) {
		super();
		this.type = type;
	}

	public Type getType() {
		return type;
	}
	
	
}
