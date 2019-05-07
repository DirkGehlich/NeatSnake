package io.battlesnake.logic;

import java.util.LinkedList;

import io.battlesnake.neat.Genome;
import io.battlesnake.world.Board;
import io.battlesnake.world.Field;
import io.battlesnake.world.Snake;

public class NeatSnake extends Snake {

	protected Genome brain = null;

	private double[] inputs = new double[17];
	protected Board board;

	public NeatSnake(Snake snake) {
		super(snake);
	}

	public NeatSnake(LinkedList<Field> body, int health) {
		super(body, health);
	}

	public void setBrain(Genome brain) {
		this.brain = brain;
	}

	public Field evaluateNextMove(Board board) {

		// Performance increase instead of passing it to sub functions?
		this.board = board;

		look();
		Field nextField = think();

		return nextField;
	}

	protected Field getMovementDirecton() {

		Field head = body.getFirst();
		Field firstTail = body.get(1);

		Field direction = head.clone();
		direction.substract(firstTail);

		return direction;
	}

	protected Field getDirectionLeft(Field movementDirection) {
		if (movementDirection.getX() == 0 && movementDirection.getY() == -1) {
			return new Field(-1, 0);
		} else if (movementDirection.getX() == 1 && movementDirection.getY() == 0) {
			return new Field(0, -1);
		} else if (movementDirection.getX() == 0 && movementDirection.getY() == 1) {
			return new Field(1, 0);
		} else if (movementDirection.getX() == -1 && movementDirection.getY() == 0) {
			return new Field(0, 1);
		}

		return null;
	}

	protected Field getDirectionStraightLeft(Field movementDirection) {
		if (movementDirection.getX() == 0 && movementDirection.getY() == -1) {
			return new Field(-1, -1);
		} else if (movementDirection.getX() == 1 && movementDirection.getY() == 0) {
			return new Field(1, -1);
		} else if (movementDirection.getX() == 0 && movementDirection.getY() == 1) {
			return new Field(1, 1);
		} else if (movementDirection.getX() == -1 && movementDirection.getY() == 0) {
			return new Field(-1, 1);
		}

		return null;
	}

	protected Field getDirectionRight(Field movementDirection) {
		if (movementDirection.getX() == 0 && movementDirection.getY() == -1) {
			return new Field(1, 0);
		} else if (movementDirection.getX() == 1 && movementDirection.getY() == 0) {
			return new Field(0, 1);
		} else if (movementDirection.getX() == 0 && movementDirection.getY() == 1) {
			return new Field(-1, 0);
		} else if (movementDirection.getX() == -1 && movementDirection.getY() == 0) {
			return new Field(0, -1);
		}

		return null;
	}

	protected Field getDirectionStraightRight(Field movementDirection) {
		if (movementDirection.getX() == 0 && movementDirection.getY() == -1) {
			return new Field(1, -1);
		} else if (movementDirection.getX() == 1 && movementDirection.getY() == 0) {
			return new Field(1, 1);
		} else if (movementDirection.getX() == 0 && movementDirection.getY() == 1) {
			return new Field(-1, 1);
		} else if (movementDirection.getX() == -1 && movementDirection.getY() == 0) {
			return new Field(-1, -1);
		}

		return null;
	}
	
	protected Field getDirectionBackLeft(Field movementDirection) {
		if (movementDirection.getX() == 0 && movementDirection.getY() == -1) {
			return new Field(-1, 1);
		} else if (movementDirection.getX() == 1 && movementDirection.getY() == 0) {
			return new Field(-1, -1);
		} else if (movementDirection.getX() == 0 && movementDirection.getY() == 1) {
			return new Field(1, -1);
		} else if (movementDirection.getX() == -1 && movementDirection.getY() == 0) {
			return new Field(1, 1);
		}

		return null;
	}
	
	protected Field getDirectionBackRight(Field movementDirection) {
		if (movementDirection.getX() == 0 && movementDirection.getY() == -1) {
			return new Field(1, 1);
		} else if (movementDirection.getX() == 1 && movementDirection.getY() == 0) {
			return new Field(-1, 1);
		} else if (movementDirection.getX() == 0 && movementDirection.getY() == 1) {
			return new Field(-1, -1);
		} else if (movementDirection.getX() == -1 && movementDirection.getY() == 0) {
			return new Field(1, -1);
		}

		return null;
	}
	
	protected void look() {

		Field directionStraight = getMovementDirecton();
		Field directionLeft = getDirectionLeft(directionStraight);
		Field directionRight = getDirectionRight(directionStraight);
		Field directionStraightLeft = getDirectionStraightLeft(directionStraight);
		Field directionStraightRight = getDirectionStraightRight(directionStraight);
		Field directionBackLeft = getDirectionBackLeft(directionStraight);
		Field directionBackRight = getDirectionBackRight(directionStraight);

		inputs[0] = getDistanceToWall(directionStraight);
		inputs[1] = getDistanceToWall(directionLeft);
		inputs[2] = getDistanceToWall(directionRight);

		inputs[3] = getDistanceToEnemy(directionStraight);
		inputs[4] = getDistanceToEnemy(directionLeft);
		inputs[5] = getDistanceToEnemy(directionRight);
		inputs[6] = getDistanceToEnemy(directionStraightLeft);
		inputs[7] = getDistanceToEnemy(directionStraightRight);
		inputs[8] = getDistanceToEnemy(directionBackLeft);
		inputs[9] = getDistanceToEnemy(directionBackRight);

		inputs[10] = getDistanceToFood(directionStraight);
		inputs[11] = getDistanceToFood(directionLeft);
		inputs[12] = getDistanceToFood(directionRight);
		inputs[13] = getDistanceToFood(directionStraightLeft);
		inputs[14] = getDistanceToFood(directionStraightRight);
		inputs[15] = getDistanceToFood(directionBackLeft);
		inputs[16] = getDistanceToFood(directionBackRight);
	}

	private double getDistanceToWall(Field direction) {
		int boardSizeX = board.getBoardSizeX();
		int boardSizeY = board.getBoardSizeY();

		Field tmpPos = getHeadPosition().clone();
		tmpPos.add(direction);

		int distance = 1;
		while (tmpPos.getX() < boardSizeX && tmpPos.getY() < boardSizeY && tmpPos.getX() >= 0 && tmpPos.getY() >= 0) {
			++distance;
			tmpPos.add(direction);
		}

		return 1.0 / distance;
	}

	private double getDistanceToEnemy(Field direction) {
		int boardSizeX = board.getBoardSizeX();
		int boardSizeY = board.getBoardSizeY();

		Field tmpPos = getHeadPosition().clone();
		tmpPos.add(direction);

		int distance = 1;
		while (tmpPos.getX() < boardSizeX && tmpPos.getY() < boardSizeY && tmpPos.getX() >= 0 && tmpPos.getY() >= 0) {
			++distance;

			if (board.getSnakePositions().contains(tmpPos)) {
				return 1.0 / distance;
			}

			tmpPos.add(direction);
		}

		return 0;
	}

	private double getDistanceToFood(Field direction) {
		int boardSizeX = board.getBoardSizeX();
		int boardSizeY = board.getBoardSizeY();

		Field tmpPos = getHeadPosition().clone();
		tmpPos.add(direction);

		int distance = 1;
		while (tmpPos.getX() < boardSizeX && tmpPos.getY() < boardSizeY && tmpPos.getX() >= 0 && tmpPos.getY() >= 0) {
			++distance;

			if (board.getFoodPositions().contains(tmpPos)) {
				return 1.0 / distance;
			}

			tmpPos.add(direction);
		}

		return 0;
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

		double[] output = brain.calculate(inputs);

		double highestOutput = 0.0;

		int outputIdx = 0;

		for (int i = 0; i < output.length; ++i) {
			if (output[i] > highestOutput) {
				highestOutput = output[i];
				outputIdx = i;
			}
		}

		// go straight
		if (outputIdx == 0) {
			return getMovementDirecton();
		}

		// go left
		else if (outputIdx == 1) {
			return getDirectionLeft(getMovementDirecton());
		}

		// go right
		else {
			return getDirectionRight(getMovementDirecton());
		}
	}
}
