/*	Author: Garrett Maitland
	Version: 0.6
	Date: October 11, 2019
*/
import java.util.ArrayList;

public class GameLogic {
	private GameState currentState;
	private CheckersAI ai;
	private CheckersFX game;

	public GameLogic(CheckersFX game) {
		currentState = new GameState();
		ai = new CheckersAI();
		this.game = game;
	}

	public void newGame(String color) {
		currentState = new GameState();
		ai.newGame();

		if(color.equals("White")) {
			Action move = ai.nextMove();
			game.showMove(move);
			currentState = currentState.successorState(move);
		}
	}

	public boolean makeMove(Action move) {
		ArrayList<Action> validMoves = null;
		if (isValidMove(move)) {
			game.showMove(move);
			currentState = currentState.successorState(move);
			if (currentState.isTerminal()) {
				game.gameover("You win!");
				return true;
			}
			move = ai.nextMove(move);
			game.showMove(move);
			currentState = currentState.successorState(move);
			if (currentState.isTerminal())
				game.gameover("You lose!");

			return true;
		} else
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