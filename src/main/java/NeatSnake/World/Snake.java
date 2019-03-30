package NeatSnake.World;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Snake {
	
	
	
	public Point head = new Point();
	public List<Point> tail = new ArrayList<Point>();
	protected Point direction = new Point();
	
	
	
	

	public Snake() {
		
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

	protected Point getNextTile(Point direction) {
		Point nextTile = new Point();
		
		nextTile.x = head.x + direction.x;
		nextTile.y = head.y + direction.y;
		
		return nextTile;
	}
	
	public void move(Point direction) {
			
		for (int i=0; i<tail.size() - 1; ++i) {
			tail.get(i).x = tail.get(i+1).x;
			tail.get(i).y = tail.get(i+1).y;
		}
		
		if (tail.size() > 0) {
			tail.get(tail.size()-1).x = head.x;
			tail.get(tail.size()-1).y = head.y;
		}
		
		head.x += direction.x;
		head.y += direction.y;	
	}

	public Point getDirection() {
		return direction;
	}

	public void setHead(Point head) {
		this.head = head;
	}
	
	
}
