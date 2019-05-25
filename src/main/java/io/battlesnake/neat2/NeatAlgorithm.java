package io.battlesnake.neat2;


public class NeatAlgorithm {
	private Population population;

	public NeatAlgorithm(int numInNodes, int numOutNodes) {

		population = new Population(Parameters.populationSize, numInNodes, numOutNodes);
	}
	
	public Population getPopulation() {
		return population;
	}
}
