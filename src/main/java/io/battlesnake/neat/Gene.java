package io.battlesnake.neat;

public class Gene {

	protected int innovationNr;

	
	public Gene(int innovationNr) {
		super();
		this.innovationNr = innovationNr;
	}

	public int getInnovationNumber() {
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
