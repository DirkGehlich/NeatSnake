package io.battlesnake.training;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;

import io.battlesnake.world.Board;
import io.battlesnake.world.Field;

public class Population {
	

	final int POPULATIONSIZE = 500;
	double MUTATIONRATE = 0.15;
	private final int BOARD_SIZE_X;
	private final int BOARD_SIZE_Y;
	List<TrainingsSnake> snakes = new ArrayList<TrainingsSnake>();
	private int generationNo = 1;
	Random random = new Random();
	TrainingsSnake bestSnake = null;
	private int globalBestFitness = 0;
	private int globalMostMoves = 0;
	private int globalBiggestLength = 0;
	private boolean training = true;
	private int localMostMoves = 0;
	private int localBiggestLength = 0;
	
	private List<Integer> lifetimeHistory = new ArrayList<Integer>();
	private List<Integer> tailLengthHistory = new ArrayList<Integer>();
	
	public Population(int boardSizeX, int boardSizeY) {
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;
		
		for (int i=0; i<POPULATIONSIZE; ++i) {			
			TrainingsSnake snake = new TrainingsSnake(BOARD_SIZE_X, BOARD_SIZE_Y, createInitialBody());
			snake.generateFood();
			snakes.add(snake);
		}
	}
	
	public Population(int boardSizeX, int boardSizeY, String fileName) {
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;
		
		MultiLayerPerceptron brain = (MultiLayerPerceptron) NeuralNetwork.createFromFile(fileName);
		for (int i=0; i<POPULATIONSIZE; ++i) {
			TrainingsSnake snake = new TrainingsSnake(BOARD_SIZE_X, BOARD_SIZE_Y, createInitialBody(), brain);
			snake.generateFood();
			snakes.add(snake);
		}
	}
	
	private LinkedList<Field> createInitialBody() {
		LinkedList<Field> body = new LinkedList<Field>();
		body.add(new Field(5,4));
		body.add(new Field(5,5));
		body.add(new Field(5,6));

		return body;
	}
	
	public boolean isPopulationDead() {
	
		if (snakes.size() <= 0)
			return true;
		
		for (TrainingsSnake snake : snakes) {
			if (!snake.isDead()) {
				return false;
			}
		}			
		
		return true;
	}
	
	public void moveSnakes(List<Field> enemySnakesCoords) {
		
		Board board = new Board(BOARD_SIZE_X, BOARD_SIZE_Y, enemySnakesCoords);
		
		for (TrainingsSnake snake : snakes) {
			if (!snake.isDead()) {
				snake.updateEnvironment(enemySnakesCoords);
				
				Field direction = snake.evaluateNextMove(board);
				snake.moveIn(direction);
				snake.checkIfAlive();
			}
		}
	}

	public List<TrainingsSnake> getSnakes() {
		return snakes;
	}
	
	public void createNewGeneration() {
		// Select two parents based on fitness
		// crossover the two parents into a new child
		// mutate that childs brain
		
		calculateFitness();
		
		List<TrainingsSnake> newGeneration = new ArrayList<TrainingsSnake>();
				
		setBestSnake();
		
		
		int bestFitness = bestSnake.getFitness();
		if (bestFitness < 2500) 
			MUTATIONRATE = 0.2;
		if (bestFitness > 10000) 
			MUTATIONRATE = 0.1;
		if (bestFitness > 50000) 
			MUTATIONRATE = 0.05;
		if (bestFitness > 100000) 
			MUTATIONRATE = 0.02;
		if (bestFitness > 200000) 
			MUTATIONRATE = 0.01;
				
		TrainingsSnake bestSnake = this.bestSnake.copy();
		bestSnake.generateFood();
		newGeneration.add(bestSnake);
		
		for (int i=1; i<POPULATIONSIZE; ++i) {
			
			TrainingsSnake parent1 = selectParent();
			TrainingsSnake parent2 = selectParent();
			TrainingsSnake child = crossover(parent1, parent2);
			child.mutate(MUTATIONRATE);
			child.generateFood();
			newGeneration.add(child);
		}
					
		snakes.clear();
		snakes.addAll(newGeneration);	
		
		generationNo++;

	}

	public TrainingsSnake getBestSnake() {
		return bestSnake;
	}

	public int getGlobalBestFitness() {
		return globalBestFitness;
	}

	public int getGlobalMostMoves() {
		return globalMostMoves;
	}

	public int getGenerationNo() {
		return generationNo;
	}

	public int getGlobalBiggestLength() {
		return globalBiggestLength;
	}

	private void setBestSnake() {
		
		double fitness = 0;
		TrainingsSnake bestSnake = null;
		localBiggestLength = 0;
		localMostMoves = 0;
		
		for (TrainingsSnake snake : snakes) {
			if (snake.getFitness() > fitness) {
				fitness = snake.getFitness();
				bestSnake = snake;
			}
			
			if (snake.getLifetime() > localMostMoves)
				localMostMoves = snake.getLifetime();
			
			if (snake.getBody().size() > localBiggestLength)
				localBiggestLength = snake.getBody().size();
		}
				
		// TODO: move to better location
		lifetimeHistory.add(bestSnake.getLifetime());
		tailLengthHistory.add(bestSnake.getBody().size());
		
		this.bestSnake = bestSnake;
		
		if (bestSnake.getFitness() > globalBestFitness) {
			globalBestFitness = bestSnake.getFitness();
		}
		
		if (localMostMoves > globalMostMoves) {
			globalMostMoves = localMostMoves;
		}

		if (localBiggestLength > globalBiggestLength) {
			globalBiggestLength = localBiggestLength;
		}
	}
	
	private void calculateFitness() {
		
		for (TrainingsSnake snake : snakes) {
			snake.calculateFitness();
		}
	}
	
	private TrainingsSnake selectParent() {
		
		int fitnessSum = 0;
		
		for (TrainingsSnake snake : snakes) {
			fitnessSum += snake.getFitness();
		}
		
		int rndFitness = random.nextInt(fitnessSum);
		
		int runningSum = 0;
		for (TrainingsSnake snake : snakes) {
			runningSum += snake.getFitness();
			if (runningSum >= rndFitness) {
				
				return snake;
			}
		}
		
		return null;
	}
	
	private TrainingsSnake crossover(TrainingsSnake parent1, TrainingsSnake parent2) {
		
		TrainingsSnake child = parent1.copy();
		
		Double[] weightsParent1 = parent1.getBrain().getWeights();
		Double[] weightsParent2 = parent2.getBrain().getWeights();
		double[] weightsChild = new double[weightsParent1.length];
		
		int maxIdxParent1 = random.nextInt(weightsParent1.length);
		for (int i=0; i<=maxIdxParent1; ++i) {
			weightsChild[i] = weightsParent1[i];
		}
		
		if (maxIdxParent1 < weightsParent2.length -2) {
			for (int i=maxIdxParent1+1; i<weightsParent2.length; ++i) {
				weightsChild[i] = weightsParent2[i];
			}
		}
		
		child.getBrain().setWeights(weightsChild);
		return child;		
	}

	public boolean isTraining() {
		return training;
	}

	public void setTraining(boolean training) {
		this.training = training;
	}

	public List<Integer> getLifetimeHistory() {
		return lifetimeHistory;
	}

	public List<Integer> getSnakeLengthHistory() {
		return tailLengthHistory;
	}

	public int getLocalMostMoves() {
		return localMostMoves;
	}

	public int getLocalBiggestLength() {
		return localBiggestLength;
	}
	
	

}
