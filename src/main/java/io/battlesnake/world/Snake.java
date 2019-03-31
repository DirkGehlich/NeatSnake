package io.battlesnake.world;

import java.util.LinkedList;

public class Snake {
	
	protected final int MAXHEALTH;
	protected LinkedList<Field> body;
	protected Field headPosition;
	protected int health;
		
	public Snake(Snake snake) {
		this.body = snake.body;
		this.headPosition = snake.headPosition;
		this.health = snake.health;
		this.MAXHEALTH = snake.MAXHEALTH;
	}
	
	public Snake(LinkedList<Field> body, int health) {
		super();
		this.body = body;
		this.headPosition = body.getFirst();
		this.health = health;
		this.MAXHEALTH = health;
	}

	public LinkedList<Field> getBody() {
		return body;
	}

	public void setBody(LinkedList<Field> body) {
		this.body = body;
		this.headPosition = body.getFirst();
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Field getHeadPosition() {
		return headPosition;
	}

	public int getHealth() {
		return health;
	}

}
