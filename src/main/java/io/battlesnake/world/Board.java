package io.battlesnake.world;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private final int BOARDSIZE;
	List<Field> foodPositions;
	List<Snake> snakes;
	List<Field> snakePositions = new ArrayList<Field>();
	
	
	public Board(int boardSize, List<Field> foodPositions, List<Snake> snakes) {
		super();
		BOARDSIZE = boardSize;
		this.foodPositions = foodPositions;
		this.snakes = snakes;
		
		snakes.forEach(snake -> snakePositions.addAll(snake.body));
	}


	public int getBoardsize() {
		return BOARDSIZE;
	}


	public List<Field> getFoodPositions() {
		return foodPositions;
	}


	public List<Snake> getSnakes() {
		return snakes;
	}


	public List<Field> getSnakePositions() {
		return snakePositions;
	}
	
	
}
