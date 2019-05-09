package io.battlesnake.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Species extends ArrayList<Genome> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Genome representative;
	private double adjustedFitness = 0;
	private double avgAdjustedFitness = 0;
	private int stagnationCounter = 0;
	
	public Species(Genome representative) {
		this.representative = representative;
		this.add(representative);
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
		this.adjustedFitness = 0;
		this.clear();
	}

	public void addAdjustedFitness(double adjustedFitness) {
		this.adjustedFitness += adjustedFitness;
	}

	public Genome getBestGenome() {
		if (isEmpty()) {
			throw new RuntimeException("Cannot get fittest genome as the species is empty");
		}
		
		return get(0);
	}
	
	public void sortSpeciesByFitness() {
		Collections.sort(this, (a, b) -> a.getAdjustedFitness() < b.getAdjustedFitness() ? 1 : a.getAdjustedFitness() == b.getAdjustedFitness() ? 0 : -1);
	}
	
	public Genome selectRandomGenomeBasedOnAdjustedFitness(Random random) {
		double adjustedFitnessSum = getAdjustedSpeciesFitnessSum();
		
		double rnd = random.nextDouble() * adjustedFitnessSum;
		double runningAdjustedFitnessSum = 0;
		
		for (Genome genome : this) {
			runningAdjustedFitnessSum += genome.getAdjustedFitness();
			if (runningAdjustedFitnessSum > rnd) {
				return genome;
			}
		}
		
		throw new RuntimeException("Error in selecting random genome");
	}
	
	private double getAdjustedSpeciesFitnessSum() {
		double adjustedFitnessSum = 0;
		
		for (Genome genome : this) {
			adjustedFitnessSum += genome.getAdjustedFitness();
		}
		
		return adjustedFitnessSum;
	}
	
	public void increaseStagnationCounter() {
		++stagnationCounter;
	}
	
	public boolean isStagnated() {
		return stagnationCounter >= Parameters.maxStagnationCount; 
	}

	public double getAvgAdjustedFitness() {
		return avgAdjustedFitness;
	}

	public void setAvgAdjustedFitness(double avgAdjustedFitness) {
		this.avgAdjustedFitness = avgAdjustedFitness;
	}
}
