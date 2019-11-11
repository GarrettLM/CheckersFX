/*	Author: Garrett Maitland
	Version: 0.5
	Date: October 11, 2019
*/
import java.util.Random;
import java.util.ArrayList;


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
class AdjacencyEvaluator extends Evaluator{
	private ArrayList<Piece> player;
	private ArrayList<Piece> opponent;

	public AdjacencyEvaluator() { super(); }

	public int evaluate(GameState s) {
		return playerEvaluate(s) - opponentEvaluate(s);
	}

	public int playerEvaluate(GameState s) {
		player = s.getPlayerPieces();
		int playerCount = 0;

		for(int i = 0; i <= player.size(); i++){
			if (doesPieceExist(player, player.get(i).getX() - 1 , player.get(i).getY() - 1)== true)
				playerCount++;
			if (doesPieceExist(player, player.get(i).getX() - 1 , player.get(i).getY() + 1) == true)
				playerCount++;
			if (doesPieceExist(player, player.get(i).getX() + 1 , player.get(i).getY() - 1) == true)
				playerCount++;
			if (doesPieceExist(player, player.get(i).getX() + 1 , player.get(i).getY() + 1) == true)
				playerCount++;
			return playerCount;
		}

	}
	public int opponentEvaluate(GameState s){
		opponent = s.getOpponentPieces();
		int opponentCount = 0;

		for(int i = 0; i <= player.size(); i++){
			if (doesPieceExist(opponent, opponent.get(i).getX() - 1 , opponent.get(i).getY() - 1) == true)
				opponentCount++;
			if (doesPieceExist(opponent, opponent.get(i).getX() - 1 , opponent.get(i).getY() + 1) == true)
				opponentCount++;
			if (doesPieceExist(opponent, opponent.get(i).getX() + 1 , opponent.get(i).getY() - 1) == true)
				opponentCount++;
			if (doesPieceExist(opponent, opponent.get(i).getX() + 1 , opponent.get(i).getY() + 1) == true)
				opponentCount++;
		}


		return opponentCount;
	}

	public boolean doesPieceExist(ArrayList<Piece> p, int x, int y){
		Piece temp = p.get(0).copy;
		temp.setCoord(x, y);

		return p.contains(temp);
	}

}