package io.battlesnake.neat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NeatAlgorithmTest {

	@Test
	void testXOR() {
		NeatAlgorithm neat = new NeatAlgorithm(2, 1);
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
		
		Genome fittestGenome = population.getFittestGenome();
		
		double[] outputs = fittestGenome.calculate(new double[] {0,0});
		assertEquals(1,  outputs.length);
		System.out.println("0,0 --> " + outputs[0]);
		assertEquals(0, outputs[0], 0.01f);
		
		outputs = fittestGenome.calculate(new double[] {0,1});
		assertEquals(1,  outputs.length);
		System.out.println("0,1 --> " + outputs[0]);
		assertEquals(1, outputs[0], 0.01f);

		outputs = fittestGenome.calculate(new double[] {1,0});
		assertEquals(1,  outputs.length);
		System.out.println("1,0 --> " + outputs[0]);
		assertEquals(1, outputs[0], 0.01f);

		outputs = fittestGenome.calculate(new double[] {1,1});
		assertEquals(1,  outputs.length);
		System.out.println("1,1 --> " + outputs[0]);
		assertEquals(0, outputs[0], 0.01f);
	}

}
