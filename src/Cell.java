import java.awt.Color;
import java.awt.Graphics;

public class Cell {
	private int myX, myY; // x,y position on grid
	public Cell(int row, int col) {
		myX = col;
		myY = row;
	}

	public int getX() {
		return myX;
	}

	public int getY() {
		return myY;
	}

	public void draw(int x_offset, int y_offset, int width, int height,
			Graphics g, Color color) {
		// I leave this understanding to the reader
		int xleft = x_offset + 1 + (myX * (width + 1));
		int ytop = y_offset + 1 + (myY * (height + 1));
		g.setColor(Color.black);
		//g.drawRect(xleft, ytop, width, height);
		g.setColor(color);
		g.fillRect(xleft + 2, ytop + 2, width - 4, height - 4);
	}
}

