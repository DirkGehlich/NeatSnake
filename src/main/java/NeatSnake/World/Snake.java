package NeatSnake.World;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;

public class Snake {
	public Point head = new Point();
	public List<Point> tail = new ArrayList<Point>();
	
	private NeuralNetwork brain = new MultiLayerPerceptron(24, 8, 4);
	private Point direction = new Point();
	
	private boolean isDead = false;
	
	public Snake() {
		
	}
	
	public Snake(Point startingPosition) {
		head.x = startingPosition.x;
		head.y = startingPosition.y;
	}
	
	public boolean wouldDie(Point direction, int boardSize) {
		
		// TODO: Check if any free space is adjacent to destination tile, otherwise the snake will block itself in a corner
		if (direction.x != 0 && direction.y != 0)
			return true;
		
		Point nextTile = getNextTile(direction);
					
		if (head.equals(nextTile))
			return true;
		
		for (Point tailTile : tail) {
			if (tailTile.equals(nextTile))
				return true;
		}
		
		if (nextTile.x >= boardSize || nextTile.y >= boardSize)
			return true;
		
		if (nextTile.x < 0 || nextTile.y < 0)
			return true;				
		
		return false;			
	}

	private Point getNextTile(Point direction) {
		Point nextTile = new Point();
		
		nextTile.x = head.x + direction.x;
		nextTile.y = head.y + direction.y;
		
		return nextTile;
	}
	
	public void move(Point direction) {
		
		for (int i=0; i<tail.size(); ++i) {
			if (i == tail.size()-1) {
				tail.get(i).x = head.x;
				tail.get(i).y = head.y;
			}
			else {
				tail.get(i).x = tail.get(i+1).x;
				tail.get(i).y = tail.get(i+1).y;
			}
		}
		
		head.x += direction.x;
		head.y += direction.y;
	}
	
	private double getDistance(Point from, Point to) {
		double distance = 0.0;
		
		// Pythagoras a² + b² = c² ==> c = sqrt((bx - ax)² + (by - ay)²)
		distance = Math.sqrt(Math.pow((to.x - from.x), 2) + Math.pow((to.y - from.y), 2));	
		
		return distance;
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
		move(direction);
	}
	
	public void kill() {
		isDead = true;
	}
	
	public boolean isDead() {
		return isDead;
	}
}
