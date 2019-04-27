package io.battlesnake.neat;

import java.util.ArrayList;
import java.util.Random;

public class Species extends ArrayList<Genome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Genome representative;
	private double adjustedFitness = 0;

	public Species(Genome representative) {
		this.representative = representative;
	}

	public Genome getRepresentative() {
		return representative;
	}

	public double getAdjustedFitness() {
		return adjustedFitness;
	}

	public void reset(Random random) {
		int representativeIdx = random.nextInt(size());
		this.representative = this.get(representativeIdx);
		this.clear();
	}

	public void addAdjustedFitness(double adjustedFitness) {
		this.adjustedFitness += adjustedFitness;
	}

	public Genome getBestGenome() {
		Genome bestGenome = null;
		double highestFitness = 0;

		for (Genome genome : this) {
			if (genome.getFitness() > highestFitness) {
				highestFitness = genome.getFitness();
				bestGenome = genome;
			}
		}

		return bestGenome;
	}
	
	public Genome selectRandomGenomeBasedOnAdjustedFitness(Random random) {
		double adjustedFitnessSum = getAdjustedSpeciesFitnessSum();
		
		double rnd = random.nextDouble() * adjustedFitnessSum;
		double runningAdjustedFitnessSum = 0;
		
		for (Genome genome : this) {
			runningAdjustedFitnessSum += genome.getFitness();
			if (runningAdjustedFitnessSum > rnd) {
				return genome;
			}
		}
		
		throw new RuntimeException("Error in selecting random genome");
	}
	
	private double getAdjustedSpeciesFitnessSum() {
		double adjustedFitnessSum = 0;
		
		for (Genome genome : this) {
			adjustedFitnessSum += genome.getFitness();
		}
		
		return adjustedFitnessSum;
	}
}
