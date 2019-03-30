package NeatSnake.World;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

public class Population {
	
	double MUTATIONRATE = 0.15;
	private final int BOARDSIZE;
	private final int POPULATIONSIZE;
	List<GoodSnake> snakes = new ArrayList<GoodSnake>();
	private int generationNo = 1;
	Random random = new Random();
	GoodSnake bestSnake = null;
	GoodSnake evalBestSnake = null;
	private int globalBestFitness = 0;
	private int globalMostMoves = 0;
	private int globalBiggestLength = 0;
	private boolean training = true;
	private int localMostMoves = 0;
	private int localBiggestLength = 0;
	
	private List<Integer> lifetimeHistory = new ArrayList<Integer>();
	private List<Integer> tailLengthHistory = new ArrayList<Integer>();
	
	public Population(int populationSize, int boardSize) {
		
		this.BOARDSIZE = boardSize;
		this.POPULATIONSIZE = populationSize;
		for (int i=0; i<populationSize; ++i) {
			List<Point> tail = new ArrayList<Point>();
			tail.add(new Point(5,4));

			GoodSnake snake = new GoodSnake(BOARDSIZE, new Point(5,5), tail);
			snake.generateFood();
			snakes.add(snake);
		}
	}
	
	public Population(String fileName, int populationSize, int boardSize) {
		
		MultiLayerPerceptron brain = (MultiLayerPerceptron) NeuralNetwork.createFromFile(fileName);
		this.BOARDSIZE = boardSize;
		this.POPULATIONSIZE = populationSize;
		for (int i=0; i<populationSize; ++i) {
			List<Point> tail = new ArrayList<Point>();
			tail.add(new Point(5,4));
			GoodSnake snake = new GoodSnake(BOARDSIZE, new Point(5,5), tail, brain);
			snake.generateFood();
			snakes.add(snake);
		}
	}
	
	public boolean isPopulationDead() {
	
		if (snakes.size() <= 0)
			return true;
		
		for (GoodSnake snake : snakes) {
			if (!snake.isDead()) {
				return false;
			}
		}			
		
		return true;
	}
	
	public void moveSnakes(List<Point> enemySnakesCoords) {
		for (GoodSnake snake : snakes) {
			if (!snake.isDead()) {
				snake.think(BOARDSIZE, enemySnakesCoords);
				snake.move();
				if (isDead(snake, enemySnakesCoords))
					snake.kill();
			}
		}
	}

	public List<GoodSnake> getSnakes() {
		return snakes;
	}
	
	private boolean isDead(Snake snake, List<Point> enemySnakesCoords) {
		
		// Hit the wall
		if (snake.head.x >= BOARDSIZE || snake.head.x < 0 ||
			snake.head.y >= BOARDSIZE || snake.head.y < 0) {
			return true;
		}
			
		// Hit enemy snake
		for (Point enemySnakeCoord : enemySnakesCoords) {
			if (snake.head.equals(enemySnakeCoord)) {
				return true;
			}
		}
		
		// Hit itself
		for (Point ownTail : snake.tail) {
			if (snake.head.equals(ownTail)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void createNewGeneration() {
		// Select two parents based on fitness
		// crossover the two parents into a new child
		// mutate that childs brain
		
		calculateFitness();
		
		List<GoodSnake> newGeneration = new ArrayList<GoodSnake>();
				
		setBestSnake();
		
		
		int bestFitness = bestSnake.getFitness();
		if (bestFitness < 10000) 
			MUTATIONRATE = 0.2;
		if (bestFitness > 20000) 
			MUTATIONRATE = 0.1;
		if (bestFitness > 50000) 
			MUTATIONRATE = 0.05;
		if (bestFitness > 100000) 
			MUTATIONRATE = 0.02;
		if (bestFitness > 1000000) 
			MUTATIONRATE = 0.01;
				
		GoodSnake bestSnake = this.bestSnake.copy();
		bestSnake.generateFood();
		newGeneration.add(bestSnake);
		
		for (int i=1; i<POPULATIONSIZE; ++i) {
			
			GoodSnake parent1 = selectParent();
			GoodSnake parent2 = selectParent();
			GoodSnake child = crossover(parent1, parent2);
			child.mutate(MUTATIONRATE);
			child.generateFood();
			newGeneration.add(child);
		}
					
		snakes.clear();
		snakes.addAll(newGeneration);	
		
		generationNo++;

	}

	public GoodSnake getDemonstrationSnake() {
		return evalBestSnake;
	}

	public GoodSnake getBestSnake() {
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
		GoodSnake bestSnake = null;
		localBiggestLength = 0;
		localMostMoves = 0;
		
		for (GoodSnake snake : snakes) {
			if (snake.getFitness() > fitness) {
				fitness = snake.getFitness();
				bestSnake = snake;
			}
			
			if (snake.getScore() > localMostMoves)
				localMostMoves = snake.getScore();
			
			if (snake.tail.size() + 1 > localBiggestLength)
				localBiggestLength = snake.tail.size() + 1;
		}
		
		if (bestSnake.getScore() < 100) {
			@SuppressWarnings("unused")
			boolean foobar = true;
		}
		
		// TODO: move to better location
		lifetimeHistory.add(bestSnake.getScore());
		tailLengthHistory.add(bestSnake.tail.size() + 1);
		
		this.bestSnake = bestSnake;
		this.evalBestSnake = bestSnake.copy();
		
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
		
		for (GoodSnake snake : snakes) {
			snake.calculateFitness();
		}
	}
	
	private GoodSnake selectParent() {
		
		int fitnessSum = 0;
		
		for (GoodSnake snake : snakes) {
			fitnessSum += snake.getFitness();
		}
		
		int rndFitness = random.nextInt(fitnessSum);
		
		int runningSum = 0;
		for (GoodSnake snake : snakes) {
			runningSum += snake.getFitness();
			if (runningSum >= rndFitness) {
				
				return snake;
			}
		}
		
		return null;
	}
	
	private GoodSnake crossover(GoodSnake parent1, GoodSnake parent2) {
		
		GoodSnake child = parent1.copy();
		
		Double[] weightsParent1 = parent1.brain.getWeights();
		Double[] weightsParent2 = parent2.brain.getWeights();
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
		
		child.brain.setWeights(weightsChild);
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
