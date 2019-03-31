package io.battlesnake.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.battlesnake.world.Board;
import io.battlesnake.world.Field;
import io.battlesnake.world.Snake;

public class ObjectCreator {

	public static Snake createSnake(JsonNode jsonNode) {

		LinkedList<Field> body = createBody(jsonNode.get("body"));
		int health = jsonNode.get("health").asInt();
		
		return new Snake(body, health);
	}
	
	private static Field createField(JsonNode jsonNode) {
		return new Field(jsonNode.get("x").asInt(), jsonNode.get("y").asInt());		
	}
	
	public static LinkedList<Field> createBody(JsonNode jsonNode) {
		LinkedList<Field> body = new LinkedList<Field>();
		jsonNode.forEach(field -> body.add(createField(field)));
		
		return body;
	}
	
	public static Board createBoard(JsonNode jsonNode) {
		
		int boardSize = jsonNode.get("height").asInt();
		List<Field> foodPositions = new ArrayList<Field>();
		List<Snake> snakes = new ArrayList<Snake>();

		jsonNode.get("food").forEach(food -> foodPositions.add(createField(food)));
		jsonNode.get("snakes").forEach(snake -> snakes.add(createSnake(snake)));
		
		return new Board(boardSize, foodPositions, snakes);
	}
}
