package io.battlesnake.training;

import java.util.ArrayList;
import java.util.List;

import io.battlesnake.world.Field;

public class EnemySnakes extends ArrayList<EnemySnake> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnemySnakes() {
		for (int i = 0; i < Settings.NUM_ENEMYSNAKES; ++i) {
			EnemySnake enemySnake = new EnemySnake(Settings.BOARDSIZE_X, Settings.BOARDSIZE_Y);
			add(enemySnake);
		}
	}

	public void moveRandomly() {
		forEach(enemySnake -> enemySnake.moveRandomly());
	}

	public void reset() {
		forEach(enemySnake -> enemySnake.reset());
	}

	public List<Field> getBodyFields() {
		List<Field> bodyFields = new ArrayList<Field>();

		forEach(enemySnake -> bodyFields.addAll(enemySnake.getBody()));

		return bodyFields;
	}
}
