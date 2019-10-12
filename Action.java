/*	Author: Garrett Maitland
	Version: 0.6
	Date: October 12, 2019
*/
import java.util.StringTokenizer;

public class Action {
	public final boolean attack;
	public final int startX;
	public final int startY;
	public final int destX;
	public final int destY;
	public final int atkX;
	public final int atkY;
	private Action jump;

	public Action(int s, int d, boolean a) {
		int[] start = notationToCoordinates(s);
		int[] destination = notationToCoordinates(d);
		startX = start[0];
		startY = start[1];
		destX = destination[0];
		destY = destination[1];
		attack = a;
		jump = null;
		if (a) {
			atkX = (startX + destX)/2;
			atkY = (startY + destY)/2;
		} else {
			atkX = -1;
			atkY = -1;
		}
	}

	public Action(int sx, int sy, int dx, int dy, int ax, int ay) {
		startX = sx;
		startY = sy;
		destX = dx;
		destY = dy;
		atkX = ax;
		atkY = ay;
		jump = null;
		attack = true;
	}

	public Action(int sx, int sy, int dx, int dy) {
		startX = sx;
		startY = sy;
		destX = dx;
		destY = dy;
		jump = null;
		if (((dy - sy) == 2) || ((sy - dy) == 2)) {
			attack = true;
			atkX = (startX + destX)/2;
			atkY = (startY + destY)/2;
		} else {
			attack = false;
			atkX = -1;
			atkY = -1;
		}
	}

	public Action(String move) {
		StringTokenizer st;
		if (move.contains("x")) {
			attack = true;
			st = new StringTokenizer(move, "x");
		} else {
			attack = false;
			st = new StringTokenizer(move, "-");
		}

		int[] start = notationToCoordinates(Integer.parseInt(st.nextToken()));
		int[] destination = notationToCoordinates(Integer.parseInt(st.nextToken()));
		startX = start[0];
		startY = start[1];
		destX = destination[0];
		destY = destination[1];
		if (move.contains("x")) {
			atkX = (startX + destX)/2;
			atkY = (startY + destY)/2;
		} else {
			atkX = -1;
			atkY = -1;
		}
		jump = null;
	}

	public boolean addJump(Action jump) {
		if ((jump.startX != this.destX) || (jump.startY != this.destY))
			return false;
		this.jump = jump;
		return true;
	}

	public Action getJump() {
		return jump;
	}

	public String toString() {
		if (attack)
			return startX + ", " + startY + " x " + destX + ", " + destY;
		else
			return startX + ", " + startY + " - " + destX + ", " + destY;
	}

	public String toNotation() {
		int[][] board = {{0,1,0,2,0,3,0,4},{5,0,6,0,7,0,8,0},{0,9,0,10,0,11,0,12},{13,0,14,0,15,0,16,0},{0,17,0,18,0,19,0,20},{21,0,22,0,23,0,24,0},{0,25,0,26,0,27,0,28},{29,0,30,0,31,0,32,0}};
		if (attack)
			return board[startY][startX] + "x" + board[destY][destX];
		else
			return board[startY][startX] + "-" + board[destY][destX];
	}

	public boolean equals(Action move) {
		return (move.startX == this.startX && move.startY == this.startY && move.destX == this.destX && this.destY == move.destY);
	}

	public int[] notationToCoordinates(int notation) {
		int[] coord = new int[2];
		switch (notation) {
			case 1:
				coord[1] = 0;
				coord[0] = 1;
				break;
			case 2:
				coord[1] = 0;
				coord[0] = 3;
				break;
			case 3:
				coord[1] = 0;
				coord[0] = 5;
				break;
			case 4:
				coord[1] = 0;
				coord[0] = 7;
				break;
			case 5:
				coord[1] = 1;
				coord[0] = 0;
				break;
			case 6:
				coord[1] = 1;
				coord[0] = 2;
				break;
			case 7:
				coord[1] = 1;
				coord[0] = 4;
				break;
			case 8:
				coord[1] = 1;
				coord[0] = 6;
				break;
			case 9:
				coord[1] = 2;
				coord[0] = 1;
				break;
			case 10:
				coord[1] = 2;
				coord[0] = 3;
				break;
			case 11:
				coord[1] = 2;
				coord[0] = 5;
				break;
			case 12:
				coord[1] = 2;
				coord[0] = 7;
				break;
			case 13:
				coord[1] = 3;
				coord[0] = 0;
				break;
			case 14:
				coord[1] = 3;
				coord[0] = 2;
				break;
			case 15:
				coord[1] = 3;
				coord[0] = 4;
				break;
			case 16:
				coord[1] = 3;
				coord[0] = 6;
				break;
			case 17:
				coord[1] = 4;
				coord[0] = 1;
				break;
			case 18:
				coord[1] = 4;
				coord[0] = 3;
				break;
			case 19:
				coord[1] = 4;
				coord[1] = 5;
				break;
			case 20:
				coord[1] = 4;
				coord[0] = 7;
				break;
			case 21:
				coord[1] = 5;
				coord[0] = 0;
				break;
			case 22:
				coord[1] = 5;
				coord[0] = 2;
				break;
			case 23:
				coord[1] = 5;
				coord[0] = 4;
				break;
			case 24:
				coord[1] = 5;
				coord[0] = 6;
				break;
			case 25:
				coord[1] = 6;
				coord[0] = 1;
				break;
			case 26:
				coord[1] = 6;
				coord[0] = 3;
				break;
			case 27:
				coord[1] = 6;
				coord[0] = 5;
				break;
			case 28:
				coord[1] = 6;
				coord[0] = 7;
				break;
			case 29:
				coord[1] = 7;
				coord[0] = 0;
				break;
			case 30:
				coord[1] = 7;
				coord[0] = 2;
				break;
			case 31:
				coord[1] = 7;
				coord[0] = 4;
				break;
			case 32:
				coord[1] = 7;
				coord[0] = 6;
				break;
			default:
				coord[1]= -1;
				coord[0] = -1;
		}
		return coord;
	}
}