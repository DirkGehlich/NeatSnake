package io.battlesnake.neat;


public class Parameters {

	public static float weightPerturbingChance = 0.9f;
	public static float weightPerturbingStep = 0.5f;
	public static float c1 = 1.0f;
	public static float c2 = 1.0f;
	public static float c3 = 0.4f;
	public static float compatibilityThreshold = 3.0f;
	public static int minNumberGenomesForBest = 1;
	public static float noCrossoverChange = 0.25f;
	public static float weightMutationChance = 0.8f;
	public static float weightMutationPower = 2.5f;
	public static float enableGeneMutationChance = 0.01f;
	public static float addNodeMutationChance = 0.02f;
	public static float deleteNodeMutationChance = 0.01f;
	public static float addConnectionMutationChance = 0.08f;
	public static float deleteConnectionMutationChance = 0.025f;
	public static float removeWeakestGenomesPercentage = 0.5f;
	public static int targetSpeciesCount = 10;
	public static float compatibilityThresholdChange = 0.3f;
	public static int populationSize = 150;
	public static float minWeight = -30.0f;
	public static float maxWeight = 30.0f;
	public static int maxStagnationCount = 15;
	
}
