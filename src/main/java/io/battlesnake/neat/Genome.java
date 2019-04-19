package io.battlesnake.neat;

import java.util.Random;

import io.battlesnake.neat.NodeGene.Type;

public class Genome {

	private Random random = new Random();
	private NodeGenes nodeGenes = new NodeGenes();
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
		
		NodeGene node1 = nodeGenes.get(random.nextInt(nodeGenes.size()));
		NodeGene node2 = nodeGenes.get(random.nextInt(nodeGenes.size()));

		if (connectionGenes.isGeneConnected(node1.getInnovationNumber()) || connectionGenes.isGeneConnected(node2.getInnovationNumber())) {
			return;
		}

		// TODO: Don't connect same layers?
		if (node1.getType() == node2.getType()) {
			return;
		}

		boolean invertConnection = (node1.getType() == Type.Hidden && node2.getType() == Type.Input
				|| node1.getType() == Type.Output);

		float randomWeight = random.nextFloat() * 2.0f - 1.0f;
		boolean enabled = true;
		int innovationNr = InnovationNrGenerator.getNext();
		ConnectionGene connection = new ConnectionGene(invertConnection ? node2.getInnovationNumber() : node1.getInnovationNumber(),
				invertConnection ? node1.getInnovationNumber() : node2.getInnovationNumber(), randomWeight, enabled, innovationNr);

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
		
		NodeGene newNode = new NodeGene(Type.Hidden, InnovationNrGenerator.getNext());
		nodeGenes.add(newNode);
		int newNodeIdx = nodeGenes.size()-1;
		
		oldConnection.disable();
		ConnectionGene connectionIn = new ConnectionGene(oldConnection.getInNodeInnovationNr(), newNodeIdx, 1, true, InnovationNrGenerator.getNext());
		ConnectionGene connectionOut = new ConnectionGene(newNodeIdx, oldConnection.getOutNodeInnovationNr(), oldConnection.getWeight(), true, InnovationNrGenerator.getNext());
		
		connectionGenes.add(connectionIn);
		connectionGenes.add(connectionOut);		
	}

	public NodeGenes getNodeGenes() {
		return nodeGenes;
	}

	public ConnectionGenes getConnectionGenes() {
		return connectionGenes;
	}

}
