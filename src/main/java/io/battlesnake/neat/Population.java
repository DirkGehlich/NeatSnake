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
				child.addConnectionGene(random.nextBoolean() ? parent1Connection : parent2Connection);
			} else {
				child.addConnectionGene(parent1Connection); // Always take from more fit parent
			}
		}

		return child;
	}
}
