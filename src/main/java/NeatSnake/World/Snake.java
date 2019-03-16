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
	
	public void think() {
		brain.setInput(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
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
