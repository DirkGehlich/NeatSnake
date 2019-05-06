package io.battlesnake.neat;

import java.io.Serializable;

public class Gene  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int innovationNr;

	
	public Gene(int innovationNr) {
		super();
		this.innovationNr = innovationNr;
	}

	public int getInnovationNr() {
		return innovationNr;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		ConnectionGene connection = (ConnectionGene)obj;
		
		return (connection.innovationNr == this.innovationNr);
	}
	
	@Override
	public int hashCode() {
		return innovationNr;
	}
}
