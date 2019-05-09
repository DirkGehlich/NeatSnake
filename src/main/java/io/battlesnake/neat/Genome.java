package io.battlesnake.neat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.battlesnake.neat.NodeGene.Type;

public class Genome implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Random random = new Random();
	private NodeGenes nodeGenes = new NodeGenes();
	private ConnectionGenes connectionGenes = new ConnectionGenes();
	private double fitness = 0.0;
	private double adjustedFitness = 0.0;
	private int numOutNodes = 0;

	public Genome(Random random) {
		super();
		this.random = random;
	}

	private Genome(Genome genome) {
		this.random = genome.random;
		this.nodeGenes = genome.nodeGenes.copy();
		this.connectionGenes = genome.connectionGenes.copy();
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

	public double getAdjustedFitness() {
		return adjustedFitness;
	}

	public void setAdjustedFitness(double adjustedFitness) {
		this.adjustedFitness = adjustedFitness;
	}

	public int getNumOutNodes() {
		return numOutNodes;
	}

	/**
	 * Add nodeGene to list of nodeGenes
	 * 
	 * @param nodeGene to add
	 */
	public void addNodeGene(NodeGene nodeGene) {
		nodeGenes.add(nodeGene);
		if (nodeGene.getType() == Type.Output) {
			++numOutNodes;
		}
	}

	/**
	 * Add connectionGene to list of connectionGenes
	 * 
	 * @param connectionGene to add
	 */
	public void addConnectionGene(ConnectionGene connectionGene) {
		if (!connectionGene.isEnabled()) {
			if (random.nextFloat() < Parameters.enableGeneMutationChance) {
				connectionGene.enable();
			}
		}
		connectionGenes.add(connectionGene);
	}

	/**
	 * "each weight had a 90% chance of being uniformly perturbed and a 10% chance
	 * of being assigned a new random value."
	 */
	public void performWeightMutation() {
		for (ConnectionGene connection : connectionGenes) {
			// TODO: use gausian with some weight mutation power instead of 1.0 as standard deviation?
			//float rnd = Parameters.minWeight + random.nextFloat() * (Parameters.maxWeight - Parameters.minWeight);
			double rnd = random.nextGaussian() * Parameters.weightMutationPower;
			if (random.nextFloat() < Parameters.weightPerturbingChance) {				
				double newWeight = connection.getWeight() + rnd;
				if (newWeight > Parameters.maxWeight) {
					newWeight = Parameters.maxWeight;
				}
				connection.setWeight(newWeight);
			} else {
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

		NodeGene node1 = null;
		NodeGene node2 = null;

		int triesLeft = 10;
		while (triesLeft > 0) {
			--triesLeft;

			node1 = nodeGenes.get(random.nextInt(nodeGenes.size()));
			node2 = nodeGenes.get(random.nextInt(nodeGenes.size()));
			int node1InnovationNr = node1.getInnovationNr();
			int node2InnovationNr = node2.getInnovationNr();

			boolean connectionExists = connectionGenes.stream()
					.anyMatch(c -> (c.getInNodeInnovationNr() == node1InnovationNr
							&& c.getOutNodeInnovationNr() == node2InnovationNr)
							|| c.getInNodeInnovationNr() == node2InnovationNr
									&& c.getOutNodeInnovationNr() == node1InnovationNr);

			if (connectionExists) {
				continue;
			}

			if ((node1.getType() == Type.Bias || node1.getType() == Type.Input)
					&& (node2.getType() == Type.Bias || node2.getType() == Type.Input)) {
				continue;
			}

			if (node1.getType() == Type.Output && node2.getType() == Type.Output) {
				continue;
			}

			boolean invertConnection = (node1.getType() == Type.Hidden
					&& (node2.getType() == Type.Input || node2.getType() == Type.Bias)
					|| node1.getType() == Type.Output);

			if (invertConnection) {
				NodeGene tmp = node1;
				node1 = node2;
				node2 = tmp;
			}

			if (!this.connectionGenes.isNodeConnected(node1.getInnovationNr(), node2.getInnovationNr())) {
				break;
			}

		}

		if (triesLeft == 0) {
			return;
		}

		float randomWeight = Parameters.minWeight + random.nextFloat() * (Parameters.maxWeight - Parameters.minWeight);
		boolean enabled = true;
		int innovationNr = InnovationNrGenerator.getNext();
		ConnectionGene connection = new ConnectionGene(node1.getInnovationNr(), node2.getInnovationNr(), randomWeight,
				enabled, innovationNr);

		addConnectionGene(connection);
	}

	/**
	 * "An existing connection is split and the new node placed where the old
	 * connection used to be. The old connection is disabled and two new connections
	 * are added to the genome. The new connection leading into the new node
	 * receives a weight of 1, and the new connection leading out receives the same
	 * weight as the old connection.
	 */
	public void performAddNodeMutation() {
		if (connectionGenes.isEmpty()) {
			return;
		}

		List<ConnectionGene> enabledConnections = connectionGenes.stream().filter(c -> c.isEnabled())
				.collect(Collectors.toList());
		if (enabledConnections.isEmpty()) {
			return;
		}

		ConnectionGene oldConnection = enabledConnections.get(random.nextInt(enabledConnections.size()));

		NodeGene newNode = new NodeGene(Type.Hidden, InnovationNrGenerator.getNext());
		nodeGenes.add(newNode);

		oldConnection.disable();
		ConnectionGene connectionIn = new ConnectionGene(oldConnection.getInNodeInnovationNr(),
				newNode.getInnovationNr(), 1, true, InnovationNrGenerator.getNext());
		ConnectionGene connectionOut = new ConnectionGene(newNode.getInnovationNr(),
				oldConnection.getOutNodeInnovationNr(), oldConnection.getWeight(), true,
				InnovationNrGenerator.getNext());

		addConnectionGene(connectionIn);
		addConnectionGene(connectionOut);
	}
	
	private void performDeleteNodeMutation() {
		List<NodeGene> hiddenNodes = nodeGenes.stream().filter(n -> n.getType() == Type.Hidden).collect(Collectors.toList());
		if (hiddenNodes.isEmpty()) {
			return;
		}
		
		NodeGene hiddenNode = hiddenNodes.get(random.nextInt(hiddenNodes.size()));
		
		connectionGenes.removeIf(c -> c.getInNodeInnovationNr() == hiddenNode.getInnovationNr() || c.getOutNodeInnovationNr() == hiddenNode.getInnovationNr());
		nodeGenes.remove(hiddenNode);
	}
	
	private void performDeleteConnectionMutation() {
		if (connectionGenes.isEmpty()) {
			return;
		}
		
		ConnectionGene connection = connectionGenes.get(random.nextInt(connectionGenes.size()));
		
		connectionGenes.remove(connection);
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
	 * A genome fits into a species if its calculated compatibility distance is less
	 * then a given threshold
	 * 
	 * @param genome  the genome to test
	 * @param species the species to test against
	 * @return true if genome fits into species (based on the compatibility
	 *         distance), false otherwise
	 */
	public boolean fitsIntoSpecies(Species species) {
		return calculateCompatibilityDistanceTo(species.getRepresentative()) < Parameters.compatibilityThreshold;
	}

	public void mutate() {
		if (random.nextFloat() < Parameters.weightMutationChance) {
			performWeightMutation();
		}

		if (random.nextFloat() < Parameters.addNodeMutationChance) {
			performAddNodeMutation();
		}
		
		if (random.nextFloat() < Parameters.deleteNodeMutationChance) {
			performDeleteNodeMutation();
		}

		if (random.nextFloat() < Parameters.addConnectionMutationChance) {
			performAddConnectionMutation();
		}
		
		if (random.nextFloat() < Parameters.deleteConnectionMutationChance) {
			performDeleteConnectionMutation();
		}
		
		// TODO: remove connection mutation (remove node if not in and out node connected anymore?)
	}

	

	private void setInputs(double[] inputs) {
		List<NodeGene> inputNodes = getNodeGenes().stream().filter(g -> g.getType() == Type.Input)
				.collect(Collectors.toList());
		if (inputs.length != inputNodes.size()) {
			throw new RuntimeException("Size of inputs does not equal size of input neurons");
		}

		for (int i = 0; i < inputs.length; ++i) {
			inputNodes.get(i).setActivation(inputs[i]);
		}
	}

	public double[] calculate(double[] inputs) {
		setInputs(inputs);

		double[] outputs = new double[numOutNodes];

		List<NodeGene> outputNodes = getNodeGenes().stream().filter(n -> n.getType() == Type.Output)
				.collect(Collectors.toList());
		calculatePreviousLayer(outputNodes);

		for (int i = 0; i < outputNodes.size(); ++i) {
			outputs[i] = outputNodes.get(i).getActivation();
		}

		return outputs;
	}

	private void calculatePreviousLayer(List<NodeGene> layer) {
		List<NodeGene> previousLayer = new ArrayList<NodeGene>();
		for (NodeGene node : layer) {
			getConnectionGenes().stream()
					.filter(c -> node.getInnovationNr() == c.getOutNodeInnovationNr() && c.isEnabled()).forEach(c -> {
						NodeGene inNode = getNodeGenes().getByInnovatioNr(c.getInNodeInnovationNr());
						if (!previousLayer.contains(inNode)) {
							previousLayer.add(inNode);
						}
					});
		}

		if (!previousLayer.isEmpty()) {
			calculatePreviousLayer(previousLayer);
		}

		for (NodeGene node : layer) {
			getConnectionGenes().stream()
					.filter(c -> node.getInnovationNr() == c.getOutNodeInnovationNr() && c.isEnabled()).forEach(c -> {
						NodeGene inNode = getNodeGenes().getByInnovatioNr(c.getInNodeInnovationNr());
						node.addWeightedInputSum(inNode.getActivation() * c.getWeight());
					});
			node.activate();
		}
	}

	public void randomizeWeights() {
		for (ConnectionGene connection : connectionGenes) {
			float rndWeight = Parameters.minWeight + random.nextFloat() * (Parameters.maxWeight - Parameters.minWeight);
			connection.setWeight(rndWeight);
		}
	}

	public void saveToFile() {
		saveToFile("");
	}
	
	public void saveToFile(String postfix) {
		try (FileOutputStream fout = new FileOutputStream("savednn" + postfix + ".txt", false);
				ObjectOutputStream oos = new ObjectOutputStream(fout);) {
			oos.writeObject(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Genome loadFromFile() {
		Genome genome = null;
		ObjectInputStream objectinputstream = null;
		try {
		    FileInputStream streamIn = new FileInputStream("savednn.txt");
		    objectinputstream = new ObjectInputStream(streamIn);
		    genome = (Genome) objectinputstream.readObject();
		    objectinputstream .close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return genome;
	}

}
