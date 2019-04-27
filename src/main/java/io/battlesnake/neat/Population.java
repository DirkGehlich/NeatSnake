package io.battlesnake.neat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Population {

	private Random random = new Random();
	private List<Species> species = new ArrayList<Species>();
	private List<Genome> population = new ArrayList<Genome>();
	private List<Genome> nextPopulation = new ArrayList<Genome>();
	private Genome fittestGenome;
	private double highestFitness = 0;
	private int generationNr = 0;

	public Population(Genome initialGenome) {
		for (int i = 0; i < Parameters.populationSize; ++i) {
			population.add(initialGenome.copy());
		}

		fittestGenome = initialGenome;
	}

	public Genome crossover(Genome parent1, Genome parent2) {

		Genome child = new Genome(random, parent1.getNumOutNodes());

		// TOOD: Not sure about this part. We need all nodes from both parents as the
		// connections point to them
		// child.getNodeGenes().addAll(parent2.getNodeGenes());
		for (NodeGene node : parent1.getNodeGenes()) {
			child.getNodeGenes().add(node.copy());
		}

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

	public void createNewGeneration() {
		resetAllSpecies();
		cleanupTemps();
		addGenomesToSpecies();
		removeEmptySpecies();
		setAdjustedFitnessToGenomes();
		// TODO: remove weakest genomes from species
		addBestGenomesOfEachSpeciesToNewGeneration();
		breedPopulation();
		++generationNr;
	}

	private void cleanupTemps() {
		nextPopulation = new ArrayList<Genome>();
		highestFitness = 0;
		fittestGenome = null;
	}

	private void removeEmptySpecies() {
		// remove empty species
		Iterator<Species> i = this.species.iterator();
		while (i.hasNext()) {
			Species species = i.next();
			if (species.size() == 0) {
				i.remove();
			}
		}
	}

	/**
	 * Reseting a species will select a random Genome from previous generation as a
	 * representative of this species After that, all genomes will be removed from
	 * the species
	 */
	private void resetAllSpecies() {

		for (Species species : this.species) {
			species.reset(random);
		}
	}

	/**
	 * For each genome calculate compatibility distance to representative of species
	 * of previous generation If the compatibility distance is less than a
	 * compatibility distance threshold, add it to that species Otherwise create a
	 * new species
	 */
	private void addGenomesToSpecies() {
		for (Genome genome : population) {

			boolean speciesFound = false;
			for (Species species : this.species) {
				if (genome.fitsIntoSpecies(species)) {
					species.add(genome);
					speciesFound = true;
					break;
				}
			}

			if (!speciesFound) {
				// TODO: improvement: If too many species (more than 10?), increase
				// compatibility threshold to create less new species
				Species s = new Species(genome);
				this.species.add(s);
			}
		}
	}

	/**
	 * An adjusted fitness is the fitness of a genome divided by the number of
	 * genomes within its species
	 */
	private void setAdjustedFitnessToGenomes() {
		for (Species species : this.species) {
			for (Genome genome : species) {
				double fitness = genome.getFitness();
				double adjustedFitness = fitness / species.size();
				genome.setFitness(adjustedFitness);
				species.addAdjustedFitness(adjustedFitness);

				if (fitness >= highestFitness) {
					highestFitness = fitness;
					fittestGenome = genome;
				}
			}
		}

	}

	/**
	 * The best genome of each species are added to the new generation of genomes
	 */
	private void addBestGenomesOfEachSpeciesToNewGeneration() {
		for (Species species : this.species) {
			if (species.size() >= Parameters.minNumberGenomesForBest) {
				nextPopulation.add(species.getBestGenome());
			}
		}
	}

	private void breedPopulation() {

		while (nextPopulation.size() < population.size()) {
			Species species = selectRandomSpeciesBasedOnAdjustedFitness();

			Genome child;
			Genome parent1 = species.selectRandomGenomeBasedOnAdjustedFitness(random);

			if (random.nextFloat() < Parameters.noCrossoverChange) {
				child = parent1.copy();
			} else {
				Genome parent2 = species.selectRandomGenomeBasedOnAdjustedFitness(random);
				child = crossover(parent1, parent2);
			}

			child.mutate();

			nextPopulation.add(child);
		}

		population = nextPopulation;
	}

	private Species selectRandomSpeciesBasedOnAdjustedFitness() {
		double adjustedFitnessSum = getAdjustedSpeciesFitnessSum();

		double rnd = random.nextDouble() * adjustedFitnessSum;
		double runningAdjustedFitnessSum = 0;

		for (Species species : this.species) {
			runningAdjustedFitnessSum += species.getAdjustedFitness();
			if (runningAdjustedFitnessSum > rnd) {
				return species;
			}
		}

		throw new RuntimeException("Error in selecting random species");
	}

	private double getAdjustedSpeciesFitnessSum() {
		double adjustedFitnessSum = 0;

		for (Species species : this.species) {
			adjustedFitnessSum += species.getAdjustedFitness();
		}

		return adjustedFitnessSum;
	}

	public Genome getFittestGenome() {
		return fittestGenome;
	}

	public double getHighestFitness() {
		return highestFitness;
	}

	public int getGenerationNr() {
		return generationNr;
	}

	public List<Genome> getPopulation() {
		return population;
	}

}
