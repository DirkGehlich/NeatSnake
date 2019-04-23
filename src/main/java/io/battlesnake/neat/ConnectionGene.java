package io.battlesnake.neat;

public class ConnectionGene extends Gene {

	private int inNodeInnovationNr;
	private int outNodeInnovationNr;
	private float weight;
	private boolean enabled;

	public ConnectionGene(int inNodeInnovationNr, int outNodeInnovationNr, float weight, boolean enabled,
			int innovationNr) {
		super(innovationNr);
		this.inNodeInnovationNr = inNodeInnovationNr;
		this.outNodeInnovationNr = outNodeInnovationNr;
		this.weight = weight;
		this.enabled = enabled;
	}

	public ConnectionGene copy() {
		return new ConnectionGene(inNodeInnovationNr, outNodeInnovationNr, weight, enabled, innovationNr);
	}

	public int getInNodeInnovationNr() {
		return inNodeInnovationNr;
	}

	public int getOutNodeInnovationNr() {
		return outNodeInnovationNr;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void disable() {
		this.enabled = false;
	}

}
