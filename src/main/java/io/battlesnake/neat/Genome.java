package io.battlesnake.neat;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.battlesnake.neat.NodeGene.Type;

public class Genome {

	private Random random = new Random();
	private NodeGenes nodeGenes = new NodeGenes();
	private ConnectionGenes connectionGenes = new ConnectionGenes();
	private double fitness;
	private final int numOutNodes;

	public Genome(Random random, int numOutNodes) {
		super();
		this.random = random;
		this.numOutNodes = numOutNodes;
	}
	
	private Genome(Genome genome) {
		this.random = genome.random;
		this.nodeGenes = genome.nodeGenes.copy();
		this.connectionGenes = genome.connectionGenes.copy();
		this.fitness = genome.fitness;
		this.numOutNodes = genome.numOutNodes;
	}
	
	public Genome copy() {
		return new Genome(this);
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public int getNumOutNodes() {
		return numOutNodes;
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
	 * "each weight had a 90% chance of
	 * being uniformly perturbed and a 10% chance of being assigned a new random value."
	 */
	public void performWeightMutation() {
		for (ConnectionGene connection : connectionGenes) {
			float rnd = random.nextFloat() * 2f - 1f;
			if (random.nextFloat() < Parameters.weightPerturbingChance) {
				connection.setWeight(connection.getWeight() * rnd);
			} else  {
				connection.setWeight(rnd);
			}			
		}
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

		if (connectionGenes.isGeneConnected(node1.getInnovationNr()) || connectionGenes.isGeneConnected(node2.getInnovationNr())) {
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
		ConnectionGene connection = new ConnectionGene(invertConnection ? node2.getInnovationNr() : node1.getInnovationNr(),
				invertConnection ? node1.getInnovationNr() : node2.getInnovationNr(), randomWeight, enabled, innovationNr);

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
		
		oldConnection.disable();
		ConnectionGene connectionIn = new ConnectionGene(oldConnection.getInNodeInnovationNr(), newNode.getInnovationNr(), 1, true, InnovationNrGenerator.getNext());
		ConnectionGene connectionOut = new ConnectionGene(newNode.getInnovationNr(), oldConnection.getOutNodeInnovationNr(), oldConnection.getWeight(), true, InnovationNrGenerator.getNext());
		
		connectionGenes.add(connectionIn);
		connectionGenes.add(connectionOut);		
	}

	public NodeGenes getNodeGenes() {
		return nodeGenes;
	}

	public ConnectionGenes getConnectionGenes() {
		return connectionGenes;
	}
	
	public float calculateCompatibilityDistanceTo(Genome genome) {

		int cntExcessGenes = 0;
		int cntDisjointGenes = 0;
		int cntGenesLargerGenome = 0;
		int cntMatchingGenes = 0;
		float weightDiffSum = 0;

		ConnectionGenes genome1Connections = getConnectionGenes();
		ConnectionGenes genome2Connections = genome.getConnectionGenes();

		int maxGenome1InnovatioNr = genome1Connections.getMaxInnovationNr();
		int maxGenome2InnovatioNr = genome2Connections.getMaxInnovationNr();

		int cntGenome1Connections = genome1Connections.size();
		int cntGenome2Connections = genome2Connections.size();
		cntGenesLargerGenome = Math.max(cntGenome1Connections, cntGenome2Connections);

		for (int i = 0; i < cntGenome1Connections; ++i) {
			ConnectionGene connection1 = genome1Connections.get(i);

			if (genome2Connections.contains(connection1)) {
				cntMatchingGenes++;

				ConnectionGene connection2 = genome2Connections.getConnectionGene(connection1.getInnovationNr());
				weightDiffSum += Math.abs(connection1.getWeight() - connection2.getWeight());
			} else {
				if (connection1.getInnovationNr() < maxGenome2InnovatioNr) {
					cntDisjointGenes++;
				} else {
					cntExcessGenes++;
				}
			}
		}

		for (int i = 0; i < cntGenome2Connections; ++i) {
			ConnectionGene connection2 = genome2Connections.get(i);
			if (!genome1Connections.contains(connection2)) {
				if (connection2.getInnovationNr() < maxGenome1InnovatioNr) {
					cntDisjointGenes++;
				} else {
					cntExcessGenes++;
				}
			}
		}

		int n = cntGenesLargerGenome > 20 ? cntGenesLargerGenome : 1;
		float avgWeightDifference = (float) weightDiffSum / cntMatchingGenes;

		float delta = (float) (Parameters.c1 * cntExcessGenes / n) + (float) (Parameters.c2 * cntDisjointGenes / n)
				+ Parameters.c3 * avgWeightDifference;
		return delta;
	}
	/**
	 * A genome fits into a species if its calculated compatibility distance is less then a given threshold
	 * @param genome the genome to test 
	 * @param species the species to test against
	 * @return true if genome fits into species (based on the compatibility distance), false otherwise
	 */
	public boolean fitsIntoSpecies(Species species) {
		return calculateCompatibilityDistanceTo(species.getRepresentative()) < Parameters.compatibilityThreshold;
	}

	public void mutate() {
		if (random.nextFloat() < Parameters.weightMutationChance) {
			performWeightMutation();
		}
		
		// TODO: disable connection mutation
		
		if (random.nextFloat() < Parameters.addNodeMutationChance) {
			performAddNodeMutation();
		}
		
		if (random.nextFloat() < Parameters.addConnectionMutationChance) {
			performAddConnectionMutation();
		}
	}
		
	public void setInputs(float[] inputs) {
		List<NodeGene> inputNodes = getNodeGenes().stream().filter(g -> g.getType() == Type.Input).collect(Collectors.toList());
		if (inputs.length != inputNodes.size()) {
			throw new RuntimeException("Size of inputs does not equal size of input neurons");
		}
		
		for (int i=0; i<inputs.length; ++i) {
			inputNodes.get(i).setActivation(inputs[i]);
		}
	}
	
	public double[] calculate() {
		double[] outputs = new double[numOutNodes];
				
		for (ConnectionGene connection : getConnectionGenes()) {
			int inNodeInnovatioNr = connection.getInNodeInnovationNr();
			NodeGene inNode = getNodeGenes().getByInnovatioNr(inNodeInnovatioNr);
			double signal = inNode.getActivation() * connection.getWeight();
			connection.setSignal(signal);
		}
		
		for (ConnectionGene connection : getConnectionGenes()) {
			int outNodeInnovatioNr = connection.getOutNodeInnovationNr();
			NodeGene outNode = getNodeGenes().getByInnovatioNr(outNodeInnovatioNr);
			outNode.addActivationSum(connection.getSignal());
		}
		
		getNodeGenes().stream().filter(n -> (n.getType() != Type.Input && n.getType() != Type.Bias)).forEach(n1 -> {
			double activationSum = n1.getActivationSum();
			n1.restActivationSum();
			n1.setActivation(activate(activationSum));
			
		});
		
		List<NodeGene> outNodes = getNodeGenes().stream().filter(n -> n.getType() == Type.Output).collect(Collectors.toList());
		for (int i=0; i<outNodes.size(); ++i) {
			outputs[i] = outNodes.get(i).getActivation();
		}
		
		return outputs;
	}
	
	private double activate(double x) {
		double activation = 1/(1 + Math.exp(-4.9 * x));
		
		return activation;
	}
	
	
}
