package io.battlesnake.neat;

public class NodeGene extends Gene {

	public enum Type {
		Input, Hidden, Output, Bias;
	}

	private double activation = 0;
	private Type type;
	private double weightedInputSum = 0;
	
	public NodeGene(Type type, int innovationNumber) {
		super(innovationNumber);
		this.type = type;
		
		if (type == Type.Bias) {
			activation = 1.0;
		}
	}
	
	public NodeGene(NodeGene nodeGene) {
		super(nodeGene.innovationNr);
		this.type = nodeGene.type;
		
		if (type == Type.Bias) {
			activation = 1.0;
		}
	}

	public NodeGene copy() {
		return new NodeGene(this);
	}

	public Type getType() {
		return type;
	}

	public double getActivation() {
		return activation;
	}

	public void setActivation(double activation) {
		this.activation = activation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		NodeGene nodeGene = (NodeGene)obj;
		
		return (nodeGene.innovationNr == this.innovationNr);		
	}
	
	@Override
	public int hashCode() {
		return this.innovationNr;
	}
	public void activate() {
		
		activation = 1/(1 + Math.exp(-4.9 * weightedInputSum));
	}
	
	public void addWeightedInputSum(double weightedInputSum) {
		this.weightedInputSum  += weightedInputSum;
	}
}
