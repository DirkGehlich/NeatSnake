package io.battlesnake.neat2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import io.battlesnake.neat.NodeGene.Type;

class NeatAlgorithmTest {

	@Test
	void testXOR() {
		NeatAlgorithm neat = new NeatAlgorithm(2, 1);
		testXOR(neat);
	}

	private void testXOR(NeatAlgorithm neat) {
		Population population = neat.getPopulation();
		boolean done = false;

		for (int i = 0; i < 1000 && !done; ++i) {
			for (Player player : population.pop) {

				double[] outputs = player.brain.feedForward(new double[] { 0, 0 });
				assertEquals(1, outputs.length);
				double err00 = Math.abs(0 - outputs[0]);

				outputs = player.brain.feedForward(new double[] { 1, 0 });
				assertEquals(1, outputs.length);
				double err10 = Math.abs(1 - outputs[0]);

				outputs = player.brain.feedForward(new double[] { 0, 1 });
				assertEquals(1, outputs.length);
				double err01 = Math.abs(1 - outputs[0]);

				outputs = player.brain.feedForward(new double[] { 1, 1 });
				assertEquals(1, outputs.length);
				double err11 = Math.abs(0 - outputs[0]);

				double err = err00 + err01 + err10 + err11;

				player.fitness = Math.pow((4 - err), 2);
				player.unadjustedFitness = player.fitness;

				if (player.unadjustedFitness > 15.99) {
					@SuppressWarnings("unused")
					int foo = 42;
				}
			}

			neat.getPopulation().naturalSelection();

			Player bestPlayer = population.bestPlayer;
			System.out.println(
					String.format("Generation: %d\tBest Fitness: %f", neat.getPopulation().gen, bestPlayer.unadjustedFitness));

			if (bestPlayer.unadjustedFitness > 15.99) {
				break;
			}
		}

		double[] outputs = population.bestPlayer.brain.feedForward(new double[] { 0, 0 });
		assertEquals(1, outputs.length);
		System.out.println("0,0 --> " + outputs[0]);
		assertEquals(0, outputs[0], 0.05f);

		outputs = population.bestPlayer.brain.feedForward(new double[] { 0, 1 });
		assertEquals(1, outputs.length);
		System.out.println("0,1 --> " + outputs[0]);
		assertEquals(1, outputs[0], 0.05f);

		outputs = population.bestPlayer.brain.feedForward(new double[] { 1, 0 });
		assertEquals(1, outputs.length);
		System.out.println("1,0 --> " + outputs[0]);
		assertEquals(1, outputs[0], 0.05f);

		outputs = population.bestPlayer.brain.feedForward(new double[] { 1, 1 });
		assertEquals(1, outputs.length);
		System.out.println("1,1 --> " + outputs[0]);
		assertEquals(0, outputs[0], 0.05f);

		population.bestPlayer.brain.printGenome();
	}

}
