package io.battlesnake.neat;

import java.util.Random;

public class Population {

	private Random random = new Random();

	public Genome crossover(Genome parent1, Genome parent2) {
		Genome child = new Genome(random);

		// TOOD: Not sure about this part. We need all nodes from both parents as the
		// connections point to them
		child.getNodeGenes().addAll(parent2.getNodeGenes());
		child.getNodeGenes().addAll(parent1.getNodeGenes());

		for (ConnectionGene parent1Connection : parent1.getConnectionGenes()) {
			ConnectionGene parent2Connection = parent2.getConnectionGenes().stream()
					.filter(c -> c.getInNodeInnovationNr() == parent1Connection.getInNodeInnovationNr()).findFirst()
					.orElse(null);
			
			if (parent2Connection != null) {
				child.addConnectionGene(random.nextBoolean() ? parent1Connection.copy() : parent2Connection.copy());
			} else {
				child.addConnectionGene(parent1Connection.copy()); // Always take from more fit parent
			}
		}

		return child;
	}
	
	public float calculateCompatibilityDistance(Genome genome1, Genome genome2) {
		
		int cntExcessGenes = 0;
		int cntDisjointGenes = 0;
		int cntGenesLargerGenome = 0;
		int cntMatchingGenes = 0;
		float weightDiffSum = 0;
		
		ConnectionGenes genome1Connections = genome1.getConnectionGenes();
		ConnectionGenes genome2Connections = genome2.getConnectionGenes();
		
		int cntGenome1Connections = genome1Connections.size();
		int cntGenome2Connections = genome2Connections.size();
		cntGenesLargerGenome = Math.max(cntGenome1Connections, cntGenome2Connections);
		
		for (int i=0; i<cntGenome1Connections; ++i) {
			if (!genome2Connections.contains(genome1Connections.get(i))) {
				if (i < cntGenome2Connections) {
					cntDisjointGenes++;	
				} else {
					cntExcessGenes++;
				}
			} else {
				cntMatchingGenes++;
				ConnectionGene connection1 = genome1Connections.get(i);
				weightDiffSum += Math.abs(connection1.getWeight() - genome2Connections.getConnectionGene(connection1.getInnovationNumber()).getWeight());
			}
		}
		
		for (int i=0; i<cntGenome2Connections; ++i) {
			if (!genome1Connections.contains(genome2Connections.get(i))) {
				if (i < cntGenome1Connections) {
					cntDisjointGenes++;
				} else {
					cntExcessGenes++;
				}
			}
		}
		
		int n = cntGenesLargerGenome > 20 ? cntGenesLargerGenome : 1;
		float avgWeightDifference = (float)weightDiffSum / cntMatchingGenes;
		
		float delta = (float)(Parameters.C1 * cntExcessGenes / n) + (float)(Parameters.C2 * cntDisjointGenes / n) + Parameters.C3 * avgWeightDifference;
		return delta;
	}
}
