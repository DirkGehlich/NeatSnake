package io.battlesnake.neat;

import java.util.Random;

import io.battlesnake.neat.NodeGene.Type;

public class NeatAlgorithm {

	private Random random;
	private Population population;

	public NeatAlgorithm(int numInNodes, int numOutNodes) {
		random = new Random();

		Genome genome = new Genome(random);

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

		genome.getNodeGenes().stream().filter(nIn -> nIn.getType() != Type.Output).forEach(nIn -> {
			genome.getNodeGenes().stream().filter(nOut -> nOut.getType() == Type.Output).forEach(nOut -> {
				float randomWeight = Parameters.minWeight + random.nextFloat() * (Parameters.maxWeight - Parameters.minWeight);
				ConnectionGene connection = new ConnectionGene(nIn.getInnovationNr(), nOut.getInnovationNr(),
						randomWeight, true, InnovationNrGenerator.getNext());
				genome.addConnectionGene(connection);
			});
		});

		population = new Population(genome);
		population.randomizeAllWeights();
		population.mutate();
	}
		
	public NeatAlgorithm(Genome initialGenome, boolean mutateInitially) {
		random = new Random();

		population = new Population(initialGenome);
		if (mutateInitially) {
			population.mutate();
		}
	}

	public double[] calculateFittest(double[] inputs) {
		Genome fittestGenome = population.getFittestGenome();
		return fittestGenome.calculate(inputs);
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
