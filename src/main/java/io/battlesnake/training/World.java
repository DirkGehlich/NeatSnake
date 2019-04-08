package io.battlesnake.training;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
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
	final int BOARD_SIZE_X = 11;
	final int BOARD_SIZE_Y = 11;
	final int CHART_WIDTH = 1000;
	final int INFO_HEIGHT = 250;
	final int MARGIN = 50;
	final int FIELD_SIZE = 50;
	final int FRAME_WIDTH = 800;
	final int FRAME_HEIGHT = 600;
	final int MOVETIME_MS = 20;
	
	boolean worldRunning = false;
	
	Timer timer = null;
	EnemySnake enemySnake;
	private Population population = null;
	
	PlayArea playArea;
	JPanel container = new JPanel();
	
	private JFreeChart chart = null;
	private XYSeries seriesLifetime = null;
	private XYSeries seriesSnakeLength = null;
		
	public static void main(String[] args) {
		World world = new World();
		world.setVisible(true);
	}
	
	public World() {
		int width = BOARD_SIZE_X * FIELD_SIZE + MARGIN + CHART_WIDTH;
		int height = BOARD_SIZE_Y * FIELD_SIZE + MARGIN + INFO_HEIGHT;
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusable(true);
		addKeyListener(this);
		
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		playArea = new PlayArea();
		container.add(playArea);
		
		container.add(createChart());
		add(container);
		createEnemySnake();	
	}
	
	private ChartPanel createChart() {
		seriesLifetime = new XYSeries("# Moves");
		seriesSnakeLength = new XYSeries("Snake length");
		
		final XYSeriesCollection data = new XYSeriesCollection(seriesLifetime);
		data.addSeries(seriesSnakeLength);
		
		chart = ChartFactory.createXYLineChart("Highest Number moves per generation", "Generation", "# Moves", data, PlotOrientation.VERTICAL, true, true, false);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(900, 500));
		chartPanel.setMaximumSize(new Dimension(900, 500));
		
		return chartPanel;
	}
	
	private void createEnemySnake() {		
		LinkedList<io.battlesnake.world.Field> body = new LinkedList<io.battlesnake.world.Field>();
		body.add(new io.battlesnake.world.Field(9, 1));
		body.add(new io.battlesnake.world.Field(9, 2));
		body.add(new io.battlesnake.world.Field(9, 3));
		body.add(new io.battlesnake.world.Field(9, 4));
		body.add(new io.battlesnake.world.Field(9, 5));
		
		enemySnake = new EnemySnake(BOARD_SIZE_X, BOARD_SIZE_Y, body);
	}
	
	
	public void runWorld() {
		
		if (timer != null) {
			return;
		}
		
		if (population == null) {
			population = new Population(BOARD_SIZE_X, BOARD_SIZE_Y);
		}
		
		worldRunning = true;
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new MoveTask(this), 0, MOVETIME_MS);
	}
	
	protected void update() {
		playArea.update();
	}
		
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			population.setTraining(!population.isTraining());
		}		
		else if (arg0.getKeyChar() == 's') {
			population.getBestSnake().getBrain().save("savednn.txt");
		}		
		else if (arg0.getKeyChar() == 'l') {
			population = new Population(BOARD_SIZE_X, BOARD_SIZE_Y, "savednn.txt");
		}
		else if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			runWorld();
		}
	}
	
	public void moveEnemySnakeRandomly() {
		io.battlesnake.world.Field direction = getRandomDirection();
		
		int deadlockCnt = 0;
		while (deadlockCnt < 20 && enemySnake.wouldDie(direction)) {
			direction = getRandomDirection();
			deadlockCnt++;
		}
		
		if (deadlockCnt == 20) {
			createEnemySnake();
		}
		
		enemySnake.moveIn(direction);
		
	}
	
	private io.battlesnake.world.Field getRandomDirection() {
		int x = ThreadLocalRandom.current().nextInt(-1, 2);
		int y = ThreadLocalRandom.current().nextInt(-1, 2);
		
		return new io.battlesnake.world.Field(x, y);
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
			if (population.isPopulationDead()) {
				population.createNewGeneration();
				createEnemySnake();
			}
			else {
				population.moveSnakes(enemySnake.getBody());
			}						

			world.update();
		}
		
	}

	class PlayArea extends JPanel {		
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public PlayArea() {
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
			for (int rowIdx = 0; rowIdx < BOARD_SIZE_X; ++rowIdx) {
				for (int colIdx = 0; colIdx < BOARD_SIZE_Y; ++colIdx) {
					g.drawRect(MARGIN + rowIdx * FIELD_SIZE, MARGIN + colIdx * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
				}
			}

			if (!worldRunning) {
				return;
			}
				
			// Draw Food
//				for (GoodSnake snake : population.getSnakes()) {
//					
//					// Food
//					if (!snake.isDead() ) {
//						g.setColor(Color.BLUE);
//						for (Point food : snake.getFood()) {
//							g.fillRect(MARGIN + food.x * FIELD_SIZE, MARGIN + food.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
//						}
//						break;
//					}
//				}
			
			// Draw Snakes
			for (TrainingsSnake snake : population.getSnakes()) {
				if (!snake.isDead()) {
					if (snake.isDead()) {
						g.setColor(Color.GRAY);
					}
					else {
						g.setColor(Color.BLACK);
					}
					for (int i=1; i<snake.getBody().size(); ++i) {
						io.battlesnake.world.Field tail = snake.getBody().get(i);
						g.fillRect(MARGIN + tail.getX() * FIELD_SIZE, MARGIN + tail.getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
					}
					
					if (snake.isDead()) {
						g.setColor(Color.GRAY);
					}
					else {
						g.setColor(Color.GREEN);
					}
					g.fillRect(MARGIN + snake.getHeadPosition().getX() * FIELD_SIZE, MARGIN + snake.getHeadPosition().getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
					
//						break;
				}
			}
			
			
			// Draw Enemy Snake
			g.setColor(Color.BLACK);
			for (int i=1; i<enemySnake.getBody().size(); ++i) {
				io.battlesnake.world.Field tail = enemySnake.getBody().get(i);
				g.fillRect(MARGIN + tail.getX() * FIELD_SIZE, MARGIN + tail.getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
			}
				
			g.setColor(Color.RED);
			g.fillRect(MARGIN + enemySnake.getHeadPosition().getX() * FIELD_SIZE, MARGIN + enemySnake.getHeadPosition().getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
						
			// Draw Logging
			g.setColor(Color.BLACK);
			g.drawString("# Populations: " + Integer.toString(population.getGenerationNo()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN);
			
			TrainingsSnake bestSnake = population.getBestSnake();
			if (bestSnake != null) {
				g.drawString("Current Best Snakes Fitness: " + Double.toString(bestSnake.getFitness()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN + 20);
				g.drawString("Current Best Snakes Lifetime: " + Double.toString(population.getLocalMostMoves()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN + 40);
				g.drawString("Current Best Snakes Length: " + Double.toString(population.getLocalBiggestLength()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN + 60);
				g.drawString("Global Best Snakes Fitness: " + Double.toString(population.getGlobalBestFitness()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN + 80);
				g.drawString("Global Best Snakes Lifetime: " + Double.toString(population.getGlobalMostMoves()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN + 100);
				g.drawString("Global Best Snakes Length: " + Double.toString(population.getGlobalBiggestLength()), MARGIN, MARGIN + BOARD_SIZE_Y * FIELD_SIZE + MARGIN + 120);
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


