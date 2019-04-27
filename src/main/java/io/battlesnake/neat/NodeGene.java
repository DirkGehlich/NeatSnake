package io.battlesnake.neat;

public class NodeGene extends Gene {

	public enum Type {
		Input, Hidden, Output, Bias;
	}

	private Type type;
	private double activation;
	private double activationSum;

	public NodeGene(Type type, int innovationNumber) {
		super(innovationNumber);
		this.type = type;
		
		if (type == Type.Bias) {
			activation = 1.0;
		}
	}

	public NodeGene copy() {
		return new NodeGene(type, innovationNr);
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

	public double getActivationSum() {
		return activationSum;
	}

	public void addActivationSum(double activation) {
		this.activationSum += activation;
	}
	
	public void restActivationSum() {
		this.activationSum = 0;
	}
}
