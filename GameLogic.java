/*	Author Garrett Maitland
	Version 0.2
	Date: October 1, 2019

	This class is responsible for implementing the rules for the chess game
	and executing legal moves and attacks.
*/
public class GameLogic {
	//The number for the last tile that the player selected.
	private int previousTile;
	private Tile[] tiles;
	//The following two variables count how many pieces are left for each player.
	private int blackPieces;
	private int whitePieces;
	private CheckersAI ai;

	//t is an array of Tile objects used for the game board.
	public GameLogic(Tile[] t) {
		//previousTile is set to -1 to represent when no tile has been selected.
		previousTile = -1;
		tiles = t;
		blackPieces = 12;
		whitePieces = 12;
		ai = new CheckersAI();
	}

	//This method accepts input from the TileFilter class.
	//tileNumber should be the number for the tile that the player clicked on.
	public void sendTileNumber(int tileNumber) {
		//Checks if the move is a legal move (that is not an attack) and
		//then moves the unit.
		if (isLegalMove(tileNumber)) {
			move(tileNumber);
			String move = previousTile + "-"+ tileNumber;
			System.out.println(move);
			Action aisMove = ai.nextMove(move);
			previousTile = aisMove.getStart();
			if (aisMove.attack) {
				attack(aisMove.getDest());
			} else {
				move(aisMove.getDest());
			}
			System.out.println(aisMove.toString());
			previousTile = -1;
		} else if (isLegalAttack(tileNumber)) {
			//If the move was a legal attack the attack is carried out.
			//(This does not implement double jumps yet.)
			String move = previousTile + "-"+ tileNumber;
			System.out.println(move);
			attack(tileNumber);
			Action aisMove = ai.nextMove(move);
			previousTile =  aisMove.getStart();
			if (aisMove.attack) {
				attack(aisMove.getDest());
			} else {
				move(aisMove.getDest());
			}
			System.out.println(aisMove.toString());
			previousTile = -1;
		} else
			previousTile = tileNumber;
	}

	//selectedTile is the last tile the player clicked on.
	public boolean isLegalMove(int selectedTile) {
		//If no starting tile has previously been selected returns false.
		if (previousTile == -1) return false;

		int prevX = tiles[previousTile].X;
		int prevY = tiles[previousTile].Y;
		int selX = tiles[selectedTile].X;
		int selY = tiles[selectedTile].Y;

		//Checks to make sure the unit is moving in the correct direction and that the move is valid.
		if ((unitIsKing() || unitIsBlack()) && (prevY+1) == selY && (((prevX-1) == selX) || ((prevX+1) == selX))) {
			//Checks that the selected tile is clear.
			if (tiles[selectedTile].getUnit() == null) return true;
		}
		else if ((unitIsKing() || unitIsWhite()) && (prevY-1) == selY && (((prevX-1) == selX) || ((prevX+1) == selX))) {
			if (tiles[selectedTile].getUnit() == null) return true;
		}
		return false;
	}

	//selectedTile is the tile that the attacker will move to after the attack.
	public boolean isLegalAttack(int selectedTile) {
		if (previousTile == -1) return false;

		int prevX = tiles[previousTile].X;
		int prevY = tiles[previousTile].Y;
		int selX = tiles[selectedTile].X;
		int selY = tiles[selectedTile].Y;

		//Checks to make sure the unit is moving in the correct direction and that the attack is valid.
		if ((unitIsKing() || unitIsBlack()) && (prevY+2) == selY && (((prevX-2) == selX) || ((prevX+2) == selX))) {
			//Checks that the selectedTile is clear and the unit being attacked is the opposite color of the attacker.
			if (tiles[selectedTile].getUnit() == null && unitIsOppositeColor(selectedTile))
				return true;
		}
		else if ((unitIsKing() || unitIsWhite()) && (prevY-2) == selY && (((prevX-2) == selX) || ((prevX+2) == selX))) {
			//Checks that the selectedTile is clear and the unit being attacked is the opposite color of the attacker.
			if (tiles[selectedTile].getUnit() == null && unitIsOppositeColor(selectedTile))
				return true;
		}
		return false;
	}

	//Returns true if the unit on previousTile is black.
	public boolean unitIsBlack() {
		if (tiles[previousTile].getUnit() == null) return false;
		return tiles[previousTile].getUnit().isBlack();
	}

	//target is the int of a tile.
	//The method then returns true if the unit on that tile is black.
	public boolean unitIsBlack(int target) {
		if (tiles[target].getUnit() == null) return false;
		return tiles[target].getUnit().isBlack();
	}

	//Returns true if the unit on previousTile is white.
	public boolean unitIsWhite() {
		if (tiles[previousTile].getUnit() == null) return false;
		return tiles[previousTile].getUnit().isWhite();
	}

	//target is the int of a tile.
	//The method then returns true if the unit on that tile is black.
	public boolean unitIsWhite(int target) {
		if (tiles[target].getUnit() == null) return false;
		return tiles[target].getUnit().isWhite();
	}

	//Returns true if the unit on previousTile is a king.
	public boolean unitIsKing() {
		if (tiles[previousTile].getUnit() == null) return false;
		return tiles[previousTile].getUnit().isKing();
	}

	//Returns true if the unit being attacked is of the opposite color.
	//selected tile is the tile that the attacking unit will move to after the attack.
	public boolean unitIsOppositeColor(int selectedTile) {
		//Sets targetedTile to the tile of the unit under attack.
		int targetedTile = getTargetedTile(selectedTile);
		if (unitIsBlack() && unitIsWhite(targetedTile)) return true;
		if (unitIsWhite() && unitIsBlack(targetedTile)) return true;
		return false;
	}

	//selected tile is the tile that an attacking unit will move to after the attack.
	//This method then returns the tile number for the unit under attack.
	public int getTargetedTile(int selectedTile) {
		int y = (tiles[previousTile].Y + tiles[selectedTile].Y) / 2;
		int x = (tiles[previousTile].X + tiles[selectedTile].X) /2;
		return (x/2) + (y*4);
	}

	//Performs a move to the selectedTile.
	public void move(int selectedTile) {
		Piece unit = tiles[previousTile].removeUnit();
		tiles[selectedTile].addUnit(unit);
		//Make the unit a king if it has made it to the opposite side of the board.
		if (tiles[selectedTile].Y == 0 || tiles[selectedTile].Y == 7)
			unit.makeKing();
	}

	//Performs an attack and moves the attacking unit to the selectedTile.
	public void attack(int selectedTile) {
		Piece unit = tiles[previousTile].removeUnit();
		Piece attackedUnit = tiles[getTargetedTile(selectedTile)].removeUnit();

		if (attackedUnit.isBlack()) {
			blackPieces--;
			if (blackPieces == 0) {
				System.out.println("White wins!");
			}
		} else {
			whitePieces--;
			if (whitePieces == 0) {
				System.out.println("Black wins!");
			}
		}

		tiles[selectedTile].addUnit(unit);
		//Make the unit a king if it has made it to the opposite side of the board.
		if (tiles[selectedTile].Y == 0 || tiles[selectedTile].Y == 7)
			unit.makeKing();
	}
}