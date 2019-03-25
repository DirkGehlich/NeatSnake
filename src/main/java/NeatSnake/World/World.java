package NeatSnake.World;

import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class World extends JFrame implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int BOARDSIZE = 11;
	final int MARGIN = 50;
	final int FIELD_SIZE = 50;
	final int FRAME_WIDTH = 800;
	final int FRAME_HEIGHT = 600;
	final int POPULATIONSIZE = 200;
	final int MOVETIME_MS = 25;
	
	boolean worldRunning = false;
	
	Timer timer = null;
	Snake enemySnake;
	private Population population = null;
	
	PlayArea playArea;
	JPanel container = new JPanel();
	
	private JFreeChart chart = null;
	private XYSeries seriesLifetime = null;
	private XYSeries seriesSnakeLength = null;
		
	public static void main(String[] args) {
		World world = new World();
		world.setVisible(true);
//		world.runWorld();
	}
	
	public World() {
		int playAreaSize = BOARDSIZE * FIELD_SIZE + MARGIN;
		setSize(playAreaSize + 1000, playAreaSize + 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusable(true);
		addKeyListener(this);
		
		
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		playArea = new PlayArea(BOARDSIZE, FIELD_SIZE, MARGIN);
		container.add(playArea);
		
		seriesLifetime = new XYSeries("# Moves");
		seriesSnakeLength = new XYSeries("Snake length");
//		series.add(1.0, 400);
//		series.add(2.0, 200);
		
		final XYSeriesCollection data = new XYSeriesCollection(seriesLifetime);
		data.addSeries(seriesSnakeLength);
		
		chart = ChartFactory.createXYLineChart("Highest Number moves per generation", "Generation", "# Moves", data, PlotOrientation.VERTICAL, true, true, false);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 500));
		chartPanel.setMaximumSize(new Dimension(900, 500));
		
		container.add(chartPanel);
		add(container);
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
		
		if (timer != null) {
			return;
		}
		
		if (population == null) {
			population = new Population(POPULATIONSIZE, BOARDSIZE);
		}
		
		worldRunning = true;
		timer = new Timer(true);
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
			population.setTraining(!population.isTraining());
		}		
		else if (arg0.getKeyChar() == 's') {
			population.getBestSnake().brain.save("savednn.txt");
		}		
		else if (arg0.getKeyChar() == 'l') {
			population = new Population("savednn.txt", POPULATIONSIZE, BOARDSIZE);
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			runWorld();
		}
	}
	
	public void moveEnemySnakeRandomly() {
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
			population.moveSnakes(getEnemySnakesCoords());		

			world.update();
		}
		
	}

	class PlayArea extends JPanel {		
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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

			if (!worldRunning) {
				return;
			}
				
			if (population.isTraining()) {
				// Draw Food
	//			for (GoodSnake snake : population.getSnakes()) {
	//				
	//				// Food
	//				g.setColor(Color.BLUE);
	//				for (Point food : snake.getFood()) {
	//					g.fillRect(MARGIN + food.x * FIELD_SIZE, MARGIN + food.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
	//				}
	//			}
				
				// Draw Snakes
				for (GoodSnake snake : population.getSnakes()) {
					if (!snake.isDead()) {
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
				}
			}
			else {
				GoodSnake bestSnake = population.getDemonstrationSnake();
				
				// Draw Food				
				g.setColor(Color.BLUE);
				for (Point food : bestSnake.getFood()) {
					g.fillRect(MARGIN + food.x * FIELD_SIZE, MARGIN + food.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
				}
					
				if (bestSnake.isDead()) {
					g.setColor(Color.GRAY);
				}
				else {
					g.setColor(Color.BLACK);
				}
				
				for (Point tail : bestSnake.tail) {
					g.fillRect(MARGIN + tail.x * FIELD_SIZE, MARGIN + tail.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
				}
					
				if (bestSnake.isDead()) {
					g.setColor(Color.GRAY);
				}
				else {
					g.setColor(Color.GREEN);
				}
				g.fillRect(MARGIN + bestSnake.head.x * FIELD_SIZE, MARGIN + bestSnake.head.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
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
			g.drawString("# Populations: " + Integer.toString(population.getGenerationNo()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN);
			
			GoodSnake bestSnake = population.getBestSnake();
			if (bestSnake != null) {
				g.drawString("Current Best Snakes Fitness: " + Double.toString(bestSnake.getFitness()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN + 20);
				g.drawString("Current Best Snakes Length: " + Double.toString(bestSnake.tail.size() + 1), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN + 40);
				g.drawString("Current Best Snakes Lifetime: " + Double.toString(bestSnake.getScore()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN + 60);
				g.drawString("Global Best Snakes Moves: " + Double.toString(population.getGlobalMostMoves()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN + 80);
				g.drawString("Global Best Snakes Fitness: " + Double.toString(population.getGlobalBestFitness()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN + 100);
				g.drawString("Global Best Snakes Length: " + Double.toString(population.getGlobalBiggestLength()), MARGIN, MARGIN + BOARDSIZE * FIELD_SIZE + MARGIN + 120);
			}
			
			if (bestSnake != null) {
				if (population.getLifetimeHistory().size() > seriesLifetime.getItemCount()) {
					seriesLifetime.add(seriesLifetime.getItemCount() + 1, population.getLifetimeHistory().get(population.getLifetimeHistory().size()-1));
				}
				
				if (population.getSnakeLengthHistory().size() > seriesSnakeLength.getItemCount()) {
					seriesSnakeLength.add(seriesSnakeLength.getItemCount() + 1, population.getSnakeLengthHistory().get(population.getSnakeLengthHistory().size()-1));
				}
			}
		}
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}


