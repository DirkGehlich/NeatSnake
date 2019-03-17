package NeatSnake.World;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population {
	
	private final double MUTATIONRATE = 0.1;
	private final int BOARDSIZE;
	private final int POPULATIONSIZE;
	List<GoodSnake> snakes = new ArrayList<GoodSnake>();
	private int generationNo = 1;
	
	public Population(int populationSize, int boardSize) {
		
		this.BOARDSIZE = boardSize;
		this.POPULATIONSIZE = populationSize;
		for (int i=0; i<populationSize; ++i) {
			snakes.add(new GoodSnake(BOARDSIZE, new Point(1,1)));
		}
	}
	
	public void moveSnakes(List<Point> enemySnakesCoords, List<Point> foodCoords) {
		boolean allSnakesDead = true;
		for (GoodSnake snake : snakes) {
			if (!snake.isDead()) {
				allSnakesDead = false;
				snake.think(BOARDSIZE, enemySnakesCoords, foodCoords);
				snake.move();
				if (isDead(snake, enemySnakesCoords))
					snake.kill();
			}
		}			
		
		if (allSnakesDead) {			
			createNewGeneration();
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
				
		newGeneration.add(getBestSnake());
		
		for (int i=1; i<POPULATIONSIZE; ++i) {
			
			GoodSnake parent = selectParent().copy();
			parent.mutate(MUTATIONRATE);
			newGeneration.add(parent);
		}
		
		snakes.clear();
		snakes.addAll(newGeneration);	
		
		generationNo++;
	}
	
	public int getGenerationNo() {
		return generationNo;
	}

	private GoodSnake getBestSnake() {
		
		double fitness = 0;
		GoodSnake bestSnake = null;
		
		for (GoodSnake snake : snakes) {
			if (snake.getFitness() > fitness) {
				fitness = snake.getFitness();
				bestSnake = snake;
			}
		}
		
		return bestSnake;
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
		
		Random random = new Random();
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

}
