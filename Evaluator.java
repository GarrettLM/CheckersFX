/*	Author: Garrett Maitland
	Version: 0.5
	Date: October 11, 2019
*/
import java.util.Random;

public abstract class Evaluator {
	public Evaluator() {}

	public abstract int evaluate(GameState s);
}

class DumbEvaluator extends Evaluator {
	private Random rand;

	public DumbEvaluator() {
		super();
		rand = new Random();
	}

	public int evaluate(GameState s) {
		return rand.nextInt();
	}
}

class SimpleEvaluator extends Evaluator {
	public SimpleEvaluator() {
		super();
	}

	public int evaluate(GameState s) {
		return s.getPlayersScore() - s.getOpponentsScore();
	}
}
/*
 Class AdjacencyEvaluator
 used to check for adjacent pieces of both player and opponnent.
 */
class AdjacencyEvaluator extends Evaluator {
	public int playerEvaluate(GameState s) {
		int playerCount = 0;


		if (s.playerPieces[(get.x) - 1][(get.y) - 1] == true)
			playerCount++;
		if (s.playerPieces[(get.x) - 1][(get.y) + 1] == true)
			playerCount++;
		if (s.playerPieces[(get.x) + 1][(get.y) - 1] == true)
			playerCount++;
		if (s.playerPieces[(get.x) + 1][(get.y) + 1] == true)
			playerCount++;
		return playerCount;
	}
	public int opponentEvaluate(GameState s){
		int opponentCount = 0;

		if (s.opponentPieces[(get.x)-1][(get.y)-1])
			opponentCount++;
		if (s.opponentPieces[(get.x)-1][(get.y)+1])
			opponentCount++;
		if (s.opponentPieces[(get.x)+1][(get.y)-1])
			opponentCount++;
		if (s.opponentPieces[(get.x)+1][(get.y)+1])
			opponentCount++;
		return opponentCount;
	}

}