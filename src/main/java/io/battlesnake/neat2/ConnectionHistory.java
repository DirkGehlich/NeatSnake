package io.battlesnake.neat2;

import java.util.ArrayList;

public class ConnectionHistory {
	
	private int fromNode;
	private int toNode;
	public int innovationNumber;
	
	ArrayList<Integer> innovationNumbers = new ArrayList<Integer>();
	
	@SuppressWarnings("unchecked")
	public ConnectionHistory(int from, int to, int inno, ArrayList<Integer> innovationNos) {
		this.fromNode = from;
		this.toNode = to;
		this.innovationNumber = inno;
		this.innovationNumbers = (ArrayList<Integer>)innovationNos.clone();
	}
	
	public boolean matches(Genome genome, Node from, Node to) {
		if (genome.genes.size() == innovationNumbers.size()) {
			if (from.number == fromNode && to.number == toNode) {
				for (int i=0; i<genome.genes.size(); i++) {
					if (!innovationNumbers.contains(genome.genes.get(i).innovationNo)) {
						return false;
					}
				}
				

				return true;
			}
		}
		
		return false;		
	}

}
