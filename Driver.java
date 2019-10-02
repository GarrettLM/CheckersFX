/*	Author: Garrett Maitland
	Version: 0.2
	Date: October 1, 2019
*/
import java.lang.Thread;

public class Driver {
	public static void main(String[] args) {
		CheckersAI black = new CheckersAI();
		CheckersAI white = new CheckersAI();
		Action move = black.nextMove();
		System.out.println();
		for (int i = 0; i < 10; i++) {
			move = white.nextMove(move);
			System.out.println();
/*			try {
            	// thread to sleep for 10 seconds
            	Thread.sleep(10000);
         	} catch (Exception e) {
            	System.out.println(e);
         	}*/
			move = black.nextMove(move);
			System.out.println();
		}
	}
}