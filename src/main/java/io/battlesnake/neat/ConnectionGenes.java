package io.battlesnake.neat;

import java.util.ArrayList;

public class ConnectionGenes extends ArrayList<ConnectionGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean isGeneConnected(int nodeGeneIdx) {		
		return stream().anyMatch(g -> g.getInNodeIdx() == nodeGeneIdx || g.getOutNodeIdx() == nodeGeneIdx);
	}
}
