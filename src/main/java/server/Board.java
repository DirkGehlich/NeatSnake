package server;

import java.util.List;

public class Board {
	
	private int height;
	private int width;
	private List<Position> food;
	private List<Snake> snakes;
	
	public Board() {
		
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public List<Position> getFood() {
		return food;
	}
	public void setFood(List<Position> food) {
		this.food = food;
	}

	public List<Snake> getSnakes() {
		return snakes;
	}

	public void setSnakes(List<Snake> snakes) {
		this.snakes = snakes;
	}	
}
