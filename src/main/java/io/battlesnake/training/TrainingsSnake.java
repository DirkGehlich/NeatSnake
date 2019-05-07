package io.battlesnake.training;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.battlesnake.logic.NeatSnake;
import io.battlesnake.neat.Genome;
import io.battlesnake.world.Board;
import io.battlesnake.world.Field;

public class TrainingsSnake extends NeatSnake {

	private final int MAXFOOD = 5;
	private final int BOARD_SIZE_X;
	private final int BOARD_SIZE_Y;
	private boolean isDead = false;

	private long fitness = 0;
	private int lifetime = 0;
	private List<Field> food = new ArrayList<Field>();
	private EnemySnakes enemySnakes = new EnemySnakes();

	private Random random = new Random();

	public TrainingsSnake(int boardSizeX, int boardSizeY, LinkedList<Field> body) {
		super(body, Settings.MAXHEALTH);
		BOARD_SIZE_X = boardSizeX;
		BOARD_SIZE_Y = boardSizeY;
	}

	public TrainingsSnake(int boardSizeX, int boardSizeY, LinkedList<Field> body, Genome brain) {
		super(body, Settings.MAXHEALTH);
		this.BOARD_SIZE_X = boardSizeX;
		this.BOARD_SIZE_Y = boardSizeY;
		this.brain = brain;
	}

	public void updateEnvironment(EnemySnakes enemySnakes) {
		this.enemySnakes = enemySnakes;
	}

	public void generateFood() {

		food.clear();
		for (int i = 0; i < MAXFOOD; ++i) {
			Field newFood = new Field(random.nextInt(BOARD_SIZE_X), random.nextInt(BOARD_SIZE_Y));
			food.add(newFood);
		}
	}

	public boolean isDead() {
		return isDead;
	}

	public void moveIn(Field direction) {
		--health;
		++lifetime;
		if (health <= 0)
			kill();

		Field nextField = getHeadPosition().clone();
		nextField.add(direction);

		Field addedTail = null;
		if (isEatingFood(nextField)) {

			addedTail = body.getLast().clone();

			health = MAXHEALTH;
			generateNewFood(nextField);
		}

		for (int i = body.size() - 1; i > 0; --i) {
			body.get(i).setX(body.get(i - 1).getX());
			body.get(i).setY(body.get(i - 1).getY());
		}

		body.getFirst().add(direction);

		if (addedTail != null) {
			body.add(addedTail);
		}
	}

	private boolean isEatingFood(Field nextField) {

		if (food.contains(nextField)) {
			return true;
		}

		return false;
	}

	private void generateNewFood(Field eatenFood) {

		food.remove(eatenFood);
		Field newFood = new Field(random.nextInt(BOARD_SIZE_X), random.nextInt(BOARD_SIZE_Y));
		food.add(newFood);
	}

	public void checkIfAlive() {

		Field head = body.getFirst();

		// Hit the wall
		if (head.getX() >= BOARD_SIZE_X || head.getX() < 0 || head.getY() >= BOARD_SIZE_Y || head.getY() < 0) {
			kill();
		}

		// Hit enemy snake
		for (EnemySnake enemySnake : enemySnakes) {
			for (Field bodyPart : enemySnake.getBody()) {
				if (head.equals(bodyPart)) {
					kill();
				}
			}
		}

		// Hit itself
		for (int i = 1; i < body.size(); ++i) {
			Field ownTail = body.get(i);
			if (head.equals(ownTail)) {
				kill();
			}
		}
	}

	private void kill() {
		isDead = true;
	}

	public long getFitness() {
		return fitness;
	}

	public void calculateFitness() {
		// Try to help the snake learn that eating is healthy

		int len = body.size();

//		fitness = (long)Math.floor(Math.pow(len - 2, 2)) * lifetime;
		fitness = (long) len * lifetime * lifetime;
//		fitness = lifetime/10;
		brain.setFitness(fitness);

//		
//		if (len < 10) {
//			fitness = (int)Math.floor(lifetime * lifetime * Math.pow(2, len));
//		} else {
//		      //grows slower after 10 to stop fitness from getting stupidly big
//		      //ensure greater than len = 9
//		      fitness =  lifetime * lifetime;
//		      fitness *= Math.pow(2, 10);
//		      fitness *=(len-9);
//		}
	}

	public Genome getBrain() {
		return brain;
	}

	public int getLifetime() {
		return lifetime;
	}

	@Override
	public Field evaluateNextMove(Board board) {

		board.setFoodPositions(food);
		// Performance increase instead of passing it to sub functions?
		this.board = board;

		look();
		Field nextField = think();

		return nextField;
	}

	public void setFood(List<Field> food) {
		this.food = food;
	}

}
