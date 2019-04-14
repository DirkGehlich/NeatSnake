package io.battlesnake.logic;

import java.util.LinkedList;

import org.neuroph.nnet.MultiLayerPerceptron;

import io.battlesnake.world.Board;
import io.battlesnake.world.Field;
import io.battlesnake.world.Snake;

public class NeatSnake extends Snake {

	protected MultiLayerPerceptron brain = null;

	private double[] inputs = new double[24];
	protected Board board;

	public NeatSnake(Snake snake) {
		super(snake);
	}

	public NeatSnake(LinkedList<Field> body, int health) {
		super(body, health);
	}

	public void setBrain(MultiLayerPerceptron brain) {
		this.brain = brain;
	}

	public Field evaluateNextMove(Board board) {

		// Performance increase instead of passing it to sub functions?
		this.board = board;

		look();
		Field nextField = think();

		return nextField;
	}

	protected void look() {
		// Look in each of the 8 direction and find the nearest snake, food and wall
		double[] info = getInfoFromDirection(new Field(-1, 0));
		inputs[0] = info[0];
		inputs[1] = info[1];
		inputs[2] = info[2];

		info = getInfoFromDirection(new Field(-1, -1));
		inputs[3] = info[0];
		inputs[4] = info[1];
		inputs[5] = info[2];

		info = getInfoFromDirection(new Field(0, -1));
		inputs[6] = info[0];
		inputs[7] = info[1];
		inputs[8] = info[2];

		info = getInfoFromDirection(new Field(1, -1));
		inputs[9] = info[0];
		inputs[10] = info[1];
		inputs[11] = info[2];

		info = getInfoFromDirection(new Field(1, 0));
		inputs[12] = info[0];
		inputs[13] = info[1];
		inputs[14] = info[2];

		info = getInfoFromDirection(new Field(1, 1));
		inputs[15] = info[0];
		inputs[16] = info[1];
		inputs[17] = info[2];

		info = getInfoFromDirection(new Field(0, 1));
		inputs[18] = info[0];
		inputs[19] = info[1];
		inputs[20] = info[2];

		info = getInfoFromDirection(new Field(-1, 1));
		inputs[21] = info[0];
		inputs[22] = info[1];
		inputs[23] = info[2];

//		evaluateNearestFood();
	}

	public double[] getInfoFromDirection(Field direction) {

		int boardSizeX = board.getBoardSizeX();
		int boardSizeY = board.getBoardSizeY();
		double[] info = new double[3];

//		Field currentPos = getHeadPosition();

		// TODO: Always try to get some distance to food
		// Get shortest distance to food from this position.
		// Idea could be to not only take shortest one, but area with most food, so that
		// when one gets stolen by an enemy, we are still in an area of food

		Field tmpPos = getHeadPosition().clone();
		tmpPos.add(direction);

//		if (tmpPos.getX() >= 0 && tmpPos.getY() >= 0) {
//			double distanceBefore = 1000.0;
//			for (Field f : board.getFoodPositions()) {
//				double distanceToFood = currentPos.distanceTo(f);
//				if (distanceToFood < distanceBefore)
//					distanceBefore = distanceToFood;
//			}
//
//			double distanceAfter = 1000.0;
//			for (Field f : board.getFoodPositions()) {
//				double distanceToFood = tmpPos.distanceTo(f);
//				if (distanceToFood < distanceAfter)
//					distanceAfter = distanceToFood;
//			}
//
//			if (distanceAfter < distanceBefore && distanceAfter != 1000.0) {
//				info[0] = (distanceBefore - distanceAfter);
//			}
//		}

		boolean foodFound = false;
		boolean snakeFound = false;
		int distance = 1;
		while (tmpPos.getX() < boardSizeX && tmpPos.getY() < boardSizeY && tmpPos.getX() >= 0 && tmpPos.getY() >= 0) {

			++distance;

			if (!foodFound && board.getFoodPositions().contains(tmpPos)) {
				foodFound = true;
				info[0] = 1.0 / distance;
			}

			if (!snakeFound && board.getSnakePositions().contains(tmpPos)) {
				snakeFound = true;
				info[1] = 1.0 / distance;
			}

			tmpPos.add(direction);
		}

		info[2] = 1.0 / distance;

		return info;

	}

	public Field think() {

		brain.setInput(inputs);
		brain.calculate();
		double[] output = brain.getOutput();

		double highestOutput = 0.0;

		int outputIdx = 0;

		for (int i = 0; i < output.length; ++i) {
			if (output[i] > highestOutput) {
				highestOutput = output[i];
				outputIdx = i;
			}
		}

		// go left
		if (outputIdx == 0) {
			return new Field(-1, 0);
		}

		// go up
		else if (outputIdx == 1) {
			return new Field(0, -1);
		}

		// go right
		else if (outputIdx == 2) {
			return new Field(1, 0);
		}

		// go down
		else {
			return new Field(0, 1);
		}

	}

	protected void evaluateNearestFood() {

		double nearestFood = 0.0;
		int nearestFoodIdx = -1;

		for (int i = 0; i < inputs.length - 1; i += 3) {
			if (inputs[i + 2] < 1.0 && inputs[i] > nearestFood) {
				nearestFood = inputs[i];
				nearestFoodIdx = i;
			}

			inputs[i] = 0;
		}

		if (nearestFoodIdx >= 0) {
			inputs[nearestFoodIdx] = 1.0;
		}
	}
}
