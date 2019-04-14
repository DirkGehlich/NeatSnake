package server;

import java.util.List;

public class Snake {

	private String id;
	private String name;
	private int health;
	private List<Position> body;

	public Snake() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public List<Position> getBody() {
		return body;
	}

	public void setBody(List<Position> body) {
		this.body = body;
	}

}
