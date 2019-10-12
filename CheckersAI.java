/*	Author: Garrett Maitland
	Version: 0.5
	Date: October 11, 2019
*/
import java.util.ArrayList;

public class CheckersAI {
	private GameState currentState;
	private Evaluator evaluator;
	public static final int DEPTH = 4;

	public CheckersAI() {
		currentState = new GameState();
		evaluator = new DumbEvaluator();
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
		currentState.printState();
		return optimalMove;
	}

	public int eval(GameState s) {
		return evaluator.evaluate(s);
	}

	public Action minimax() {
		ArrayList<Action> moves = currentState.possibleMoves();
		Action optimalMove = null;
		int max = Integer.MIN_VALUE;

		for (Action m : moves) {
			int temp = max(currentState.successorState(m), DEPTH);
			if (temp > max)
				optimalMove = m;
		}

		return optimalMove;
	}

	public int max(GameState state, int depth) {
		if (depth == 1 || state.isTerminal())
			return eval(state);

		int max = Integer.MIN_VALUE;
		ArrayList<Action> moves = state.possibleMoves();
		for (Action m : moves) {
			int temp = max(state.successorState(m), depth-1);
			if (temp > max)
				max = temp;
		}
		return max;
	}
}