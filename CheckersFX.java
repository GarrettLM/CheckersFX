/*	Author: Garrett Maitland
	Version: 0.9.1
	Date: November 20, 2019
*/
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.util.ArrayList;

//	This class is responsible for the GUI and running the game.
public class CheckersFX extends Application {
	//An array of all of the usable tiles.
	private static Tile[][] tiles = new Tile[8][8];
	private static Tile prevTile = null;
	private static GameLogic logic = null;
	private static Stage primaryStage;
	private static VBox history, availableMoves;

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

		history = new VBox();
		history.setPrefSize(200, 640);
		availableMoves = new VBox();
		availableMoves.setPrefSize(200, 640);
		HBox window = new HBox(history, board, availableMoves);

		Scene s = new Scene(window, 1040, 640);
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

	public void highlgiht(Action move) {
		tiles[move.startY][move.startX].highlight();
		tiles[move.destY][move.destX].highlight();
		Action jump = move.getJump();
		while (jump != null) {
			tiles[jump.destY][jump.destX].highlight();
			jump = jump.getJump();
		}
	}

	public void unhighlight(Action move) {
		tiles[move.startY][move.startX].unhighlight();
		tiles[move.destY][move.destX].unhighlight();
		Action jump = move.getJump();
		while (jump != null) {
			tiles[jump.destY][jump.destX].unhighlight();
			jump = jump.getJump();
		}
	}

	public void showMove(Action move) {
		Piece movedUnit = tiles[move.startY][move.startX].removeUnit();
		if (move.attack) {
			while (move.hasJump()) {
				tiles[move.atkY][move.atkX].removeUnit();
				move = move.getJump();
			}
			tiles[move.atkY][move.atkX].removeUnit();
		}
		tiles[move.destY][move.destX].addUnit(movedUnit);
		history.getChildren().add(new Label(move.toNotation()));
		getAvailableMoves();
	}

	public void getAvailableMoves() {
		availableMoves.getChildren().clear();
		ArrayList<Action> validMoves = logic.getValidMoves();
		for (Action v : validMoves) {
			availableMoves.getChildren().add(new MoveButton(v));
		}
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
		private Circle highlightCircle;

		//n is the tile number and x and y
		//are the xy-coordinates.
		public Tile(int x, int y) {
			super();
			X = x;
			Y = y;
			unit = null;
			getChildren().add(new Rectangle(80, 80, Color.BLACK));
			highlightCircle = new Circle(40, Color.rgb(255, 255, 0 ,0.5));
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

		public void highlight() {
			getChildren().add(highlightCircle);
		}

		public void unhighlight() {
			getChildren().remove(highlightCircle);
		}
	}

	private class MoveButton extends Button {
		private Action move;

		private MoveButton(Action m) {
			super(m.toNotation());
			move = m;
			setOnMouseEntered(e -> highlight(move));
			setOnMouseExited(e -> unhighlight(move));
			setOnAction(e -> {showMove(move); logic.makeMove(move);});
		}

		public void highlight(Action move){
			tiles[move.startY][move.startX].highlight();
			tiles[move.destY][move.destX].highlight();
			Action jump = move.getJump();
			while (jump != null) {
				tiles[jump.destY][jump.destX].highlight();
				jump = jump.getJump();
			}
		}

		public void unhighlight(Action move){
			tiles[move.startY][move.startX].unhighlight();
			tiles[move.destY][move.destX].unhighlight();
			Action jump = move.getJump();
			while (jump != null) {
				tiles[jump.destY][jump.destX].unhighlight();
				jump = jump.getJump();
			}
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