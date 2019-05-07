package io.battlesnake.training;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.battlesnake.neat.Genome;
import io.battlesnake.neat.NeatAlgorithm;
import io.battlesnake.world.Board;
import io.battlesnake.world.Field;

public class Population {

	double MUTATIONRATE = 0.15;
	private final int BOARD_SIZE_X;
	private final int BOARD_SIZE_Y;
	List<TrainingsSnake> snakes = new ArrayList<TrainingsSnake>();
	Random random = new Random();
	TrainingsSnake bestSnake = null;
	private long globalBestFitness = 0;
	private int globalMostMoves = 0;
	private int globalBiggestLength = 0;
	private boolean training = true;
	private int localMostMoves = 0;
	private int localBiggestLength = 0;
	private NeatAlgorithm neat;

	private List<Integer> lifetimeHistory = new ArrayList<Integer>();
	private List<Integer> tailLengthHistory = new ArrayList<Integer>();

	public Population(int boardSizeX, int boardSizeY) {
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;

		// TODO: change to relative direction to reduce number of inputs and outputs
		neat = new NeatAlgorithm(17, 3);
		for (Genome genome : neat.getPopulation().getPopulation()) {
			TrainingsSnake snake = new TrainingsSnake(BOARD_SIZE_X, BOARD_SIZE_Y, createInitialBody(), genome);
			snake.generateFood();
			snakes.add(snake);
		}
	}

	public Population(int boardSizeX, int boardSizeY, String fileName) {
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;
		
		Genome brain = Genome.loadFromFile();
		neat = new NeatAlgorithm(brain);
		for (Genome genome : neat.getPopulation().getPopulation()) {
			TrainingsSnake snake = new TrainingsSnake(BOARD_SIZE_X, BOARD_SIZE_Y, createInitialBody(), genome);
			snake.generateFood();
			snakes.add(snake);
		}
	}

	private LinkedList<Field> createInitialBody() {
		LinkedList<Field> body = new LinkedList<Field>();
		body.add(new Field(5, 4));
		body.add(new Field(5, 5));
		body.add(new Field(5, 6));

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

	public void moveSnakes(EnemySnakes enemySnakes) {

		Board board = new Board(BOARD_SIZE_X, BOARD_SIZE_Y, enemySnakes.getBodyFields());

		for (TrainingsSnake snake : snakes) {
			if (!snake.isDead()) {
				snake.updateEnvironment(enemySnakes);

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
		
		calculateFitness();
		setBestSnake();
		neat.createNewGeneration();
		snakes.clear();
		for (Genome genome : neat.getPopulation().getPopulation()) {
			TrainingsSnake snake = new TrainingsSnake(BOARD_SIZE_X, BOARD_SIZE_Y, createInitialBody(), genome);
			snake.generateFood();
			snakes.add(snake);
		}
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
	
	public TrainingsSnake getBestSnake() {
		return bestSnake;
	}

	public long getGlobalBestFitness() {
		return globalBestFitness;
	}

	public int getGlobalMostMoves() {
		return globalMostMoves;
	}

	public int getGenerationNo() {
		return neat.getGeneratioNr();
	}

	public int getGlobalBiggestLength() {
		return globalBiggestLength;
	}

	private void calculateFitness() {

		for (TrainingsSnake snake : snakes) {
			snake.calculateFitness();
		}
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
