package io.battlesnake.neat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.battlesnake.neat.NodeGene.Type;

public class Genome {

	private Random random = new Random();
	private List<NodeGene> nodeGenes = new ArrayList<NodeGene>();
	private ConnectionGenes connectionGenes = new ConnectionGenes();

	
	public Genome(Random random) {
		super();
		this.random = random;
	}
	
	/**
	 * Add nodeGene to list of nodeGenes
	 * @param nodeGene to add
	 */
	public void addNodeGene(NodeGene nodeGene) {
		nodeGenes.add(nodeGene);
	}
	
	/**
	 * Add connectionGene to list of connectionGenes
	 * @param connectionGene to add
	 */
	public void addConnectionGene(ConnectionGene connectionGene) {
		connectionGenes.add(connectionGene);
	}

	/**
	 * "A single new connection gene with a random weight is added connecting two
	 * previously unconnected nodes"
	 */
	public void performAddConnectionMutation() {
		if (nodeGenes.isEmpty()) {
			return;
		}
		
		int node1Idx = random.nextInt(nodeGenes.size());
		int node2Idx = random.nextInt(nodeGenes.size());

		if (connectionGenes.isGeneConnected(node1Idx) || connectionGenes.isGeneConnected(node2Idx)) {
			return;
		}

		NodeGene node1 = nodeGenes.get(node1Idx);
		NodeGene node2 = nodeGenes.get(node2Idx);

		// TODO: Don't connect same layers?
		if (node1.getType() == node2.getType()) {
			return;
		}

		boolean invertConnection = (node1.getType() == Type.Hidden && node2.getType() == Type.Input
				|| node1.getType() == Type.Output);

		float randomWeight = random.nextFloat() * 2.0f - 1.0f;
		boolean enabled = true;
		int innovationNr = InnovationNrGenerator.getNext();
		ConnectionGene connection = new ConnectionGene(invertConnection ? node2Idx : node1Idx,
				invertConnection ? node1Idx : node2Idx, randomWeight, enabled, innovationNr);

		connectionGenes.add(connection);
	}
	
	/**
	 * "An existing connection is split and the new node placed where the old connection used to be.
	 * The old connection is disabled and two new connections are added to the genome.
	 * The new connection leading into the new node receives a weight of 1,
	 * and the new connection leading out receives the same weight as the old connection.
	 */
	public void performAddNodeMutation() {
		if (connectionGenes.isEmpty()) {
			return;
		}
		
		ConnectionGene oldConnection = connectionGenes.get(random.nextInt(connectionGenes.size()));
		
		NodeGene newNode = new NodeGene(Type.Hidden);
		nodeGenes.add(newNode);
		int newNodeIdx = nodeGenes.size()-1;
		
		oldConnection.disable();
		ConnectionGene connectionIn = new ConnectionGene(oldConnection.getInNodeIdx(), newNodeIdx, 1, true, InnovationNrGenerator.getNext());
		ConnectionGene connectionOut = new ConnectionGene(newNodeIdx, oldConnection.getOutNodeIdx(), oldConnection.getWeight(), true, InnovationNrGenerator.getNext());
		
		connectionGenes.add(connectionIn);
		connectionGenes.add(connectionOut);		
	}

	public List<NodeGene> getNodeGenes() {
		return nodeGenes;
	}

	public ConnectionGenes getConnectionGenes() {
		return connectionGenes;
	}

}
