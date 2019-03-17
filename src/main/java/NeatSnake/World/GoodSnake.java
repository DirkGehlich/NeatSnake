package NeatSnake.World;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.stop.MaxErrorStop;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.GaussianRandomizer;
import org.neuroph.util.random.WeightsRandomizer;

public class GoodSnake extends Snake {

	private final int MAXFOOD = 4;
	private final int MAXHEALTH = 30;
	private final int BOARDSIZE;
	private List<Point> food = new ArrayList<Point>();
	private Point startingPosition = new Point();
	public NeuralNetwork brain = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 16, 12, 4);
	
	private boolean isDead = false;
	private int health = MAXHEALTH;
	
	private int fitness = 0;
	private int score = 0;

	Random random = new Random();
	
	public GoodSnake(int boardSize) {
		this.BOARDSIZE = boardSize;
		brain.randomizeWeights();
		
		generateFood();
	}
	
	public GoodSnake(int boardSize, Point startingPosition, NeuralNetwork brain) {
		this.BOARDSIZE = boardSize;
		this.startingPosition = startingPosition;
		this.brain = brain;
//		this.brain = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 24, 18, 4);
//		this.brain.setInputNeurons(brain.getInputNeurons());
		
//		Double[] parentWeights = brain.getWeights();
//		double[] weights = new double[brain.getWeights().length];
//		for (int i=0; i<parentWeights.length; ++i) {
//			weights[i] = parentWeights[i];
//		}
//		
//		this.brain.setWeights(weights);
		
		generateFood();
	}	
	
	public GoodSnake(int boardSize, Point startingPosition) {
		this.BOARDSIZE = boardSize;
		this.startingPosition.x = startingPosition.x;
		this.startingPosition.y = startingPosition.y;
		head.x = startingPosition.x;
		head.y = startingPosition.y;
		brain.randomizeWeights();
		
		generateFood();
	}
	
	public GoodSnake copy() {
		GoodSnake snake = new GoodSnake(BOARDSIZE, startingPosition, brain);
		
		return snake;
	}
	
	private void generateFood() {

		food.clear();
		for (int i=0; i<MAXFOOD; ++i) {
			Point newFood = new Point(random.nextInt(BOARDSIZE), random.nextInt(BOARDSIZE));
			food.add(newFood);
		}
	}
	
	private void generateNewFood(Point eatenFood) {

		food.remove(eatenFood);
		Point newFood = new Point(random.nextInt(BOARDSIZE), random.nextInt(BOARDSIZE));
		food.add(newFood);
	}

	public List<Point> getFood() {
		return food;
	}
	
	private boolean isEatingFood() {
				
		Point nextTile = new Point(head.x + direction.x, head.y + direction.y);
		if (food.contains(nextTile)) {
			return true;
		}
		
		return false;
	}	
	

	public double[] getInfoFromDirection(Point direction, int boardSize, List<Point> snakes) {
	
		double[] info = new double[3];
		
		int distance = 0;
		Point currentPos = (Point) head.clone();
		boolean snakeFound = false;
		boolean foodFound = false;
		
		do {	
				currentPos.x += direction.x;
				currentPos.y += direction.y;
				distance += 1;
				
				if (!foodFound && food.contains(currentPos)) {
					foodFound = true;
					info[0] = 1.0;
				}
				
				if (!snakeFound && snakes.contains(currentPos)) {
					snakeFound = true;
					info[1] = 1.0/distance;
				}
				
		} while (currentPos.x < boardSize && currentPos.y < boardSize &&
		         currentPos.x >= 0 && currentPos.y >= 0);
				
		
		info[2] = 1.0/distance;
		
		return info;
		
	}
	
	public void think(int boardSize, List<Point> snakes) {
		
//		double[] inputs = new double[24];
		double[] inputs = new double[16];
		
//		double[] info = getInfoFromDirection(new Point(-1, 0), boardSize, snakes);
//		inputs[0] = info[0];
//		inputs[1] = info[2];
//		
//		info = getInfoFromDirection(new Point(0, -1), boardSize, snakes);
//		inputs[2] = info[0];
//		inputs[3] = info[2];
//		
//		info = getInfoFromDirection(new Point(1, 0), boardSize, snakes);
//		inputs[4] = info[0];
//		inputs[5] = info[2];
//		
//		info = getInfoFromDirection(new Point(0, 1), boardSize, snakes);
//		inputs[6] = info[0];
//		inputs[7] = info[2];

		
		double[] info = getInfoFromDirection(new Point(-1, 0), boardSize, snakes);
		inputs[0] = info[0];
		inputs[1] = info[2];
		
		info = getInfoFromDirection(new Point(-1, -1), boardSize, snakes);
		inputs[2] = info[0];
		inputs[3] = info[2];
		
		info = getInfoFromDirection(new Point(0, -1), boardSize, snakes);
		inputs[4] = info[0];
		inputs[5] = info[2];
		
		info = getInfoFromDirection(new Point(1, -1), boardSize, snakes);
		inputs[6] = info[0];
		inputs[7] = info[2];
		
		info = getInfoFromDirection(new Point(1, 0), boardSize, snakes);
		inputs[8] = info[0];
		inputs[9] = info[2];
		
		info = getInfoFromDirection(new Point(1, 1), boardSize, snakes);
		inputs[10] = info[0];
		inputs[11] = info[2];
		
		info = getInfoFromDirection(new Point(0, 1), boardSize, snakes);
		inputs[12] = info[0];
		inputs[13] = info[2];
		
		info = getInfoFromDirection(new Point(-1, 1), boardSize, snakes);
		inputs[14] = info[0];
		inputs[15] = info[2];
		
		
		
		
		// Look in each of the 8 direction and find the nearest snake, food and wall
//		double[] info = getInfoFromDirection(new Point(-1, 0), boardSize, snakes);
//		inputs[0] = info[0];
//		inputs[1] = info[1];
//		inputs[2] = info[2];
//		
//		info = getInfoFromDirection(new Point(-1, -1), boardSize, snakes);
//		inputs[3] = info[0];
//		inputs[4] = info[1];
//		inputs[5] = info[2];
//		
//		info = getInfoFromDirection(new Point(0, -1), boardSize, snakes);
//		inputs[6] = info[0];
//		inputs[7] = info[1];
//		inputs[8] = info[2];
//		
//		info = getInfoFromDirection(new Point(1, -1), boardSize, snakes);
//		inputs[9] = info[0];
//		inputs[10] = info[1];
//		inputs[11] = info[2];
//		
//		info = getInfoFromDirection(new Point(1, 0), boardSize, snakes);
//		inputs[12] = info[0];
//		inputs[13] = info[1];
//		inputs[14] = info[2];
//		
//		info = getInfoFromDirection(new Point(1, 1), boardSize, snakes);
//		inputs[15] = info[0];
//		inputs[16] = info[1];
//		inputs[17] = info[2];
//		
//		info = getInfoFromDirection(new Point(0, 1), boardSize, snakes);
//		inputs[18] = info[0];
//		inputs[19] = info[1];
//		inputs[20] = info[2];
//		
//		info = getInfoFromDirection(new Point(-1, 1), boardSize, snakes);
//		inputs[21] = info[0];
//		inputs[22] = info[1];
//		inputs[23] = info[2];
		
			
		brain.setInput(inputs);
		brain.calculate();
		double[] output = brain.getOutput();
		
		double highestOutput = 0.0;
		
		// go left
		highestOutput = output[0];
		direction.x = -1;
		direction.y = 0;
		
		// go up
		if (output[1] > highestOutput) {
			highestOutput = output[1];
			direction.x = 0;
			direction.y = -1;
		}
		
		// go right
		if (output[2] > highestOutput) {
			highestOutput = output[2];
			direction.x = 1;
			direction.y = 0;
		}
		
		// go down
		if (output[3] > highestOutput) {
			direction.x = 0;
			direction.y = 1;
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
			generateNewFood(getNextTile(direction));
		}
		
		move(direction);
	}
	
	public void kill() {
		isDead = true;
	}
	
	public boolean isDead() {
		return isDead;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public int getScore() {
		return score;
	}
	
	public void calculateFitness() {
		// TODO: fix fitness calculation
		
		// Try to help the snake learn that eating is healthy
		if (tail.size() < 10) {
			fitness = (int)Math.floor(score * score  * Math.pow(2, 1 + tail.size()));
		}
		else {
			fitness = (int)Math.floor(score * score);
		}
//		fitness = (int)Math.floor(score * score  * Math.pow(2, 1 + tail.size()));
	}
	
	public void mutate(double mutationRate) {
		
		GaussianRandomizer randomizer = new GaussianRandomizer(0.0, 0.1);
//		brain.randomizeWeights(randomizer);
		Double[] weights = brain.getWeights();
		double[] adjustedWeights = new double[weights.length];
				
		for (int i=0; i<weights.length; ++i) {
			double weight = weights[i];
			double mutateRnd = random.nextDouble();
			if (mutateRnd < mutationRate) {
				double offset = random.nextGaussian() % 1 / 100.0;
				weight += offset;
				if (weight > 1.0)
					weight = 1.0;
				if (weight < -1.0)
					weight = -1.0;
			}
			adjustedWeights[i] = weight;
		}
		
		brain.setWeights(adjustedWeights);
	}
}
