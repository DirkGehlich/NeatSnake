package io.battlesnake.neat;

import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import io.battlesnake.neat.NodeGene.Type;

public class NeatAlgorithm {

	private Random random;
	private Population population;
	private final int numInNodes;
	private final int numOutNodes;

	public NeatAlgorithm(int numInNodes, int numOutNodes) {

		this.numInNodes = numInNodes;
		this.numOutNodes = numOutNodes;

		random = new Random();

		Genome genome = new Genome(random, numOutNodes);

		for (int i = 0; i < numInNodes; ++i) {
			NodeGene node = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
			genome.addNodeGene(node);
		}

		NodeGene biasNode = new NodeGene(Type.Bias, InnovationNrGenerator.getNext());
		biasNode.setActivation(1.0f);
		genome.addNodeGene(biasNode);

		for (int i = 0; i < numOutNodes; ++i) {
			NodeGene node = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
			genome.addNodeGene(node);
		}

		genome.getNodeGenes().stream().filter(n -> n.getType() != Type.Output).forEach(n -> {
			genome.getNodeGenes().stream().filter(nOut -> nOut.getType() == Type.Output).forEach(nOut -> {
				float randomWeight = random.nextFloat() * 4 - 2;
				ConnectionGene connection = new ConnectionGene(n.getInnovationNr(), nOut.getInnovationNr(),
						randomWeight, true, InnovationNrGenerator.getNext());
				genome.addConnectionGene(connection);
			});
		});

		population = new Population(genome);
	}

	public void setInputsFittest(float[] inputs) {

		population.getFittestGenome().setInputs(inputs);
	}

	public double[] calculateFittest() {
		Genome fittestGenome = population.getFittestGenome();
		return fittestGenome.calculate();
	}

	public void createNewGeneration() {
		population.createNewGeneration();
	}

	public int getGeneratioNr() {
		return population.getGenerationNr();
	}

	public Population getPopulation() {
		return population;
	}

}
