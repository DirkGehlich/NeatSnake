package io.battlesnake.neat;

import java.io.Serializable;
import java.util.ArrayList;

public class NodeGenes extends ArrayList<NodeGene>  implements Serializable {

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
	
	@Override
	public String toString() {
		String s = "Nodes\n-------------------\n";
		for (NodeGene node : this) {
			s += node.toString() + "\n";
		}
		
		return s;
	}
}
