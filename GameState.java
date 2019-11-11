/*	Author: Garrett Maitland
	Version: 0.8
	Date: November 10, 2019
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;
import javafx.scene.paint.Color;

class GameState {
	private Piece[][] board;
	private ArrayList<Piece> playersPieces;
	private ArrayList<Piece> opponentsPieces;
	public final Action action;	//move that lead to the current state
	private boolean kingedTurn;	//true if the last piece to move was kinged this turn

	public GameState() {
		playersPieces = new ArrayList<Piece>();
		opponentsPieces = new ArrayList<Piece>();
		board = new Piece[8][8];
		kingedTurn = false;

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
		board = new Piece[8][8];
		playersPieces = prevState.cloneOpponentsPieces();
		opponentsPieces = prevState.clonePlayersPieces();
		kingedTurn = false;

		for (Piece p : playersPieces) {
			board[p.getY()][p.getX()] = p;
		}
		for (Piece p : opponentsPieces) {
			board[p.getY()][p.getX()] = p;
		}

		Piece movedPiece = board[a.startY][a.startX];

		Piece movedUnit = board[a.startY][a.startX];
		board[a.startY][a.startX] = null;

		if (a.attack) {
			Piece attackedPiece = board[a.atkY][a.atkX];
			while (a.hasJump()) {
				if ((a.destY == 0 || a.destY == 7) && !movedPiece.isKing()) {
					movedPiece.makeKing();
					kingedTurn = true;
				}
				board[a.atkY][a.atkX] = null;
				playersPieces.remove(attackedPiece);
				a = a.getJump();
				attackedPiece = board[a.atkY][a.atkX];
			}
			playersPieces.remove(attackedPiece);
			board[a.atkY][a.atkX] = null;
		}
		movedUnit.setCoord(a.destX, a.destY);
		board[a.destY][a.destX] = movedUnit;
		if ((a.destY == 0 || a.destY == 7) && !movedPiece.isKing()) {
			movedPiece.makeKing();
			kingedTurn = true;
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

	public int getPlayersScore() {
		return playersPieces.size();
	}

	public int getOpponentsScore() {
		return opponentsPieces.size();
	}

	public GameState successorState(Action move) {
		return new GameState(this, move);
	}

	public ArrayList<Action> possibleMoves() {
		ArrayList<Action> actions = new ArrayList<Action>();
		boolean forcedJump = false;
		ArrayList<Action> doubleJumps = null;
		Action jump = null;

		for (Piece p : playersPieces) {
			int x = p.getX();
			int y = p.getY();
			if ((p.isKing() || p.isBlack()) && (x > 1 && y < 6) && board[y+1][x-1] != null && (board[y+1][x-1].isWhite() != p.isWhite()) && board[y+2][x-2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				jump = new Action(x, y, x-2, y+2, x-1, y+1);
				doubleJumps = successorState(jump).checkForDoubleJumps(jump);
				if (doubleJumps.isEmpty()) {
					actions.add(jump);
				} else {
					for (Action j : doubleJumps) {
						actions.add(new Action(jump, j));
					}
				}
			}
			if ((p.isKing() || p.isBlack()) && (x < 6 && y < 6) && board[y+1][x+1] != null && (board[y+1][x+1].isWhite() != p.isWhite()) && board[y+2][x+2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				jump = new Action(x, y, x+2, y+2, x+1, y+1);
				doubleJumps = successorState(jump).checkForDoubleJumps(jump);
				if (doubleJumps.isEmpty()) {
					actions.add(jump);
				} else {
					for (Action j : doubleJumps) {
						actions.add(new Action(jump, j));
					}
				}
			}
			if ((p.isKing() || p.isWhite()) && (x > 1 && y > 1) && board[y-1][x-1] != null && (board[y-1][x-1].isWhite() != p.isWhite()) && board[y-2][x-2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				jump = new Action(x, y, x-2, y-2, x-1, y-1);
				doubleJumps = successorState(jump).checkForDoubleJumps(jump);
				if (doubleJumps.isEmpty()) {
					actions.add(jump);
				} else {
					for (Action j : doubleJumps) {
						actions.add(new Action(jump, j));
					}
				}
			}
			if ((p.isKing() || p.isWhite()) && (x < 6 && y > 1) && board[y-1][x+1] != null && (board[y-1][x+1].isWhite() != p.isWhite()) && board[y-2][x+2] == null) {
				if (!forcedJump) {
					forcedJump = true;
					actions.clear();
				}
				jump = new Action(x, y, x+2, y-2, x+1, y-1);
				doubleJumps = successorState(jump).checkForDoubleJumps(jump);
				if (doubleJumps.isEmpty()) {
					actions.add(jump);
				} else {
					for (Action j : doubleJumps) {
						actions.add(new Action(jump, j));
					}
				}
			}
			if (!forcedJump) {
				if ((p.isKing() || p.isBlack()) && (y < 7 && x > 0) && board[y+1][x-1] == null) {
					actions.add(new Action(x, y, x-1, y+1));
				}
				if ((p.isKing() || p.isBlack()) && (y < 7 && x < 7) && board[y+1][x+1] == null) {
					actions.add(new Action(x, y, x+1, y+1));
				}
				if ((p.isKing() || p.isWhite()) && (y > 0 && x > 0) && board[y-1][x-1] == null) {
					actions.add(new Action(x, y, x-1, y-1));
				}
				if ((p.isKing() || p.isWhite()) && (y > 0 && x < 7) && board[y-1][x+1] == null) {
					actions.add(new Action(x, y, x+1, y-1));
				}
			}
		}
		return actions;
	}

/*	public ArrayList<Action> checkForDoubleJumps(Action attack, String player) {
		//ArrayList<Pieces> playersPieces, opponentsPieces;
		ArrayList<Action> jumps =  new ArrayList<Action>();
		ArrayList<Action> results = new ArrayList<Action>();
		/*if (player.equals("black")) {
			playersPieces = blackPieces;
			opponentsPieces = whitePieces;
		} else {
			playersPieces = whitePieces;
			opponentsPieces = whitePieces;
		}
		int x = attack.destX;
		int y = attack.destY;
		Piece p = board[y][x];
		//System.out.println(attack.toNotation());
		//System.out.println(p);
		if (p == null)
			return results;
		if ((p.isKing() || p.isBlack()) && (x > 1 && y < 6) && board[y+1][x-1] != null && (board[y+1][x-1].isWhite() != p.isWhite()) && board[y+2][x-2] == null) {
			jumps.add(new Action(x, y, x-2, y+2, x-1, y+1));
		}
		if ((p.isKing() || p.isBlack()) && (x < 6 && y < 6) && board[y+1][x+1] != null && (board[y+1][x+1].isWhite() != p.isWhite()) && board[y+2][x+2] == null) {
			jumps.add(new Action(x, y, x+2, y+2, x+1, y+1));
		}
		if ((p.isKing() || p.isWhite()) && (x > 1 && y > 1) && board[y-1][x-1] != null && (board[y-1][x-1].isWhite() != p.isWhite()) && board[y-2][x-2] == null) {
			jumps.add(new Action(x, y, x-2, y-2, x-1, y-1));
		}
		if ((p.isKing() || p.isWhite()) && (x < 6 && y > 1) && board[y-1][x+1] != null && (board[y-1][x+1].isWhite() != p.isWhite()) && board[y-2][x+2] == null) {
			jumps.add(new Action(x, y, x+2, y-2, x+1, y-1));
		}
		for (Action j : jumps) {
			GameState successor = successorState(j);
			ArrayList<Action> multijump = successor.checkForDoubleJumps(attack, player);
			if (!(multijump.isEmpty())) {
				for (Action m : multijump) {
					results.add(new Action(j, m));
				}
			} else
				results.add(j);
		}
		return results;
	}*/

	public ArrayList<Action> checkForDoubleJumps(Action attack) {
		int x = attack.destX;
		int y = attack.destY;
		Piece p = board[y][x];

		System.out.println(attack.toNotation());
		System.out.println(attack.toString());
		System.out.println(p);

		if (p == null) {
			return new ArrayList<Action>();
		}


		ArrayList<Action> jumps =  new ArrayList<Action>();
		ArrayList<Action> results = new ArrayList<Action>();

		if (((p.isKing() && !kingedTurn)|| p.isBlack()) && (x > 1 && y < 6) && board[y+1][x-1] != null && (board[y+1][x-1].isWhite() != p.isWhite()) && board[y+2][x-2] == null) {
			jumps.add(new Action(x, y, x-2, y+2, x-1, y+1));
		}
		if (((p.isKing() && !kingedTurn)|| p.isBlack()) && (x < 6 && y < 6) && board[y+1][x+1] != null && (board[y+1][x+1].isWhite() != p.isWhite()) && board[y+2][x+2] == null) {
			jumps.add(new Action(x, y, x+2, y+2, x+1, y+1));
		}
		if (((p.isKing() && !kingedTurn)|| p.isWhite()) && (x > 1 && y > 1) && board[y-1][x-1] != null && (board[y-1][x-1].isWhite() != p.isWhite()) && board[y-2][x-2] == null) {
			jumps.add(new Action(x, y, x-2, y-2, x-1, y-1));
		}
		if (((p.isKing() && !kingedTurn)|| p.isWhite()) && (x < 6 && y > 1) && board[y-1][x+1] != null && (board[y-1][x+1].isWhite() != p.isWhite()) && board[y-2][x+2] == null) {
			jumps.add(new Action(x, y, x+2, y-2, x+1, y-1));
		}

		for (Action j : jumps) {
			GameState successor = successorState(j);
			ArrayList<Action> multijump = successor.checkForDoubleJumps(attack);
			if (!(multijump.isEmpty())) {
				for (Action m : multijump) {
					results.add(new Action(j, m));
				}
			} else
				results.add(j);
		}
		return results;
	}

	public void printState() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == null) {
					System.out.print("|_");
				} else {
					if (board[i][j].isBlack()) {
						System.out.print("|b");
					} else if (board[i][j].isWhite()) {
						System.out.print("|w");
					} else {
						System.out.print("?");
					}
				}
			}
			System.out.println("|");
		}
	}

	public void swapPlayers() {
		ArrayList<Piece> temp = playersPieces;
		playersPieces = opponentsPieces;
		opponentsPieces = temp;
	}

	public boolean isTerminal() {
		return playersPieces.isEmpty() || opponentsPieces.isEmpty() || possibleMoves().isEmpty();
	}
}