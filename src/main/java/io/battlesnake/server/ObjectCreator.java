package io.battlesnake.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.battlesnake.world.Board;
import io.battlesnake.world.Field;
import io.battlesnake.world.Snake;

public class ObjectCreator {

    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);
    
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
		
		if (body.isEmpty()) {
			LOG.error("Body empty");
		}
		
		return body;
	}
	
	public static Board createBoard(JsonNode jsonNode) {
		
		int boardSize = jsonNode.get("height").asInt();
		if (boardSize < 1) {
			LOG.error("Board size too small: " + boardSize);
		}
		
		List<Field> foodPositions = new ArrayList<Field>();
		List<Snake> snakes = new ArrayList<Snake>();

		jsonNode.get("food").forEach(food -> foodPositions.add(createField(food)));
		jsonNode.get("snakes").forEach(snake -> snakes.add(createSnake(snake)));
		
		if (foodPositions.isEmpty()) {
			LOG.error("Food positions empty");
		}
		
		if (snakes.isEmpty()) {
			LOG.error("Snakes list empty");
		}
		
		return new Board(boardSize, foodPositions, snakes);
	}
}
