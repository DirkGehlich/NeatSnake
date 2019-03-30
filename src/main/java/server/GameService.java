package server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;

import NeatSnake.World.GoodSnake;

@Path( GameService.webContextPath )
public class GameService {
	
	static GoodSnake snake;
	static MultiLayerPerceptron brain;
	static private int boardSize;

	static final String webContextPath = "";
	
	public GameService() {
		brain = (MultiLayerPerceptron) NeuralNetwork.createFromFile("savednn.txt");
		
	}

	@Path("/start")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startGame(GameInfo  gameInfo) {
		
		Point startingPosition = new Point();
		startingPosition.x = gameInfo.getYou().getBody().get(0).getX();
		startingPosition.y = gameInfo.getYou().getBody().get(0).getY();
		boardSize = gameInfo.getBoard().getHeight();
		
		List<Point> tail = getTail(gameInfo.getYou().getBody());
		
		snake = new GoodSnake(boardSize, startingPosition, tail, brain);
		
		JSONObject response = new JSONObject();
		response.put("color", "#ff00ff");
		response.put("headType", "bendr");
		response.put("tailType", "pixel");
		String jsonResponse = response.toString();
		
		return Response.ok(jsonResponse).build();
	}

	private List<Point> getTail(List<Position> body) {
		
		List<Point> tailPositions = new ArrayList<Point>();
		
		for (Position pos : body) {
			tailPositions.add(translateToPoint(pos));
		}
		
		return tailPositions;
	}
	
	private List<Point> getSnakes(List<Snake> snakes) {
		
		List<Point> snakePositions = new ArrayList<Point>();
		
		for (Snake s : snakes) {
			for (Position pos : s.getBody()) {
				snakePositions.add(translateToPoint(pos));
			}
		}
		
		return snakePositions;
	}
	
	private String getDirection(Point direction) {
		if (direction.x == 0 && direction.y == 1)
			return "down";
		else if (direction.x == 1 && direction.y == 0)
			return "right";
		else if (direction.x == -1 && direction.y == 0)
			return "left";
		else if (direction.x == 0 && direction.y == -1)
			return "up";
		else
			return "";		
	}

	private List<Point> getFood(List<Position> food) {
		
		List<Point> foodPositions = new ArrayList<Point>();
		
		for (Position f : food) {
			foodPositions.add(translateToPoint(f));
		}
		
		return foodPositions;
	}

	private Point translateToPoint(Position pos) {
		
		Point point = new Point(pos.getX(), pos.getY());
		
		return point;
	}
	
	@Path("/move")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response moveSnake(GameInfo gameInfo) {

		List<Point> foodPos = getFood(gameInfo.getBoard().getFood());
		List<Point> snakePos = getSnakes(gameInfo.getBoard().getSnakes());
		Point headPos = translateToPoint(gameInfo.getYou().getBody().get(0));
		
		snake.setHealth(gameInfo.getYou().getHealth());
		snake.setFood(foodPos);
		snake.setHead(headPos);
		snake.think(boardSize, snakePos);
		
		Point direction = snake.getDirection();
		
		
		
		JSONObject response = new JSONObject();
		response.put("move", getDirection(direction));
		String jsonResponse = response.toString();
		
		return Response.ok(jsonResponse).build();
	}

	@Path("/end")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response endGame(GameInfo gameInfo) {

		return Response.ok().build();
	}
	
	@Path("/ping")
	@POST
	public Response ping() {
				
		return Response.ok().build();
	}
}
