package io.battlesnake.neat;

public class InnovationNrGenerator {

	private static int innovatioNr = 0;
	
	public static void reset() {
		innovatioNr = 0;
	}
	
	public static int getNext() {
		++innovatioNr;
		return innovatioNr;
	}
}
