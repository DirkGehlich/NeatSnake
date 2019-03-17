package NeatSnake.World;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Population {
	
	private final double MUTATIONRATE = 0.1;
	private final int BOARDSIZE;
	private final int POPULATIONSIZE;
	List<GoodSnake> snakes = new ArrayList<GoodSnake>();
	private int generationNo = 1;
	Random random = new Random();
	GoodSnake bestSnake = null;
	private int globalBestFitness = 0;
	private int globalMostMoves = 0;
	private int globalBiggestLength = 0;
	private boolean training = true;
	
	public Population(int populationSize, int boardSize) {
		
		this.BOARDSIZE = boardSize;
		this.POPULATIONSIZE = populationSize;
		for (int i=0; i<populationSize; ++i) {
			snakes.add(new GoodSnake(BOARDSIZE, new Point(1,1)));
		}
	}
	
	public void moveSnakes(List<Point> enemySnakesCoords) {
		
		if (!training) {
			if (!bestSnake.isDead()) {
				bestSnake.move();
				if (isDead(bestSnake, enemySnakesCoords))
					bestSnake.kill();
			}
		}
		else {
			boolean allSnakesDead = true;
			for (GoodSnake snake : snakes) {
				if (!snake.isDead()) {
					allSnakesDead = false;
					snake.think(BOARDSIZE, enemySnakesCoords);
					snake.move();
					if (isDead(snake, enemySnakesCoords))
						snake.kill();
				}
			}			
			
			if (allSnakesDead) {			
				createNewGeneration();
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
		
		GoodSnake bestSnake = this.bestSnake.copy();
		newGeneration.add(bestSnake);
		
		for (int i=1; i<POPULATIONSIZE; ++i) {
			
			GoodSnake parent1 = selectParent().copy();
			GoodSnake parent2 = selectParent().copy();
			GoodSnake child = crossover(parent1, parent2);
			child.mutate(MUTATIONRATE);
			newGeneration.add(child);
		}
					
		snakes.clear();
		snakes.addAll(newGeneration);	
		
		generationNo++;
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
		
		for (GoodSnake snake : snakes) {
			if (snake.getFitness() > fitness) {
				fitness = snake.getFitness();
				bestSnake = snake;
			}
		}
		
		this.bestSnake = bestSnake;
		
		if (bestSnake.getFitness() > globalBestFitness) {
			globalBestFitness = bestSnake.getFitness();
		}
		
		if (bestSnake.getScore() > globalMostMoves) {
			globalMostMoves = bestSnake.getScore();
		}

		if (bestSnake.tail.size() + 1 > globalBiggestLength) {
			globalBiggestLength = bestSnake.tail.size() + 1;
		}
	}
	
	private void calculateFitness() {
		
		for (GoodSnake snake : snakes) {
			snake.calculateFitness();
		}
	}
	
	private GoodSnake selectParent() {
		
		// TODO: int or double? 
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

}
