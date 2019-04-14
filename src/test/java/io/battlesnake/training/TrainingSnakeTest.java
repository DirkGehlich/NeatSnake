package io.battlesnake.training;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.battlesnake.world.Field;

public class TrainingSnakeTest {
	
	private Field defaultDirection = new Field(1, 0);
	
	private TrainingsSnake createDefaultSnake() {
		LinkedList<Field> body = new LinkedList<Field>();
		body.add(new Field(5, 4));
		body.add(new Field(5, 5));
		body.add(new Field(5, 6));
		
		TrainingsSnake snake = new TrainingsSnake(11, 11, body);
		
		return snake;
	}
	
	@Test
	public void movingWith1HealthLeftShouldKillSnake() {
		
		TrainingsSnake snake = createDefaultSnake();		
		snake.setHealth(1);
		
		snake.moveIn(defaultDirection);
		
		assertTrue(snake.isDead());		
	}
	
	@Test
	public void movingToFieldWithFoodShouldIncreaseBodyAndHealth() {
		TrainingsSnake snake = createDefaultSnake();
		List<Field> food = new ArrayList<Field>();
		food.add(new Field(6, 4));
		snake.setFood(food);
		LinkedList<Field> movedBody = new LinkedList<Field>();
		movedBody.add(new Field(6, 4));
		movedBody.add(new Field(5, 4));
		movedBody.add(new Field(5, 5));
		movedBody.add(new Field(5, 6));
				
		snake.moveIn(defaultDirection);
		
		assertEquals(snake.getBody().size(), 4);
		assertEquals(snake.getBody(), movedBody);
		assertEquals(snake.getHealth(), Settings.MAXHEALTH);
	}

}
