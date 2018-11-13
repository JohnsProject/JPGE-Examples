package com.johnsproject.jpge.examples;

public class Main {

	public static void main(String[] args) {
		if (args[0].equals("1")) {
			new SimpleObjectViewer();
		}
		if (args[0].equals("2")) {
			new SpaceShipGame();
		}
		if (args[0].equals("-help") || args[0].equals("-h")) {
			String help = "Usage of commands: java -jar JPGE_Examples.jar <value> \n"
						+ "Run JPGE Examples: \n"
						+ "Simple object viewer: 1 \n"
						+ "Space ship game: 2";
			System.out.println(help);
		}
	}	
}
