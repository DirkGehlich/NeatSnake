package io.battlesnake.neat;

public class ConnectionGene {

	private int inNodeIdx;
	private int outNodeIdx;
	private float weight;
	private boolean enabled;
	private int innovationNr;
	
	
	public ConnectionGene(int inNodeIdx, int outNodeIdx, float weight, boolean enabled, int innovationNr) {
		super();
		this.inNodeIdx = inNodeIdx;
		this.outNodeIdx = outNodeIdx;
		this.weight = weight;
		this.enabled = enabled;
		this.innovationNr = innovationNr;
	}
	
	public int getInNodeIdx() {
		return inNodeIdx;
	}
	
	public int getOutNodeIdx() {
		return outNodeIdx;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public int getInnovationNr() {
		return innovationNr;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
}
