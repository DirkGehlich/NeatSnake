package io.battlesnake.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionGenes extends ArrayList<ConnectionGene>  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxInnovationNr = 0;
	
	public ConnectionGenes() {
		
	}
	
	private ConnectionGenes(ConnectionGenes connections) {
		for (ConnectionGene connection : connections) {
			add(connection.copy());
		}
	}
	
	public ConnectionGenes copy() {
		return new ConnectionGenes(this);
	}
	
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
	
	
	@Override
	public boolean add(ConnectionGene e) {
		if (e.getInnovationNr() > maxInnovationNr) {
			maxInnovationNr = e.getInnovationNr();
		}
		
		return super.add(e);
	}

	public int getMaxInnovationNr() {
		return maxInnovationNr;
	}
	
	public boolean isNodeConnected(int srcInnovationNr, int toFollowInnovationNr) {		
		if (srcInnovationNr == toFollowInnovationNr) {
			return true;
		}
		
		List<ConnectionGene> connections = stream().filter(c -> c.getInNodeInnovationNr() == toFollowInnovationNr).collect(Collectors.toList());
		for (ConnectionGene connection : connections) {
			if (isNodeConnected(srcInnovationNr, connection.getOutNodeInnovationNr())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString()  {
		String s = "Connections\n-------------------\n";
		for (ConnectionGene connection : this) {
			s += connection.toString() + "\n";
		}
		
		return s;
	}
}
