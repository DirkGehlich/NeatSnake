package io.battlesnake.world;

import java.util.LinkedList;
import java.util.List;

public class Snake {

	protected final int MAXHEALTH;
	protected LinkedList<Field> body;
	protected int health;

	public Snake(Snake snake) {
		this.body = snake.body;
		this.health = snake.health;
		this.MAXHEALTH = snake.MAXHEALTH;
	}

	public Snake() {
		super();
		this.MAXHEALTH = 0;
	}

	public Snake(LinkedList<Field> body, int health) {
		super();
		this.body = body;
		this.health = health;
		this.MAXHEALTH = health;
	}

	public List<Field> getBody() {
		return body;
	}

	public void setBody(LinkedList<Field> body) {
		this.body = body;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Field getHeadPosition() {
		return body.getFirst();
	}

	public int getHealth() {
		return health;
	}

}
