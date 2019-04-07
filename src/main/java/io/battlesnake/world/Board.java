package io.battlesnake.world;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private final int BOARD_SIZE_X;
	private final int BOARD_SIZE_Y;
	List<Field> foodPositions;
	List<Snake> snakes;
	List<Field> snakePositions = new ArrayList<Field>();
	
	
	public Board(int boardSizeX, int boardSizeY, List<Field> foodPositions, List<Snake> snakes) {
		super();
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;
		this.foodPositions = foodPositions;
		this.snakes = snakes;
		
		snakes.forEach(snake -> snakePositions.addAll(snake.body));
	}
	
	public Board(int boardSizeX, int boardSizeY, List<Field> snakePositions) {
		super();
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;
		this.snakePositions = snakePositions;
	}

	public int getBoardSizeX() {
		return BOARD_SIZE_X;
	}

	public int getBoardSizeY() {
		return BOARD_SIZE_Y;
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

	public void setSnakePositions(List<Field> snakePositions) {
		this.snakePositions = snakePositions;
	}

	public void setFoodPositions(List<Field> foodPositions) {
		this.foodPositions = foodPositions;
	}
	
	
}
