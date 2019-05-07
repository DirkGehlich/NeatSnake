package io.battlesnake.neat;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import io.battlesnake.neat.NodeGene.Type;

class PopulationTest {

	@Test
	public void crossover_disabledConnections() {
		Parameters.populationSize = 1;
		
		Genome genome1 = new Genome(new Random());
		NodeGene g1i1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene g1o1 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		genome1.addNodeGene(g1i1);
		genome1.addNodeGene(g1o1);
		
		ConnectionGene g1c1 = new ConnectionGene(g1i1.getInnovationNr(), g1o1.getInnovationNr(), 0.3f, true, InnovationNrGenerator.getNext());
		genome1.addConnectionGene(g1c1);
		genome1.setFitness(1);
		
		Genome genome2 = genome1.copy();
		genome2.performAddNodeMutation();
		genome2.setFitness(10);
		
		Population pop = new Population(genome1, false);
		pop.getPopulation().add(genome2);
		
		Genome child = pop.crossover(genome2, genome1);
		assertEquals(3, child.getConnectionGenes().size());		
	}
}
