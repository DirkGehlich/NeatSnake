package io.battlesnake.neat;

import java.util.ArrayList;

public class NodeGenes extends ArrayList<NodeGene> {

	public NodeGenes() {
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NodeGenes(NodeGenes nodeGenes) {
		for (NodeGene node : nodeGenes) {
			add(node.copy());
		}		
	}
	
	public NodeGenes copy() {
		return new NodeGenes(this);
	}

	public NodeGene getByInnovatioNr(int innovationNr) {
		return stream().filter(n -> n.getInnovationNr() == innovationNr).findFirst().orElseThrow(() -> new RuntimeException("NodeGene not found!"));
	}
}
