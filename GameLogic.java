/*	Author: Garrett Maitland
	Version: 0.9
	Date: November 20, 2019
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

		if (color.equals("White")) {
			Action move = ai.nextMove();
			currentState = currentState.successorState(move);
			game.showMove(move);
		}

		game.getAvailableMoves();
	}

	public boolean makeMove(Action move) {
		currentState = currentState.successorState(move);

		if (currentState.isTerminal()) {
			game.gameover("You win!");
			return true;
		}

		move = ai.nextMove(move);
		currentState = currentState.successorState(move);
		game.showMove(move);

		if (currentState.isTerminal())
			game.gameover("You lose!");

		currentState.printState();
		return true;
	}

	public ArrayList<Action> getValidMoves() {
		return currentState.possibleMoves();
	}

	public boolean isValidMove(Action move) {
		return currentState.possibleMoves().contains(move);
	}
}