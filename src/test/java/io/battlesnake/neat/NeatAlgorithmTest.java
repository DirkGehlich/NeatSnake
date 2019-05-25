package io.battlesnake.neat;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import io.battlesnake.neat.NodeGene.Type;

class NeatAlgorithmTest {

	@Test
	void testXORWithoutTopologyMutations() {
		Genome genome = new Genome(new Random());
		NodeGene i1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene i2 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene b1 = new NodeGene(Type.Bias, InnovationNrGenerator.getNext());
		
		NodeGene h1 = new NodeGene(Type.Hidden, InnovationNrGenerator.getNext());
		NodeGene h2 = new NodeGene(Type.Hidden, InnovationNrGenerator.getNext());
		
		NodeGene o1 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		
		ConnectionGene ci1h1 = new ConnectionGene(i1.getInnovationNr(), h1.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene ci1h2 = new ConnectionGene(i1.getInnovationNr(), h2.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene ci2h1 = new ConnectionGene(i2.getInnovationNr(), h1.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene ci2h2 = new ConnectionGene(i2.getInnovationNr(), h2.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene cb1h1 = new ConnectionGene(b1.getInnovationNr(), h1.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene cb1h2 = new ConnectionGene(b1.getInnovationNr(), h2.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		
		ConnectionGene ch1o1 = new ConnectionGene(h1.getInnovationNr(), o1.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene ch2o1 = new ConnectionGene(h2.getInnovationNr(), o1.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		ConnectionGene cb1o1 = new ConnectionGene(b1.getInnovationNr(), o1.getInnovationNr(), 0, true, InnovationNrGenerator.getNext());
		
		genome.addNodeGene(i1);
		genome.addNodeGene(i2);
		genome.addNodeGene(b1);
		genome.addNodeGene(h1);
		genome.addNodeGene(h2);
		genome.addNodeGene(o1);
		
		genome.addConnectionGene(ci1h1);
		genome.addConnectionGene(ci1h2);
		genome.addConnectionGene(ci2h1);
		genome.addConnectionGene(ci2h2);
		genome.addConnectionGene(cb1h1);
		genome.addConnectionGene(cb1h2);
		genome.addConnectionGene(ch1o1);
		genome.addConnectionGene(ch2o1);
		genome.addConnectionGene(cb1o1);
		
		Parameters.addConnectionMutationChance = 0;
		Parameters.addNodeMutationChance = 0;
		Parameters.deleteConnectionMutationChance = 0;
		Parameters.deleteNodeMutationChance = 0;
		
		NeatAlgorithm neat = new NeatAlgorithm(genome, true);
		
		testXOR(neat);
	}
	
	@Test
	void testXOR() {
		NeatAlgorithm neat = new NeatAlgorithm(2, 1);
		testXOR(neat);
	}
	
	private void testXOR(NeatAlgorithm neat) {
		Population population = neat.getPopulation();
		boolean done = false;

		for (int i = 0; i < 1000 && !done; ++i) {
			for (Genome genome : population.getPopulation()) {

				double[] outputs = genome.calculate(new double[] { 0, 0 });
				assertEquals(1, outputs.length);
				double err00 = Math.abs(0 - outputs[0]);

				outputs = genome.calculate(new double[] { 1, 0 });
				assertEquals(1, outputs.length);
				double err10 = Math.abs(1 - outputs[0]);

				outputs = genome.calculate(new double[] { 0, 1 });
				assertEquals(1, outputs.length);
				double err01 = Math.abs(1 - outputs[0]);

				outputs = genome.calculate(new double[] { 1, 1 });
				assertEquals(1, outputs.length);
				double err11 = Math.abs(0 - outputs[0]);

				double err = err00 + err01 + err10 + err11;

				double fitness = Math.pow((4-err), 2);
				genome.setFitness(fitness);
				
				if (genome.getFitness() > 15.99) {
					@SuppressWarnings("unused")
					int foo = 42;
				}
			}
			
			Genome fittestGenome = population.getFittestGenome();
			System.out.println(String.format("Generation: %d\tBest Fitness: %f", neat.getGeneratioNr(),
					fittestGenome.getFitness()));
			
			if (fittestGenome.getFitness() > 15.99) {
				break;
			}
			neat.createNewGeneration();

		}
				
		double[] outputs = neat.calculateFittest(new double[] {0,0});
		assertEquals(1,  outputs.length);
		System.out.println("0,0 --> " + outputs[0]);
		assertEquals(0, outputs[0], 0.01f);
		
		outputs = neat.calculateFittest(new double[] {0,1});
		assertEquals(1,  outputs.length);
		System.out.println("0,1 --> " + outputs[0]);
		assertEquals(1, outputs[0], 0.01f);

		outputs = neat.calculateFittest(new double[] {1,0});
		assertEquals(1,  outputs.length);
		System.out.println("1,0 --> " + outputs[0]);
		assertEquals(1, outputs[0], 0.01f);

		outputs = neat.calculateFittest(new double[] {1,1});
		assertEquals(1,  outputs.length);
		System.out.println("1,1 --> " + outputs[0]);
		assertEquals(0, outputs[0], 0.01f);
		
		System.out.println(neat.getPopulation().getFittestGenome().toString());
	}

}
