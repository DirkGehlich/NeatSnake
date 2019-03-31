package io.battlesnake.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.battlesnake.logic.NeatSnake;
import io.battlesnake.utils.StopWatch;
import io.battlesnake.world.Board;
import io.battlesnake.world.Field;
import spark.Request;
import spark.Response;

public class GameService {
	
	private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
    private static final Map<String, String> EMPTY = new HashMap<>();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    private static NeatSnake snake;
	private static MultiLayerPerceptron brain;
	
	private StopWatch stopWatch = new StopWatch();
	
	public GameService() {
		brain = (MultiLayerPerceptron) NeuralNetwork.createFromFile("savednn.txt");
		for (double weight : brain.getWeights()) 
			LOG.info("Weight: " + weight);
	}
	
	/**
     * Generic processor that prints out the request and response from the methods.
     *
     * @param req request
     * @param res response
     * @return whatever
     */
    Map<String, String> process(Request req, Response res) {
        try {
        	stopWatch.start();
            String uri = req.uri();
            LOG.info("{} called with: {}", uri, req.body());
            Map<String, String> snakeResponse;
            switch (uri) {
                case "/ping":
                    snakeResponse = ping();
                    break;
                case "/start":
                    snakeResponse = start(JSON_MAPPER.readTree(req.body()));
                    break;
                case "/move":
                    snakeResponse = move(JSON_MAPPER.readTree(req.body()));
                    break;
                case "/end":
                    snakeResponse = end(JSON_MAPPER.readTree(req.body()));
                    break;
                default:
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
            }
            LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
            stopWatch.stop();
            return snakeResponse;
        } catch (Exception e) {
            LOG.warn("Something went wrong!", e);
            return null;
        }
    }

	/**
     * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
     * snake is still alive.
     *
     * @return an empty response.
     */
    private Map<String, String> ping() {
        return EMPTY;
    }

    /**
     * /start is called by the engine when a game is first run.
     *
     * @param startRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing the snake setup values.
     */
    public Map<String, String> start(JsonNode startRequest) {
        snake = new NeatSnake(ObjectCreator.createSnake(startRequest.get("you")));
        snake.setBrain(brain);

        Map<String, String> response = new HashMap<>();
        response.put("color", "#ff00ff");
        response.put("headType", "silly");
        response.put("tailType", "hook");
        return response;
    }

    /**
     * /move is called by the engine for each turn the snake has.
     *
     * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing snake movement values.
     */
    private Map<String, String> move(JsonNode moveRequest) {
    	JsonNode you = moveRequest.get("you");
    	
    	LinkedList<Field> body = ObjectCreator.createBody(you.get("body"));
    	int health = you.get("health").asInt();
    	
    	snake.setBody(body);
    	snake.setHealth(health);
    	Board board = ObjectCreator.createBoard(moveRequest.get("board"));
    	
    	Field nextMove = snake.evaluateNextMove(board);
    	
    	Map<String, String> response = new HashMap<>();
        response.put("move", nextMove.toString());
        return response;
    }

    /**
     * /end is called by the engine when a game is complete.
     *
     * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return responses back to the engine are ignored.
     */
    private Map<String, String> end(JsonNode endRequest) {
        return EMPTY;
    }
    
}
