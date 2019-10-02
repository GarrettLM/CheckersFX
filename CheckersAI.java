/*	Author: Garrett Maitland
	Version: 0.2
	Date: October 1, 2019
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;
import javafx.scene.paint.Color;

public class CheckersAI {
	protected GameState currentState;
	private static Evaluator evaluator;

	public CheckersAI() {
		currentState = new GameState();
		evaluator = new DumbEvaluator();
	}

	public Action nextMove() {
		Action optimalMove = currentState.minimax();
		currentState = currentState.successorState(optimalMove);
		return optimalMove;
	}

	public Action nextMove(String opponentsMove) {
		currentState = currentState.successorState(new Action(opponentsMove));
		//currentState.printState();
		Action optimalMove = currentState.minimax();
		currentState = currentState.successorState(optimalMove);
		currentState.printState();
		return optimalMove;
	}

	public Action nextMove(Action opponentsMove) {
		currentState = currentState.successorState(opponentsMove);
		//currentState.printState();
		Action optimalMove = currentState.minimax();
		currentState = currentState.successorState(optimalMove);
		currentState.printState();
		return optimalMove;
	}

	public static int eval(GameState s) {
		return evaluator.evaluate(s);
	}
}

class GameState implements Comparable<GameState> {
	private int value;
	private ArrayList<GameState> successorStates;
	private Piece[][] board;
	private ArrayList<Piece> playersPieces;
	private ArrayList<Piece> opponentsPieces;
	public final Action action;	//move that lead to the current state
	public static final int DEPTH = 6;

	public GameState() {
		value = 0;
		successorStates = new ArrayList<GameState>();
		playersPieces = new ArrayList<Piece>();
		opponentsPieces = new ArrayList<Piece>();
		board = new Piece[8][8];

		//i is the row (y-coordinate) and j is the column (x-coordinate)
		for (int i = 0; i < 3; i++) {
			if (i % 2 == 0) {
				for (int j = 1; j < 8; j += 2) {
					Piece temp = new Piece("black", Color.BLACK, false, j, i);
					board[i][j] = temp;
					playersPieces.add(temp);
				}
			} else {
				for (int j = 0; j < 8; j += 2) {
					Piece temp = new Piece("black", Color.BLACK, false, j, i);
					board[i][j] = temp;
					playersPieces.add(temp);
				}
			}
		}

		for (int i = 5; i < 8; i++) {
			if (i % 2 == 0) {
				for (int j = 1; j < 8; j += 2) {
					Piece temp = new Piece("white", Color.WHITE, false, j, i);
					board[i][j] = temp;
					opponentsPieces.add(temp);
				}
			} else {
				for (int j = 0; j < 8; j += 2) {
					Piece temp = new Piece("white", Color.WHITE, false, j, i);
					board[i][j] = temp;
					opponentsPieces.add(temp);
				}
			}
		}

		action = null;
	}

	public GameState(GameState prevState, Action a) {
		action = a;
		value = 0;
		successorStates = new ArrayList<GameState>();
		board = new Piece[8][8];
		playersPieces = prevState.cloneOpponentsPieces();
		opponentsPieces = prevState.clonePlayersPieces();

		for (Piece p : playersPieces) {
			board[p.getY()][p.getX()] = p;
		}
		for (Piece p : opponentsPieces) {
			board[p.getY()][p.getX()] = p;
		}

		Piece movedPiece = board[a.startY][a.startX];
		board[a.startY][a.startX] = null;
		board[a.destY][a.destX] = movedPiece;
		movedPiece.setCoord(a.destX, a.destY);

		if (a.attack) {
			Piece attackedPiece = board[a.atkY][a.atkX];
			playersPieces.remove(attackedPiece);
			board[a.atkY][a.atkX] = null;
		}

	}

	public ArrayList<Piece> clonePlayersPieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (Piece p : playersPieces) {
			pieces.add(p.clone());
		}
		return pieces;
	}

	public ArrayList<Piece> cloneOpponentsPieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (Piece p : opponentsPieces) {
			pieces.add(p.clone());
		}
		return pieces;
	}

	public GameState successorState(Action move) {
/*		if (successorStates.isEmpty()) {
			ArrayList<Action> moves = possibleMoves();
			for (Action m : moves) {
				successorStates.add(new GameState(this, m));
			}
		}*/

		for (GameState s : successorStates) {
			if (s.action.equals(move))
				return s;
		}
		return new GameState(this, move);
	}

	public ArrayList<Action> possibleMoves() {
		ArrayList<Action> actions = new ArrayList<Action>();
		boolean forcedJump = false;

		for (Piece p : playersPieces) {
			int x = p.getX();
			int y = p.getY();
			if ((p.isKing() || p.isBlack()) && (x > 2 && y < 6) && board[y+1][x-1] != null && (board[y+1][x-1].isWhite() != p.isWhite()) && board[y+2][x-2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				actions.add(new Action(x, y, x-2, y+2, x-1, y+1));
			}
			if ((p.isKing() || p.isBlack()) && (x < 6 && y < 6) && board[y+1][x+1] != null && (board[y+1][x+1].isWhite() != p.isWhite()) && board[y+2][x+2] == null){
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				actions.add(new Action(x, y, x+2, y+2, x+1, y+1));
			}
			if ((p.isKing() || p.isWhite()) && (x > 2 && y > 2) && board[y-1][x-1] != null && (board[y-1][x-1].isWhite() != p.isWhite()) && board[y-2][x-2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				actions.add(new Action(x, y, x-2, y-2, x-1, y-1));
			}
			if ((p.isKing() || p.isWhite()) && (x < 6 && y > 2) && board[y-1][x+1] != null && (board[y-1][x+1].isWhite() != p.isWhite()) && board[y-2][x+2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				actions.add(new Action(x, y, x+2, y-2, x+1, y-1));
			}
			if (!forcedJump) {
				if ((p.isKing() || p.isBlack()) && (y < 7 && x > 0) && board[y+1][x-1] == null) {
					Action temp = new Action(x, y, x-1, y+1);
					actions.add(temp);
				}
				if ((p.isKing() || p.isBlack()) && (y < 7 && x < 7) && board[y+1][x+1] == null) {
					Action temp = new Action(x, y, x+1, y+1);
					actions.add(temp);
				}
				if ((p.isKing() || p.isWhite()) && (y > 0 && x > 0) && board[y-1][x-1] == null) {
					Action temp = new Action(x, y, x-1, y-1);
					actions.add(temp);
				}
				if ((p.isKing() || p.isWhite()) && (y > 0 && x < 7) && board[y-1][x+1] == null) {
					Action temp = new Action(x, y, x+1, y-1);
					actions.add(temp);
				}
			}
		}
		return actions;
	}

	public Action minimax() {
		max(DEPTH);
		return successorStates.get(0).action;
	}

	public int min(int depth) {
		//System.out.println("Hello from min! " + depth);
		if (depth == 1 || playersPieces.isEmpty() || opponentsPieces.isEmpty()) {
			value = CheckersAI.eval(this);
			return value;
		}

		if (successorStates.isEmpty()) {
			ArrayList<Action> moves = possibleMoves();
			for (Action move : moves) {
				//System.out.println("Just added " + move.toString() + " to successor States");
				successorStates.add(new GameState(this, move));
			}
		}

		for (GameState s : successorStates) {
			s.min(depth-1);
		}
		Collections.sort(successorStates);	//Sorts the successorStates in ascending order
		value = successorStates.get(0).getValue();	//Sets value to min value of the successor states
		return value;	//Returns the first element from successorStates
	}

	public int max(int depth) {
		//System.out.println("Hello from max! " + depth);
		if (depth == 1 || playersPieces.isEmpty() || opponentsPieces.isEmpty()) {
			value = CheckersAI.eval(this);
			return value;
		}

		if (successorStates.isEmpty()) {
			ArrayList<Action> moves = possibleMoves();

			for (Action move : moves) {
				//System.out.println("Just added " + move.toString() + " to successor States");
				successorStates.add(new GameState(this, move));
			}
		}

		for (GameState s : successorStates) {
			if (s.min(depth-1) > value)
				value = s.getValue();
		}
		Collections.sort(successorStates);	//Sorts the successorStates in ascending order
		Collections.reverse(successorStates);	//successorStates is now sorted in descending order
		return value;
	}

	public int utility() {
		value = CheckersAI.eval(this);
		return value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int v) {
		value = v;
	}

	public void printState() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == null) {
					System.out.print("|_");
				} else {
					if (board[i][j].isBlack()) {
						System.out.print("|b");
					} else {
						System.out.print("|w");
					}
				}
			}
			System.out.println("|");
		}
	}

	public int compareTo(GameState s) {
		if (((GameState)s).getValue() > value) {
			return -1;
		} else if (((GameState)s).getValue() < value) {
			return 1;
		} else {
			return 0;
		}
	}
}

class Action {
	public final int start;
	public final int destination;
	public final boolean attack;
	public final int startX;
	public final int startY;
	public final int destX;
	public final int destY;
	public final int atkX;
	public final int atkY;

/*	public Action(int s, int d, boolean a) {
		start = s;
		destination = d;
		attack = a;
	}*/

	public Action(int sx, int sy, int dx, int dy) {
		startX = sx;
		startY = sy;
		destX = dx;
		destY = dy;
		attack = false;
		atkX = -1;
		atkY = -1;
		start = ((sy*8 + sx-1)/2);
		destination = ((dy*8 + dx-1)/2);
	}

	public Action(int sx, int sy, int dx, int dy, int ax, int ay) {
		startX = sx;
		startY = sy;
		destX = dx;
		destY = dy;
		attack = true;
		atkX = ax;
		atkY = ay;
		start = ((sy*8 + sx-1)/2);
		destination = ((dy*8 + dx-1)/2);
	}

	public Action(String move) {
		StringTokenizer st = new StringTokenizer(move, "-");
		start = Integer.parseInt(st.nextToken());
		destination = Integer.parseInt(st.nextToken());
		switch (start) {
			case 0:
				startY = 0;
				startX = 1;
				break;
			case 1:
				startY = 0;
				startX = 3;
				break;
			case 2:
				startY = 0;
				startX = 5;
				break;
			case 3:
				startY = 0;
				startX = 7;
				break;
			case 4:
				startY = 1;
				startX = 0;
				break;
			case 5:
				startY = 1;
				startX = 2;
				break;
			case 6:
				startY = 1;
				startX = 4;
				break;
			case 7:
				startY = 1;
				startX = 6;
				break;
			case 8:
				startY = 2;
				startX = 1;
				break;
			case 9:
				startY = 2;
				startX = 3;
				break;
			case 10:
				startY = 2;
				startX = 5;
				break;
			case 11:
				startY = 2;
				startX = 7;
				break;
			case 12:
				startY = 3;
				startX = 0;
				break;
			case 13:
				startY = 3;
				startX = 2;
				break;
			case 14:
				startY = 3;
				startX = 4;
				break;
			case 15:
				startY = 3;
				startX = 6;
				break;
			case 16:
				startY = 4;
				startX = 1;
				break;
			case 17:
				startY = 4;
				startX = 3;
				break;
			case 18:
				startY = 4;
				startX = 5;
				break;
			case 19:
				startY = 4;
				startX = 7;
				break;
			case 20:
				startY = 5;
				startX = 0;
				break;
			case 21:
				startY = 5;
				startX = 2;
				break;
			case 22:
				startY = 5;
				startX = 4;
				break;
			case 23:
				startY = 5;
				startX = 6;
				break;
			case 24:
				startY = 6;
				startX = 1;
				break;
			case 25:
				startY = 6;
				startX = 3;
				break;
			case 26:
				startY = 6;
				startX = 5;
				break;
			case 27:
				startY = 6;
				startX = 7;
				break;
			case 28:
				startY = 7;
				startX = 0;
				break;
			case 29:
				startY = 7;
				startX = 2;
				break;
			case 30:
				startY = 7;
				startX = 4;
				break;
			case 31:
				startY = 7;
				startX = 6;
				break;
			default:
				startY = -1;
				startX = -1;
		}
		switch (destination) {
			case 0:
				destY = 0;
				destX = 1;
				break;
			case 1:
				destY = 0;
				destX = 3;
				break;
			case 2:
				destY = 0;
				destX = 5;
				break;
			case 3:
				destY = 0;
				destX = 7;
				break;
			case 4:
				destY = 1;
				destX = 0;
				break;
			case 5:
				destY = 1;
				destX = 2;
				break;
			case 6:
				destY = 1;
				destX = 4;
				break;
			case 7:
				destY = 1;
				destX = 6;
				break;
			case 8:
				destY = 2;
				destX = 1;
				break;
			case 9:
				destY = 2;
				destX = 3;
				break;
			case 10:
				destY = 2;
				destX = 5;
				break;
			case 11:
				destY = 2;
				destX = 7;
				break;
			case 12:
				destY = 3;
				destX = 0;
				break;
			case 13:
				destY = 3;
				destX = 2;
				break;
			case 14:
				destY = 3;
				destX = 4;
				break;
			case 15:
				destY = 3;
				destX = 6;
				break;
			case 16:
				destY = 4;
				destX = 1;
				break;
			case 17:
				destY = 4;
				destX = 3;
				break;
			case 18:
				destY = 4;
				destX = 5;
				break;
			case 19:
				destY = 4;
				destX = 7;
				break;
			case 20:
				destY = 5;
				destX = 0;
				break;
			case 21:
				destY = 5;
				destX = 2;
				break;
			case 22:
				destY = 5;
				destX = 4;
				break;
			case 23:
				destY = 5;
				destX = 6;
				break;
			case 24:
				destY = 6;
				destX = 1;
				break;
			case 25:
				destY = 6;
				destX = 3;
				break;
			case 26:
				destY = 6;
				destX = 5;
				break;
			case 27:
				destY = 6;
				destX = 7;
				break;
			case 28:
				destY = 7;
				destX = 0;
				break;
			case 29:
				destY = 7;
				destX = 2;
				break;
			case 30:
				destY = 7;
				destX = 4;
				break;
			case 31:
				destY = 7;
				destX = 6;
				break;
			default:
				destY = -1;
				destX = -1;
		}
		if ((destY - startY == 2) || (startY - destY == 2)) {
			attack = true;
			atkX = (startX + destX) / 2;
			atkY = (startY + destY) /2;
		} else {
			attack = false;
			atkX = -1;
			atkY = -1;
		}
	}

	public String toString() {
		int[][] board = {{0,0,0,1,0,2,0,3},{4,0,5,0,6,0,7,0},{0,8,0,9,0,10,0,11},{12,0,13,0,14,0,15,0},{0,16,0,17,0,18,0,19},{20,0,21,0,22,0,23,0},{0,24,0,25,0,26,0,27},{28,0,29,0,30,0,31,0}};
		return "" + board[startY][startX] + "-" + board[destY][destX];
	}

	public int getStart() {
		StringTokenizer strk = new StringTokenizer(this.toString(), "-");
		return Integer.parseInt(strk.nextToken());
	}

	public int getDest() {
		StringTokenizer strk = new StringTokenizer(this.toString(), "-");
		strk.nextToken();
		System.out.println(this.toString());
		return Integer.parseInt(strk.nextToken());
	}
}