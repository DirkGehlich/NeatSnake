package io.battlesnake.training;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	private Population population;
	private EnemySnakes enemySnakes;
	private static final Logger LOG = LoggerFactory.getLogger(App.class.getName());

	public static void main(String[] args) {
		BasicConfigurator.configure();
		App app = new App();
		app.trainSnakes();		
	}

	public App() {
		population = new Population(Settings.BOARDSIZE_X, Settings.BOARDSIZE_Y);
		enemySnakes = new EnemySnakes();
	}

	public void trainSnakes() {

		while (true) {
			enemySnakes.moveRandomly();

			if (population.isPopulationDead()) {
				population.createNewGeneration();
				enemySnakes.reset();

				logBestSnakeStats();
				saveBestSnake();
			} else {
				population.moveSnakes(enemySnakes);
			}
		}
	}

	private void logBestSnakeStats() {
		if (population.getGenerationNo() % 10 == 0) {
			TrainingsSnake bestSnake = population.getBestSnake();
			if (bestSnake != null) {
				LOG.info("# Generation: {}", population.getGenerationNo());
				LOG.info("Lifetime: {}", bestSnake.getLifetime());
				LOG.info("Length: {}", bestSnake.getBody().size());
				LOG.info("Fitness: {}", bestSnake.getFitness());
			}
		}
	}

	public void saveBestSnake() {

		if (population.getGenerationNo() % 500 == 0) {
			population.getBestSnake().getBrain().saveToFile("_" + String.valueOf(population.getGenerationNo()));
		}
	}
}
