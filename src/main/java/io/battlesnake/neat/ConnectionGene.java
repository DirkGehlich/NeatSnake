package io.battlesnake.neat;

import java.io.Serializable;

public class ConnectionGene extends Gene  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int inNodeInnovationNr;
	private int outNodeInnovationNr;
	private double weight;
	private boolean enabled;

	public ConnectionGene(int inNodeInnovationNr, int outNodeInnovationNr, double weight, boolean enabled,
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void disable() {
		this.enabled = false;
	}

	public void enable() {
		this.enabled = true;
	}
	
	@Override
	public String toString() {
		return String.format("%d --%s--> %d: %f", inNodeInnovationNr, Boolean.toString(enabled), outNodeInnovationNr, weight); 
	}

}
