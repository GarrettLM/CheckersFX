/*	Author: Garrett Maitland
	Version: 0.6
	Date: October 12, 2019
*/
import java.util.ArrayList;
import java.util.Random;

public class CheckersAI {
	private GameState currentState;
	private Evaluator evaluator;
	private Random rand = new Random();
	public static final int DEPTH = 6;

	public CheckersAI() {
		currentState = new GameState();
		evaluator = new SimpleEvaluator();
	}

	public void newGame() {
		currentState = new GameState();
	}

	public Action nextMove() {
		Action optimalMove = minimax();
		currentState = currentState.successorState(optimalMove);
		return optimalMove;
	}

	public Action nextMove(Action opponentsMove) {
		currentState = currentState.successorState(opponentsMove);
		//currentState.printState();
		Action optimalMove = minimax();
		currentState = currentState.successorState(optimalMove);
		return optimalMove;
	}

	public int eval(GameState s) {
		return evaluator.evaluate(s);
	}

	public Action minimax() {
		ArrayList<Action> moves = currentState.possibleMoves();
		Action optimalMove = null;
		int min = Integer.MAX_VALUE;

		for (Action m : moves) {
			int temp = min(currentState.successorState(m), DEPTH);
			if ((temp + rand.nextDouble()) < (min + rand.nextDouble())) {
				min = temp;
				optimalMove = m;
			}
		}

		return optimalMove;
	}

	public int max(GameState state, int depth) {
		if (depth == 1 || state.isTerminal())
			return eval(state);

		int max = Integer.MIN_VALUE;
		ArrayList<Action> moves = state.possibleMoves();
		for (Action m : moves) {
			int temp = min(state.successorState(m), depth-1);
			if (temp > max)
				max = temp;
		}
		return max;
	}

	public int min(GameState state, int depth) {
		if (depth == 1 || state.isTerminal())
			return eval(state);

		int min = Integer.MAX_VALUE;
		ArrayList<Action> moves = state.possibleMoves();
		for (Action m : moves) {
			int temp = max(state.successorState(m), depth-1);
			if (temp < min)
				min = temp;
		}
		return min;
	}
}