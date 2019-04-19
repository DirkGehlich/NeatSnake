package io.battlesnake.neat;

public class ConnectionGene extends Gene {

	private int inNodeInnovationNr;
	private int outNodeInnovationNr;
	private float weight;
	private boolean enabled;
	
	
	public ConnectionGene(int inNodeInnovationNr, int outNodeInnovationNr, float weight, boolean enabled, int innovationNr) {
		super(innovationNr);
		this.inNodeInnovationNr = inNodeInnovationNr;
		this.outNodeInnovationNr = outNodeInnovationNr;
		this.weight = weight;
		this.enabled = enabled;
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
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
}
