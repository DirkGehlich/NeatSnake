package NeatSnake.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class World extends JFrame implements KeyListener {

	final int BOARDSIZE = 11;
	final int MARGIN = 50;
	final int FIELD_SIZE = 50;
	final int FRAME_WIDTH = 800;
	final int FRAME_HEIGHT = 600;
	final int POPULATIONSIZE = 100;
	final int MOVETIME_MS = 100;
	
	Snake enemySnake;
	private Population population = new Population(POPULATIONSIZE, BOARDSIZE);
	List<Point> food = new ArrayList<Point>();
	
	PlayArea playArea;
	
	public static void main(String[] args) {
		World world = new World();
		world.setVisible(true);
		world.runWorld();
	}
	
	public World() {
		int playAreaSize = BOARDSIZE * FIELD_SIZE + MARGIN;
		setSize(playAreaSize + 500, playAreaSize + 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusable(true);
		addKeyListener(this);
		playArea = new PlayArea(BOARDSIZE, FIELD_SIZE, MARGIN);
		add(playArea);
		enemySnake = createEnemySnake();	
	}
	
	private Snake createEnemySnake() {
		Snake snake = new Snake();
		snake.head = new Point(9, 6);
		snake.tail.add(new Point(9, 1));
		snake.tail.add(new Point(9, 2));
		snake.tail.add(new Point(9, 3));
		snake.tail.add(new Point(9, 4));
		snake.tail.add(new Point(9, 5));
		
		return snake;
	}
	
	
	public void runWorld() {
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new MoveTask(this), 0, MOVETIME_MS);
	}
	
	protected void update() {
		playArea.update();
	}
	
	private List<Point> getEnemySnakesCoords() {
		List<Point> snakeCoords = new ArrayList<Point>();
		
		// TODO: replace by snake array
		snakeCoords.add(enemySnake.head);
		snakeCoords.addAll(enemySnake.tail);
		
		return snakeCoords;
	}
	
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			
			moveEnemySnakeRandomly();
		}		
	}
	
	public void moveEnemySnakeRandomly() {
		playArea.logMsg = "";
		
		Point direction = getRandomDirection();
		
		int deadlockCnt = 0;
		while (deadlockCnt < 20 && enemySnake.wouldDie(direction, BOARDSIZE)) {
			direction = getRandomDirection();
			deadlockCnt++;
		}
		
		if (deadlockCnt == 20) {
			enemySnake = createEnemySnake();
		}
		
		enemySnake.move(direction);
		playArea.logMsg += "Direction: x=" + direction.x + " - y=" + direction.y; 
		
	}
	
	private Point getRandomDirection() {
		int x = ThreadLocalRandom.current().nextInt(-1, 2);
		int y = ThreadLocalRandom.current().nextInt(-1, 2);
		
		return new Point(x, y);
	}
	
	class MoveTask extends TimerTask {

		private World world;
		
		public MoveTask(World world) {
			this.world = world;
		}
		
		@Override
		public void run() {
			world.moveEnemySnakeRandomly();
			
			// TODO: pass population as parameter to ctor?
			population.moveSnakes(getEnemySnakesCoords(), food);		

			world.update();
		}
		
	}

	class PlayArea extends JPanel {		
	
		int boardSize;
		int fieldSize;
		int margin;
		
		String logMsg = new String(); 
		
		public PlayArea(int boardSize, int fieldSize, int margin) {
			this.boardSize = boardSize;
			this.fieldSize = fieldSize;
			this.margin = margin;
		}
		
		public void update() {
			validate();
			repaint();
		}
		
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// Draw Board
			g.setColor(Color.BLACK);
			for (int rowIdx = 0; rowIdx < BOARDSIZE; ++rowIdx) {
				for (int colIdx = 0; colIdx < BOARDSIZE; ++colIdx) {
					g.drawRect(MARGIN + rowIdx * fieldSize, MARGIN + colIdx * fieldSize, fieldSize, fieldSize);
				}
			}

			// Draw Snakes
			for (GoodSnake snake : population.getSnakes()) {
				
				// Food
				g.setColor(Color.BLUE);
				g.fillRect(MARGIN + snake.getFood().x * FIELD_SIZE, MARGIN + snake.getFood().y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
			}
				// Draw Snakes
			for (GoodSnake snake : population.getSnakes()) {
				if (snake.isDead()) {
					g.setColor(Color.GRAY);
				}
				else {
					g.setColor(Color.BLACK);
				}
				for (Point tail : snake.tail) {
					g.fillRect(MARGIN + tail.x * FIELD_SIZE, MARGIN + tail.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
				}
				
				if (snake.isDead()) {
					g.setColor(Color.GRAY);
				}
				else {
					g.setColor(Color.GREEN);
				}
				g.fillRect(MARGIN + snake.head.x * FIELD_SIZE, MARGIN + snake.head.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
			}
			
			// Draw Enemy Snake
			g.setColor(Color.BLACK);
			for (Point tail : enemySnake.tail) {
				g.fillRect(MARGIN + tail.x * FIELD_SIZE, MARGIN + tail.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
			}
				
			g.setColor(Color.RED);
			g.fillRect(MARGIN + enemySnake.head.x * FIELD_SIZE, MARGIN + enemySnake.head.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
						
			// Draw Logging
			g.setColor(Color.BLACK);
			g.drawString(Integer.toString(population.getGenerationNo()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN);
		}
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}


