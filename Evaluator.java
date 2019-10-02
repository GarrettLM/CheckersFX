/*	Author: Garrett Maitland
	Version: 0.2
	Date: October 1, 2019
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