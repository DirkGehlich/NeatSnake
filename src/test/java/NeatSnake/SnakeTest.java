package NeatSnake;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import NeatSnake.World.GoodSnake;
import NeatSnake.World.Snake;

class SnakeTest {

//	@Test
//	void eatingFoodShouldIncreaseSnake() {
//		
//		Point startingPosition = new Point(1,1);
//		int boardSize = 10;		
//		Point food = new Point(2,1);
//		Point direction = new Point(1,0);
//		
//		GoodSnake snake = new GoodSnake(boardSize, startingPosition);
//		snake.setFood(food);
//		
//		assertEquals(0, snake.tail.size());
//		snake.move(direction);
//		assertEquals(1, snake.tail.size());
//	}
//	
//	@Test
//	void notEatingFoodShouldNotIncreaseSnake() {
//		
//		Point startingPosition = new Point(1,1);
//		int boardSize = 10;		
//		Point food = new Point(2,1);
//		Point direction = new Point(1,1);
//		
//		GoodSnake snake = new GoodSnake(boardSize, startingPosition);
//		snake.setFood(food);
//		
//		assertEquals(0, snake.tail.size());
//		snake.move(direction);
//		assertEquals(0, snake.tail.size());
//	}
	
//	@Test
//	void calculateFitness() {
//		
//		Point startingPosition = new Point(1,1);
//		int boardSize = 10;		
//		Point direction = new Point(1,0);
//		
//		List<Point> tail = new ArrayList<Point>();
//		tail.add(new Point(1,2));
//		tail.add(new Point(1,3));
//		tail.add(new Point(1,4));
//		
//		GoodSnake snake = new GoodSnake(boardSize, startingPosition, tail);
//		
//		
//		
//		snake.move();
//		snake.move();
//		snake.calculateFitness();
//		
//		assertEquals(256.0, snake.getFitness());
//	}
	

}
