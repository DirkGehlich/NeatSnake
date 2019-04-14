package io.battlesnake.training;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import io.battlesnake.world.Field;
import io.battlesnake.world.Snake;

public class EnemySnake extends Snake {

	private final int BOARD_SIZE_X;
	private final int BOARD_SIZE_Y;

	public EnemySnake(int boardSizeX, int boardSizeY) {
		super();
		BOARD_SIZE_X = boardSizeX;
		BOARD_SIZE_Y = boardSizeY;

		reset();
	}

	public EnemySnake(int boardSizeX, int boardSizeY, LinkedList<Field> body) {
		super(body, 90);
		BOARD_SIZE_X = boardSizeX;
		BOARD_SIZE_Y = boardSizeY;
	}

	public void reset() {
		LinkedList<Field> body = new LinkedList<Field>();
		body.add(new Field(9, 1));
		body.add(new Field(9, 2));
		body.add(new Field(9, 3));
		body.add(new Field(9, 4));
		body.add(new Field(9, 5));

		setBody(body);
		setHealth(MAXHEALTH);
	}

	public boolean wouldDie(Field direction) {
		Field head = body.getFirst().clone();
		head.add(direction);

		if (direction.getX() != 0 && direction.getY() != 0)
			return true;

		// Hit the wall
		if (head.getX() >= BOARD_SIZE_X || head.getX() < 0 || head.getY() >= BOARD_SIZE_Y || head.getY() < 0) {
			return true;
		}

		// Hit itself
		for (int i = 1; i < body.size(); ++i) {
			Field ownTail = body.get(i);
			if (head.equals(ownTail)) {
				return true;
			}
		}

		return false;
	}

	public void moveIn(Field direction) {
		for (int i = body.size() - 1; i > 0; --i) {
			body.get(i).setX(body.get(i - 1).getX());
			body.get(i).setY(body.get(i - 1).getY());
		}

		body.getFirst().add(direction);
	}

	public void moveRandomly() {
		io.battlesnake.world.Field direction = getRandomDirection();

		int deadlockCnt = 0;
		while (deadlockCnt < 20 && wouldDie(direction)) {
			direction = getRandomDirection();
			deadlockCnt++;
		}

		if (deadlockCnt == 20) {
			reset();
		}

		moveIn(direction);

	}

	private Field getRandomDirection() {
		int x = ThreadLocalRandom.current().nextInt(-1, 2);
		int y = ThreadLocalRandom.current().nextInt(-1, 2);

		return new Field(x, y);
	}
}
