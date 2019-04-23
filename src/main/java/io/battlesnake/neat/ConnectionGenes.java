package io.battlesnake.neat;

import java.util.ArrayList;

public class ConnectionGenes extends ArrayList<ConnectionGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A NodeGene is connected when its part of any ConnectionGene (either in or out)  
	 * @param nodeGeneIdx the NodeGene index to test the connection for
	 * @return true if its part of any connection
	 */
	public boolean isGeneConnected(int innovationNumber) {		
		return stream().anyMatch(g -> g.getInNodeInnovationNr() == innovationNumber || g.getOutNodeInnovationNr() == innovationNumber);
	}
	
	public ConnectionGene getConnectionGene(int innovationNr) {
		return this.stream().filter(g -> g.innovationNr == innovationNr).findFirst().orElse(null);
	}
}
