/*	Author: Garrett Maitland
	Version: 0.8
	Date: November 10, 2019
*/
import java.util.ArrayList;

public class GameLogic {
	private GameState currentState;
	private CheckersAI ai;
	private CheckersFX game;
	private ArrayList<Action> validMoves;

	public GameLogic(CheckersFX game) {
		currentState = new GameState();
		ai = new CheckersAI();
		this.game = game;
		validMoves = currentState.possibleMoves();
	}

	public void newGame(String color) {
		currentState = new GameState();
		ai.newGame();

		if(color.equals("White")) {
			Action move = ai.nextMove();
			currentState = currentState.successorState(move);
			game.showMove(move);
		}
		validMoves = currentState.possibleMoves();
	}

	public boolean makeMove(Action move) {
		/*boolean isValidMove = false;
		System.out.println("Hello");

		for (Action v : validMoves) {
			System.out.println(v);
			System.out.println(v.equals(move));
			if (v.equals(move)) {
				isValidMove = true;
				move = v;
			}
		}

		if (isValidMove) {
			game.showMove(move);
			currentState = currentState.successorState(move);

			if (currentState.isTerminal()) {
				game.gameover("You win!");
				return true;
			}

			if (move.attack && !(currentState.checkForDoubleJumps(move).isEmpty())) {
				validMoves = currentState.checkForDoubleJumps(move);
				currentState.swapPlayers();
				return false;
			}*/

			game.showMove(move);
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

			validMoves = currentState.possibleMoves();
		//}
		currentState.printState();
		return true;
	}

	public ArrayList<Action> getValidMoves() {
		return currentState.possibleMoves();
	}

/*	public boolean isValidMove(Action move) {
		ArrayList<Action> validMoves = currentState.possibleMoves();
		for (Action v : validMoves) {
			if (v.equals(move))
				return true;
		}
		return false;
	} */
}