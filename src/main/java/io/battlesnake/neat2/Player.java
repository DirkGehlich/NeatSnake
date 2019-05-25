package io.battlesnake.neat2;

public class Player {
//	Pacman pacman;
	double fitness;
	Genome brain;
	double[] vision = new double[8];// the input array fed into the neuralNet
	double[] decision = new double[4]; // the out put of the NN
	double unadjustedFitness;
	int lifespan = 0;// how long the player lived for fitness
	int bestScore = 0;// stores the score achieved used for replay
	boolean dead;
	int score;
	int gen = 0;
	int stage = 1; // used for gen shit
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	// constructor

	Player() {
	}
	
	Player(int numInputs, int numOutputs) {
//		pacman = new Pacman();
		brain = new Genome(numInputs, numOutputs);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	void show() {
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 31; j++) {
//				pacman.tiles[j][i].show();
			}
		}

//		pacman.blinky.show();
//		pacman.pinky.show();
//		pacman.inky.show();
//		pacman.clyde.show();
//		pacman.show();
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	void move() {
//		pacman.move();
//		pacman.blinky.move();
//		pacman.pinky.move();
//		pacman.inky.move();
//		pacman.clyde.move();
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	void update() {
		move();
		checkGameState();
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	void checkGameState() {
//		if (pacman.gameOver) {
//			dead = true;
//		}
//		score = pacman.score;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------

	void look() {

//		if (isCriticalPosition(pacman.pos)) {
//
//			// so how this works
//			// get the 'danger' of going in that direciton by finding the nearest ghost in
//			// that direction and inversing the distance to it
//			// also this is assuming that this is called every time pacman is at a critical
//			// point
//
//			vision = new float[13];
//			distanceToGhostInDirection();
//			setDistanceToWalls();
//			vision[vision.length - 1] = (pacman.blinky.frightened) ? 1 : 0;
//		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------------

	

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	// gets the output of the brain then converts them to actions
	void think() {

		double max = 0;
		int maxIndex = 0;
		// get the output of the neural network
		decision = brain.feedForward(vision);

		for (int i = 0; i < decision.length; i++) {
			if (decision[i] > max) {
				max = decision[i];
				maxIndex = i;
			}
		}

		if (max < 0.8) {// if the max output was less than 0.8 then do nothing
			return;
		}
//		PVector currentVel = new PVector(pacman.vel.x, pacman.vel.y);
//
//		currentVel.rotate((PI / 2) * maxIndex);
//		currentVel.x = round(currentVel.x);
//		currentVel.y = round(currentVel.y);
//		pacman.turnTo = new PVector(currentVel.x, currentVel.y);
//		pacman.turn = true;

	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	// returns a clone of this player with the same brian
	public Player clone() {
		Player clone = new Player();
		clone.brain = brain.clone();
		clone.fitness = fitness;
		clone.brain.generateNetwork();
		clone.gen = gen;
		clone.bestScore = score;
		return clone;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// since there is some randomness in pacman (when the ghosts are frightened they
	// move randomly) sometimes when we want to replay the game then we need to
	// remove that randomness
	// this fuction does that

	Player cloneForReplay() {
		Player clone = new Player();
		clone.brain = brain.clone();
		clone.fitness = fitness;
		clone.unadjustedFitness = unadjustedFitness;
		clone.brain.generateNetwork();
//		clone.pacman.blinky.frightenedTurns = (ArrayList) pacman.blinky.frightenedTurns.clone();
//		clone.pacman.blinky.replay = true;
//		clone.pacman.pinky.frightenedTurns = (ArrayList) pacman.pinky.frightenedTurns.clone();
//		clone.pacman.pinky.replay = true;
//		clone.pacman.inky.frightenedTurns = (ArrayList) pacman.inky.frightenedTurns.clone();
//		clone.pacman.inky.replay = true;
//		clone.pacman.clyde.frightenedTurns = (ArrayList) pacman.clyde.frightenedTurns.clone();
//		clone.pacman.clyde.replay = true;
//		clone.pacman.replay = true;
		clone.gen = gen;
		clone.bestScore = score;
		clone.stage = stage;
		return clone;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	// fot Genetic algorithm
	void calculateFitness() {
//		score = pacman.score;
		bestScore = score;
//		lifespan = pacman.lifespan;
		// TODO: fix this method
		//fitness = score * score;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	Player crossover(Player parent2) {
		Player child = new Player();
		child.brain = brain.crossover(parent2.brain);
		child.brain.generateNetwork();
		return child;
	}
}
