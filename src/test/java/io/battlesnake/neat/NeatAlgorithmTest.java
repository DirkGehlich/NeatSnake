package io.battlesnake.neat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NeatAlgorithmTest {

	@Test
	void testXOR() {
		NeatAlgorithm neat = new NeatAlgorithm(2, 1);
		Population population = neat.getPopulation();
		boolean done = false;

		for (int i = 0; i < 100 && !done; ++i) {
			for (Genome genome : population.getPopulation()) {

				genome.setInputs(new float[] { 0, 0 });
				double[] outputs = genome.calculate();
				assertEquals(1, outputs.length);
				double err00 = Math.abs(1 - outputs[0]);

				genome.setInputs(new float[] { 1, 0 });
				outputs = genome.calculate();
				assertEquals(1, outputs.length);
				double err10 = Math.abs(0 - outputs[0]);

				genome.setInputs(new float[] { 0, 1 });
				outputs = genome.calculate();
				assertEquals(1, outputs.length);
				double err01 = Math.abs(0 - outputs[0]);

				genome.setInputs(new float[] { 1, 1 });
				outputs = genome.calculate();
				assertEquals(1, outputs.length);
				double err11 = Math.abs(1 - outputs[0]);

				double err = err00 + err01 + err10 + err11;

				genome.setFitness(4 - err);

			}

			Genome fittestGenome = population.getFittestGenome();
			System.out.println(String.format("Generation: %d\tBest Fitness: %f", neat.getGeneratioNr(),
					fittestGenome.getFitness()));

			neat.createNewGeneration();
		}
		
		Genome fittestGenome = population.getFittestGenome();
		fittestGenome.setInputs(new float[] {0,0});
		double[] outputs = fittestGenome.calculate();
		assertEquals(1,  outputs.length);
		double err = Math.abs(1 - outputs[0]);
		assertEquals(0, err, 0.01f);
	}

}
