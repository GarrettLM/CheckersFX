/*	Author: Garrett Maitland
	Version: 0.6
	Date: October 12, 2019
*/
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

//	This class is responsible for the GUI and running the game.
public class CheckersFX extends Application {
	//An array of all of the usable tiles.
	private static Tile[][] tiles = new Tile[8][8];
	private static Tile prevTile = null;
	private static GameLogic logic = null;
	private static Stage primaryStage;

/*	public void start(Stage stage) {
		logic = new GameLogic();
		primaryStage = stage;
		//A GridPane to show the checkerboard.
		GridPane board = new GridPane();

		//Creates the tiles for the board.
		for (int y = 0; y < 8; y++) {
			//If the row is even, the first tile is a cosmetic tile.
			if (y % 2 == 0) {
				for (int x = 0; x < 8; x++) {
					Tile tile2 = new Tile(x,y,Color.RED);
					board.add(tile2, x, y);
					tiles[y][x] = tile2;
					x++;
					Tile tile = new Tile(x, y);
					board.add(tile, x, y);
					tiles[y][x] = tile;
					if (y < 3)
						tile.addUnit(new Piece("black", Color.RED));
					else if (y > 4)
						tile.addUnit(new Piece("black", Color.WHITE));
				}
			} else {
				//Otherwise the first tile is a usable tile.
				for (int x = 0; x < 8; x++) {
					Tile tile = new Tile(x, y);
					board.add(tile, x, y);
					tiles[y][x] = tile;
					if (y < 3)
						tile.addUnit(new Piece("black", Color.RED));
					else if (y > 4)
						tile.addUnit(new Piece("black", Color.WHITE));
					x++;
					Tile tile2 = new Tile(x,y,Color.RED);
					board.add(tile2, x, y);
					tiles[y][x] = tile2;
				}
			}
		}

		Scene s = new Scene(board, 640, 640);
		primaryStage.setScene(s);
		primaryStage.setTitle("Checkers");
		primaryStage.show();

		promptColor();
	}*/

	@Override
	public void start(Stage stage) {
		logic = new GameLogic(this);
		primaryStage = stage;
		newGame();
	}

	public static void main(String[] args) {
		launch();
	}

	public void newGame() {
		//A GridPane to show the checkerboard.
		GridPane board = new GridPane();

		//Creates the tiles for the board.
		for (int y = 0; y < 8; y++) {
			//If the row is even, the first tile is a cosmetic tile.
			if (y % 2 == 0) {
				for (int x = 0; x < 8; x++) {
					Tile tile2 = new Tile(x,y,Color.RED);
					board.add(tile2, x, y);
					tiles[y][x] = tile2;
					x++;
					Tile tile = new Tile(x, y);
					board.add(tile, x, y);
					tiles[y][x] = tile;
					if (y < 3)
						tile.addUnit(new Piece("black", Color.RED));
					else if (y > 4)
						tile.addUnit(new Piece("black", Color.WHITE));
				}
			} else {
				//Otherwise the first tile is a usable tile.
				for (int x = 0; x < 8; x++) {
					Tile tile = new Tile(x, y);
					board.add(tile, x, y);
					tiles[y][x] = tile;
					if (y < 3)
						tile.addUnit(new Piece("black", Color.RED));
					else if (y > 4)
						tile.addUnit(new Piece("black", Color.WHITE));
					x++;
					Tile tile2 = new Tile(x,y,Color.RED);
					board.add(tile2, x, y);
					tiles[y][x] = tile2;
				}
			}
		}

		Scene s = new Scene(board, 640, 640);
		primaryStage.setScene(s);
		primaryStage.setTitle("Checkers");
		primaryStage.show();

		promptColor();
	}

	public void promptColor() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Select Color");
		stage.setMinWidth(250);
		stage.setMinHeight(150);

		Button blackButton = new Button();
		blackButton.setText("Black");
		blackButton.setOnAction(e -> {logic.newGame("Black"); stage.close();});

		Button whiteButton = new Button();
		whiteButton.setText("White");
		whiteButton.setOnAction(e -> {logic.newGame("White"); stage.close();});

		HBox pane = new HBox();
		pane.getChildren().addAll(blackButton, whiteButton);
		Scene scene = new Scene(pane);

		stage.setScene(scene);
		stage.show();
	}

	public void showMove(Action move) {
		if (move.attack) {
			tiles[move.atkY][move.atkX].removeUnit();
		}
		Piece movedUnit = tiles[move.startY][move.startX].removeUnit();
		tiles[move.destY][move.destX].addUnit(movedUnit);
	}

	public void gameover(String message) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(message);
		stage.setMinWidth(250);
		stage.setMinHeight(250);

		Button playAgain = new Button();
		playAgain.setText("Yes");
		playAgain.setOnAction(e -> {stage.close(); newGame();});
		HBox pane = new HBox();
		pane.getChildren().addAll(new Label(message + "\nPlay Again?"), playAgain);

		stage.setScene(new Scene(pane));
		stage.show();
	}

	/*	A class for each of the usable tiles on the board.
		StackPane is extended so that it is easy to place
		units on top of the tile.
	*/
	private class Tile extends StackPane {
		//The number of the tile according to checkers notation.
		public final int X;
		public final int Y;
		private Piece unit;

		//n is the tile number and x and y
		//are the xy-coordinates.
		public Tile(int x, int y) {
			super();
			X = x;
			Y = y;
			unit = null;
			getChildren().add(new Rectangle(80, 80, Color.BLACK));
			addEventFilter(MouseEvent.MOUSE_CLICKED, new TileFilter(this));
		}

		public Tile(int x, int y, Color c) {
			super();
			X = x;
			Y = y;
			unit = null;
			getChildren().add(new Rectangle(80, 80, Color.RED));
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
	private class TileFilter implements EventHandler<MouseEvent> {
		//The number of the tile the TileFilter is assigned to.
		public final Tile TILE;

		public TileFilter(Tile tile) {
			super();
			TILE = tile;
		}

		@Override
		public void handle(MouseEvent e) {
			if (prevTile != null) {
				if (logic.makeMove(new Action(prevTile.X, prevTile.Y, TILE.X, TILE.Y)))
					prevTile = null;
				else
					prevTile = TILE;
			}	else
				prevTile = TILE;
		}
	}
}

//	A class for the checkers pieces
class Piece extends Circle {
	public final String PLAYER;
	private boolean king;
	private int x, y;

	//s is the String of the player that owns the piece.
	//c is the Color of the piece.
	public Piece(String s, Color c) {
		super(30, c);
		king = false;
		PLAYER = s;
		x = 0;
		y = 0;
	}

	public Piece(String s, Color c, boolean isKing, int xCoord, int yCoord) {
		super(30, c);
		king = isKing;
		PLAYER = s;
		x = xCoord;
		y = yCoord;
	}

	public Piece clone() {
		return copy();
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

	public Piece copy() {
		return new Piece(PLAYER, (Color)super.getFill(), king, x, y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}
}