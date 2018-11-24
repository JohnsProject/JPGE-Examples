package com.johnsproject.jpge.examples;

public class Main {

	public static int W_WIDTH = 640;
	public static int W_HEIGHT = 640;
	public static int R_WIDTH = 640;
	public static int R_HEIGHT = 640;
	
	private static final String help = ""
			+ "Usage of commands: java -jar JPGE_Examples.jar <value> \n"
			+ "Run JPGE Examples: \n"
			+ "Simple object viewer: -1 \n"
			+ "Space ship game: -2 \n"
			+ "Window Resolution: -WR width x height \n"
			+ "Rendering Resolution: -RR width x height \n";
	
	public static void main(String[] args) {
		args = new String[] {"-2"};
		if (args.length < 1) {
			System.out.println(help);
			return;
		}
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replaceAll(" ", "");
			if (args[i].equals("-help")) {
				System.out.println(help);
			}
			if (args[i].contains("-WR")) {
				String content = args[i].replaceAll("-WR", "");
				String[] resolution = content.split("x");
				W_WIDTH = Integer.parseInt(resolution[0]);
				W_HEIGHT = Integer.parseInt(resolution[1]);
			}
			if (args[i].contains("-RR")) {
				String content = args[i].replaceAll("-RR", "");
				String[] resolution = content.split("x");
				R_WIDTH = Integer.parseInt(resolution[0]);
				R_HEIGHT = Integer.parseInt(resolution[1]);
			}
			if (args[i].equals("-1")) {
				new SimpleObjectViewer();
			}
			if (args[i].equals("-2")) {
				new SpaceShipGame();
			}
		}
	}	
}
