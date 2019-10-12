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