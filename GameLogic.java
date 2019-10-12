/*	Author: Garrett Maitland
	Version: 0.5
	Date: October 11, 2019
*/
import java.util.ArrayList;

public class GameLogic {
	private GameState currentState;
	private CheckersAI ai;

	public GameLogic() {
		currentState = new GameState();
		ai = new CheckersAI();
		ArrayList<Action> validMoves = currentState.possibleMoves();
		for (Action v : validMoves) {
			System.out.println(v.toString());
		}
	}

	public boolean makeMove(Action move) {
		ArrayList<Action> validMoves = null;
		if (isValidMove(move)) {
			CheckersFX.showMove(move);
			currentState = currentState.successorState(move);
			if (currentState.isTerminal()) {
				System.out.println("You win!");
				return true;
			}
			move = ai.nextMove(move);
			CheckersFX.showMove(move);
			currentState = currentState.successorState(move);
			validMoves = currentState.possibleMoves();
		for (Action v : validMoves) {
			System.out.println(v.toString());
		}
			return true;
		} else
			validMoves = currentState.possibleMoves();
		for (Action v : validMoves) {
			System.out.println(v.toString());
		}
			return false;
	}

	public boolean isValidMove(Action move) {
		ArrayList<Action> validMoves = currentState.possibleMoves();
		for (Action v : validMoves) {
			if (v.equals(move))
				return true;
		}
		return false;
	}
}