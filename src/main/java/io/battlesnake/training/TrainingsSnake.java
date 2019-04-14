package io.battlesnake.training;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.RangeRandomizer;

import io.battlesnake.logic.NeatSnake;
import io.battlesnake.world.Board;
import io.battlesnake.world.Field;

public class TrainingsSnake extends NeatSnake {

	private final int MAXFOOD = 4;
	private final int BOARD_SIZE_X;
	private final int BOARD_SIZE_Y;
	private boolean isDead = false;

	private long fitness = 0;
	private int lifetime = 0;
	private List<Field> food = new ArrayList<Field>();
	private List<Field> enemySnakes = new ArrayList<Field>();

	private RangeRandomizer randomizer = new RangeRandomizer(-1.0, 1.0);
	private Random random = new Random();

	public TrainingsSnake(int boardSizeX, int boardSizeY, LinkedList<Field> body) {
		super(body, Settings.MAXHEALTH);
		BOARD_SIZE_X = boardSizeX;
		BOARD_SIZE_Y = boardSizeY;

		createBrain();
		brain.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);
		brain.randomizeWeights(randomizer);
	}

	public TrainingsSnake(int boardSizeX, int boardSizeY, LinkedList<Field> body, MultiLayerPerceptron brain) {
		super(body, Settings.MAXHEALTH);
		BOARD_SIZE_X = boardSizeX;
		BOARD_SIZE_Y = boardSizeY;

		createBrain();
		Double[] parentWeights = brain.getWeights();
		double[] weights = new double[parentWeights.length];

		for (int i = 0; i < parentWeights.length; ++i) {
			weights[i] = parentWeights[i];
		}
		this.brain.setWeights(weights);
	}

	public void createBrain() {
		this.brain = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 24, 18, 4);
	}

	public TrainingsSnake copy() {

		// TODO: put somewhere else
		LinkedList<Field> body = new LinkedList<Field>();
		body.add(new Field(5, 4));
		body.add(new Field(5, 5));
		body.add(new Field(5, 6));

		TrainingsSnake snake = new TrainingsSnake(BOARD_SIZE_X, BOARD_SIZE_Y, body, brain);

		return snake;
	}

	public void updateEnvironment(List<Field> enemySnakes) {
		this.enemySnakes = enemySnakes;
	}

	public void generateFood() {

		food.clear();
		for (int i = 0; i < MAXFOOD; ++i) {
			Field newFood = new Field(random.nextInt(BOARD_SIZE_X), random.nextInt(BOARD_SIZE_Y));
			food.add(newFood);
		}
	}

	public boolean isDead() {
		return isDead;
	}

	public void moveIn(Field direction) {
		--health;
		++lifetime;
		if (health <= 0)
			kill();

		Field nextField = getHeadPosition().clone();
		nextField.add(direction);

		Field addedTail = null;
		if (isEatingFood(nextField)) {

			addedTail = body.getLast().clone();

			health = MAXHEALTH;
			generateNewFood(nextField);
		}

		for (int i = body.size() - 1; i > 0; --i) {
			body.get(i).setX(body.get(i - 1).getX());
			body.get(i).setY(body.get(i - 1).getY());
		}

		body.getFirst().add(direction);

		if (addedTail != null) {
			body.add(addedTail);
		}
	}

	private boolean isEatingFood(Field nextField) {

		if (food.contains(nextField)) {
			return true;
		}

		return false;
	}

	private void generateNewFood(Field eatenFood) {

		food.remove(eatenFood);
		Field newFood = new Field(random.nextInt(BOARD_SIZE_X), random.nextInt(BOARD_SIZE_Y));
		food.add(newFood);
	}

	public void checkIfAlive() {

		Field head = body.getFirst();

		// Hit the wall
		if (head.getX() >= BOARD_SIZE_X || head.getX() < 0 || head.getY() >= BOARD_SIZE_Y || head.getY() < 0) {
			kill();
		}

		// Hit enemy snake
		for (Field enemySnakeCoord : enemySnakes) {
			if (head.equals(enemySnakeCoord)) {
				kill();
			}
		}

		// Hit itself
		for (int i = 1; i < body.size(); ++i) {
			Field ownTail = body.get(i);
			if (head.equals(ownTail)) {
				kill();
			}
		}
	}

	public void mutate(double mutationRate) {

//		GaussianRandomizer randomizer = new GaussianRandomizer(0.0, 0.1);
//		brain.randomizeWeights(randomizer);
		Double[] weights = brain.getWeights();
		double[] adjustedWeights = new double[weights.length];

		for (int i = 0; i < weights.length; ++i) {
			double weight = weights[i];
			double mutateRnd = random.nextDouble();
			if (mutateRnd < mutationRate) {
				double offset = random.nextGaussian() / 5.0;
				weight += offset;
//				weight = random.nextGaussian(); // TODO: remove
				if (weight > 1.0)
					weight = 1.0;
				if (weight < -1.0)
					weight = -1.0;
			}
			adjustedWeights[i] = weight;
		}

		brain.setWeights(adjustedWeights);
	}

	private void kill() {
		isDead = true;
	}

	public long getFitness() {
		return fitness;
	}

	public void calculateFitness() {
		// Try to help the snake learn that eating is healthy

		int len = body.size();

//		fitness = (long)Math.floor(Math.pow(len - 2, 2)) * lifetime;
		fitness = (long) len * lifetime * lifetime;

//		
//		if (len < 10) {
//			fitness = (int)Math.floor(lifetime * lifetime * Math.pow(2, len));
//		} else {
//		      //grows slower after 10 to stop fitness from getting stupidly big
//		      //ensure greater than len = 9
//		      fitness =  lifetime * lifetime;
//		      fitness *= Math.pow(2, 10);
//		      fitness *=(len-9);
//		}
	}

	public MultiLayerPerceptron getBrain() {
		return brain;
	}

	public int getLifetime() {
		return lifetime;
	}

	@Override
	public Field evaluateNextMove(Board board) {

		board.setFoodPositions(food);
		// Performance increase instead of passing it to sub functions?
		this.board = board;

		look();
		Field nextField = think();

		return nextField;
	}

	public void setFood(List<Field> food) {
		this.food = food;
	}

}
