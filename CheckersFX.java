/*	Author: Garrett Maitland
	Version: 0.1
	Date: September 14, 2019
*/
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/*	This class is responsible for the GUI and running the game.
*/
public class CheckersFX extends Application {
	//An array of all of the usable tiles.
	static public final Tile[] tiles = new Tile[32];

	@Override
	public void start(Stage stage) {
		//A GridPane to show the checkerboard.
		GridPane board = new GridPane();

		//An int to keep track of the current place in the tiles array
		//while creating the board.
		int ptr = 0;
		//Creates the tiles for the board.
		for (int y = 0; y < 8; y++) {
			//If the row is even, the first tile is a cosmetic tile.
			if (y % 2 == 0) {
				for (int x = 0; x < 8; x++) {
					Rectangle tile2 = new Rectangle(80,80,Color.RED);
					board.add(tile2, x, y);
					x++;
					Tile tile = new Tile(ptr, x, y);
					board.add(tile, x, y);
					tiles[ptr++] = tile;
				}
			} else {
				//Otherwise the first tile is a usable tile.
				for (int x = 0; x < 8; x++) {
					Tile tile = new Tile(ptr, x, y);
					board.add(tile, x, y);
					x++;
					Rectangle tile2 = new Rectangle(80,80,Color.RED);
					board.add(tile2, x, y);
					tiles[ptr++] = tile;
				}
			}
		}

		//Creates and places the black pieces on the board.
		for (int i = 0; i < 12; i++) {
			tiles[i].addUnit(new Piece("black", Color.RED));
		}

		//Creates and places the white pieces on the board.
		for (int i = 20; i < 32; i++) {
			tiles[i].addUnit(new Piece("white", Color.WHITE));
		}

		Scene s = new Scene(board, 640, 640);
		stage.setScene(s);
		stage.setTitle("Checkers");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}

/*	A class for each of the usable tiles on the board.
	StackPane is extended so that it is easy to place
	units on top of the tile.
*/
class Tile extends StackPane {
	//The number of the tile according to checkers notation.
	public final int TILENUMBER;
	public final int X;
	public final int Y;
	private Piece unit;

	//n is the tile number and x and y
	//are the xy-coordinates.
	public Tile(int n, int x, int y) {
		super();
		TILENUMBER = n;
		X = x;
		Y = y;
		unit = null;
		getChildren().add(new Rectangle(80, 80, Color.BLACK));
		addEventFilter(MouseEvent.MOUSE_CLICKED, new TileFilter(TILENUMBER));
	}

	public int getTileNumber() {
		return TILENUMBER;
	}

	//Returns and removes the unit currently placed
	//on the tile.
	public Piece removeUnit() {
		if (unit != null) {
			getChildren().remove(unit);
			Piece u = unit;
			unit = null;
			return u;
		} else
			return null;
	}

	//Places a unit on the tile.
	//Returns true if successful and false
	//if the tile is already occupied.
	public boolean addUnit(Piece u) {
		if (unit == null) {
			unit = u;
			getChildren().add(unit);
			return true;
		} else
			return false;
	}

	//Returns the unit currently placed on the tile
	//without removing it.
	public Piece getUnit() {
		return unit;
	}
}

/*	A class to handle mouse clicks. Sends the tile
	number of the clicked tile to the GameLogic
	class.
*/
class TileFilter implements EventHandler<MouseEvent> {
	//The number of the tile the TileFilter is assigned to.
	public final int TILENUMBER;
	private static GameLogic logic = null;

	public TileFilter(int tileNumber) {
		super();
		TILENUMBER = tileNumber;
		if (logic == null) {
			logic = new GameLogic(CheckersFX.tiles);
		}
	}

	@Override
	public void handle(MouseEvent e) {
		logic.sendTileNumber(TILENUMBER);

	}
}

/*	A class for the checkers pieces.
*/
class Piece extends Circle {
	public final String PLAYER;
	private boolean king;

	//s is the String of the player that owns the piece.
	//c is the Color of the piece.
	public Piece(String s, Color c) {
		super(30, c);
		king = false;
		PLAYER = s;
	}

	public boolean isKing() {
		return king;
	}

	public void makeKing() {
		king = true;
	}

	public boolean isBlack() {
		return PLAYER.equals("black");
	}

	public boolean isWhite() {
		return PLAYER.equals("white");
	}
}