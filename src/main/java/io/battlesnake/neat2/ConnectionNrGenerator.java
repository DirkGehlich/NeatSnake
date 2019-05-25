package io.battlesnake.neat2;

public class ConnectionNrGenerator {

	private static int connectionNr = 0;

	public static void reset() {
		connectionNr = 0;
	}

	public static int getNext() {
		++connectionNr;
		return connectionNr;
	}

}
