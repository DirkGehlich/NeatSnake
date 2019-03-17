package NeatSnake.World;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.stop.MaxErrorStop;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.random.WeightsRandomizer;

public class GoodSnake extends Snake {

	private final int MAXHEALTH = 90;
	private final int BOARDSIZE;
	private Point food = new Point();
	private Point startingPosition = new Point();
	private NeuralNetwork brain = new MultiLayerPerceptron(24, 8, 4);
	private Point direction = new Point();
	
	private boolean isDead = false;
	private int health = MAXHEALTH;
	
	private double fitness = 0;
	private int score = 0;
	
	public GoodSnake(int boardSize) {
		this.BOARDSIZE = boardSize;
		brain.randomizeWeights(0.0, 1.0);
		
		generateNewFood();
	}
	
	public GoodSnake(int boardSize, Point startingPosition, NeuralNetwork brain) {
		this.BOARDSIZE = boardSize;
		this.startingPosition = startingPosition;
		this.brain = brain;
		
		generateNewFood();
	}	
	
	public GoodSnake(int boardSize, Point startingPosition) {
		this.BOARDSIZE = boardSize;
		this.startingPosition.x = startingPosition.x;
		this.startingPosition.y = startingPosition.y;
		head.x = startingPosition.x;
		head.y = startingPosition.y;
		brain.randomizeWeights();
		
		generateNewFood();
	}
	
	public GoodSnake copy() {
		GoodSnake snake = new GoodSnake(BOARDSIZE, startingPosition, brain);
		
		return snake;
	}
	
	private void generateNewFood() {

		Random random = new Random();
		food.x = random.nextInt(BOARDSIZE);
		food.y = random.nextInt(BOARDSIZE);
	}

	public void setFood(Point food) {
		this.food = food;
	}

	public Point getFood() {
		return food;
	}
	
	private boolean isEatingFood() {
		
		Point nextTile = new Point(head.x + direction.x, head.y + direction.y);
		if (nextTile.equals(food)) {
			return true;
		}
		
		return false;
	}	
	

	private double[] getInfoFromDirection(Point direction, int boardSize, List<Point> snakes, List<Point> food) {
	
		double[] info = new double[3];
		
		int distance = 0;
		Point currentPos = (Point) head.clone();
		boolean snakeFound = false;
		boolean foodFound = false;
		
		do {	
				currentPos.x += direction.x;
				currentPos.y += direction.y;
				++distance;
				
				if (!snakeFound && snakes.contains(currentPos)) {
					snakeFound = true;
					info[0] = 1.0/distance;
				}
				
				if (!foodFound && food.contains(currentPos)) {
					foodFound = true;
					info[1] = 1;
				}
		} while (currentPos.x < boardSize && currentPos.y < boardSize &&
		         currentPos.x >= 0 && currentPos.y >= 0);
				
		
		info[2] = 1.0/distance;
		
		return info;
		
	}
	
	public void think(int boardSize, List<Point> snakes, List<Point> food) {
		
		double[] inputs = new double[24];
		// Look in each of the 8 direction and find the nearest snake, food and wall
		double[] info = getInfoFromDirection(new Point(1, 0), boardSize, snakes, food);
		inputs[0] = info[0];
		inputs[1] = info[1];
		inputs[2] = info[2];
		
		info = getInfoFromDirection(new Point(1, 1), boardSize, snakes, food);
		inputs[3] = info[0];
		inputs[4] = info[1];
		inputs[5] = info[2];
		
		info = getInfoFromDirection(new Point(0, 1), boardSize, snakes, food);
		inputs[6] = info[0];
		inputs[7] = info[1];
		inputs[8] = info[2];
		
		info = getInfoFromDirection(new Point(-1, 0), boardSize, snakes, food);
		inputs[9] = info[0];
		inputs[10] = info[1];
		inputs[11] = info[2];
		
		info = getInfoFromDirection(new Point(-1, -1), boardSize, snakes, food);
		inputs[12] = info[0];
		inputs[13] = info[1];
		inputs[14] = info[2];
		
		info = getInfoFromDirection(new Point(0, -1), boardSize, snakes, food);
		inputs[15] = info[0];
		inputs[16] = info[1];
		inputs[17] = info[2];
		
		info = getInfoFromDirection(new Point(-1, 1), boardSize, snakes, food);
		inputs[18] = info[0];
		inputs[19] = info[1];
		inputs[20] = info[2];
		
		info = getInfoFromDirection(new Point(1, -1), boardSize, snakes, food);
		inputs[21] = info[0];
		inputs[22] = info[1];
		inputs[23] = info[2];
		
			
		brain.setInput(inputs);
		brain.calculate();
		double[] output = brain.getOutput();
		
		double highestOutput = 0.0;
		
		// go up
		highestOutput = output[0];
		direction.x = 0;
		direction.y = -1;
		
		// go right
		if (output[1] > highestOutput) {
			highestOutput = output[1];
			direction.x = 1;
			direction.y = 0;
		}
		
		// go down
		if (output[2] > highestOutput) {
			highestOutput = output[2];
			direction.x = 0;
			direction.y = 1;
		}
		
		// go left
		if (output[3] > highestOutput) {
			direction.x = -1;
			direction.y = 0;
		}
	}
	
	public void move() {
		--health;
		++score;
		if (health <= 0)
			kill();
		
		if (isEatingFood()) {

			Point addedTail = new Point(head.x, head.y);
			tail.add(addedTail);
			
			health = MAXHEALTH;
			generateNewFood();
		}
		
		move(direction);
	}
	
	public void kill() {
		isDead = true;
	}
	
	public boolean isDead() {
		return isDead;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int getScore() {
		return score;
	}
	
	public void calculateFitness() {
		fitness = Math.floor(score * score  * Math.pow(2, 1+ tail.size()));
	}
	
	public void mutate(double mutationRate) {
		Double[] weights = brain.getWeights();
		
		Random random = new Random();
		
		for (Double weight : weights) {
			double mutateRnd = random.nextDouble();
			if (mutateRnd < mutationRate) {
				weight += random.nextGaussian() / 5.0;
				if (weight > 1.0)
					weight = 1.0;
				if (weight < -1.0)
					weight = -1.0;
			}
		}
	}
}
