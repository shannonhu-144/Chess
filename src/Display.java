import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// Note that the JComponent is set up to listen for mouse clicks
// and mouse movement.  To achieve this, the MouseListener and
// MousMotionListener interfaces are implemented and there is additional
// code in init() to attach those interfaces to the JComponent.


public class Display extends JComponent implements MouseListener, MouseMotionListener {
	public static final int ROWS = 8;
	public static final int COLS = 8;
	public static Cell[][] cell = new Cell[ROWS][COLS];
	private static String[][] possibleBoard = new String[ROWS][COLS];
	private static String[] moveList = new String[18];
	private final int X_GRID_OFFSET = 40; // 40 pixels from left
	private final int Y_GRID_OFFSET = 40; // 40 pixels from top
	private final int CELL_WIDTH = 80;
	private final int CELL_HEIGHT = 80;
	private static int timeElapsed;
	private static JTextField[][] fields;
	private static JTextArea victory;
	private static JTextArea[] leftLabel;
	private static JTextArea[] rightLabel;
	private static JTextArea[] topLabel;
	private static JTextArea[] bottomLabel;
	private static int selectedRow;
	private static int selectedCol;
	private static boolean pieceSelected;
	private static String turn;
	private static String previousStart;
	private static String previousEnd;
	private static boolean inCheck;
	private static boolean stalemate;
	private static boolean pieceTaken;
	private static boolean BlackKingMoved = false;
	private static boolean WhiteKingMoved = false;
	private static boolean BlackLeftRookMoved = false;
	private static boolean BlackRightRookMoved = false;
	private static boolean WhiteLeftRookMoved = false;
	private static boolean WhiteRightRookMoved = false;

	// Note that a final field can be initialized in constructor
	private final int DISPLAY_WIDTH;   
	private final int DISPLAY_HEIGHT;
	private boolean paintloop = false;
	private int TIME_BETWEEN_REPLOTS;
	private NewGameButton newgame;
	private JTextArea stopwatch;
	private static Timer timer;
	private static int kingRow;
	private static int kingCol;
	private PauseButton pause;
	private QuitButton quit;
	private KnightButton knight;
	private BishopButton bishop;
	private RookButton rook;
	private QueenButton queen;
	private int promoteRow;
	private int promoteCol;
	private static JTextArea turnDisplay;
	protected static boolean enabled;
	static Color brown = new Color(210, 105, 30);
	static Color lightBrown = new Color(205, 133, 63);


	public Display(int width, int height) {
		DISPLAY_WIDTH = width;
		DISPLAY_HEIGHT = height;
		timeElapsed = 0;
		pieceSelected = false;
		turn = "White";
		kingRow = 7;
		kingCol = 4;
		init();
		setPaintLoop(false);
		TIME_BETWEEN_REPLOTS = 500;
		enabled = true;
		for (int i = 0; i < moveList.length; i++)
		{
			moveList[i] = "";
		}
	}
	public void init() {
		setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
		initFields();
		initBoard();
		initCells();
		initLabels();
		Font font = new Font("Times New Roman", Font.BOLD, 30);
		TIME_BETWEEN_REPLOTS = 500;
		addMouseListener(this);
		addMouseMotionListener(this);
		newgame = new NewGameButton();
		newgame.setBounds(40, 800, 100, 36);
		add(newgame);
		newgame.setVisible(true);
		stopwatch = new JTextArea("Timer: " + timeElapsed);
		stopwatch.setBounds(600, 785, 200, 36);
		add(stopwatch);
		stopwatch.setVisible(true);
		stopwatch.setEditable(false);
		stopwatch.setBackground(getBackground());
		stopwatch.setFont(font);
		pause = new PauseButton();
		pause.setBounds(140, 800, 100, 36);
		add(pause);
		JTextArea credits = new JTextArea("Credits: Created by Shannon Hu");
		credits.setBounds(40, 836, 700, 36);
		add(credits);
		credits.setVisible(true);
		credits.setEditable(false);
		credits.setBackground(getBackground());
		turnDisplay = new JTextArea("Turn: White");
		turnDisplay.setBounds(400, 785, 300, 36);
		add(turnDisplay);
		turnDisplay.setVisible(true);
		turnDisplay.setEditable(false);
		turnDisplay.setBackground(getBackground());
		turnDisplay.setFont(font);
		victory = new JTextArea("You Win!");
		victory.setBounds(DISPLAY_WIDTH / 2 - 150, 730, 300, 60);
		Font font1 = new Font("SansSerif", Font.BOLD, 50);
		victory.setFont(font1);
		add(victory);
		victory.setBackground(getBackground());
		victory.setVisible(false);
		victory.setEditable(false);
		quit = new QuitButton();
		quit.setBounds(240, 800, 100, 36);
		add(quit);
		quit.setVisible(true);
		knight = new KnightButton();
		knight.setBounds(40, 740, 100, 36);
		add(knight);
		knight.setVisible(false);
		bishop = new BishopButton();
		bishop.setBounds(140, 740, 100, 36);
		add(bishop);
		bishop.setVisible(false);
		rook = new RookButton();
		rook.setBounds(240, 740, 100, 36);
		add(rook);
		rook.setVisible(false);
		queen = new QueenButton();
		queen.setBounds(340, 740, 100, 36);
		add(queen);
		queen.setVisible(false);
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				timeElapsed++;
				stopwatch.setText("Timer: " + timeElapsed);
			}
		});
		timer.start();
	}

	private static void initBoard() {
		String[] pieces = {"rook", "knight", "bishop", "queen", "king"};
		for (int i = 0; i < COLS; i++)
		{
			fields[1][i].setText("Black pawn");
			if (i <= 4)
			{
				if (i == 3 || i == 4)
					fields[0][i].setText("Black " + pieces[i]);
				else {
					fields[0][i].setText("Black " + pieces[i]);
					fields[0][COLS - i - 1].setText("Black " + pieces[i]);
				}
			}
		}
		for (int i = 0; i < COLS; i++)
		{
			fields[6][i].setText("White pawn");
			if (i <= 4)
			{
				if (i == 3 || i == 4)
					fields[ROWS - 1][i].setText("White " + pieces[i]);
				else {
					fields[ROWS - 1][i].setText("White " + pieces[i]);
					fields[ROWS - 1][COLS - i - 1].setText("White " + pieces[i]);
				}
			}
		}
	}
	public void initLabels()
	{
		topLabel = new JTextArea[COLS];
		bottomLabel = new JTextArea[COLS];
		leftLabel = new JTextArea[ROWS];
		rightLabel = new JTextArea[ROWS];
		String labels = "abcdefgh";
		String numbers = "87654321";
		for (int i = 0; i < ROWS; i++)
		{
			Font font = new Font("Times New Roman", Font.BOLD, 30);
			leftLabel[i] = new JTextArea(numbers.substring(i, i+1));
			leftLabel[i].setFont(font);
			leftLabel[i].setBounds(10, Y_GRID_OFFSET + 1 + (i * (CELL_HEIGHT + 1)) + 20, 25, CELL_HEIGHT);
			leftLabel[i].setEditable(false);
			leftLabel[i].setBackground(getBackground());
			add(leftLabel[i]);
			rightLabel[i] = new JTextArea(numbers.substring(i, i+1));
			rightLabel[i].setFont(font);
			rightLabel[i].setBounds(X_GRID_OFFSET + 1 + (COLS * (CELL_WIDTH + 1)) + 15, Y_GRID_OFFSET + 1 + (i * (CELL_HEIGHT + 1)) + 20, 30, CELL_HEIGHT);
			rightLabel[i].setEditable(false);
			rightLabel[i].setBackground(getBackground());
			add(rightLabel[i]);
		}
		for (int j = 0; j < COLS; j++)
		{
			Font font = new Font("Times New Roman", Font.BOLD, 30);
			topLabel[j] = new JTextArea(labels.substring(j, j+1));
			topLabel[j].setFont(font);
			topLabel[j].setBounds(X_GRID_OFFSET + 1 + (j * (CELL_WIDTH + 1)) + 30, 0, CELL_WIDTH, 35);
			topLabel[j].setEditable(false);
			topLabel[j].setBackground(getBackground());
			add(topLabel[j]);
			bottomLabel[j] = new JTextArea(labels.substring(j, j+1));
			bottomLabel[j].setFont(font);
			bottomLabel[j].setBounds(X_GRID_OFFSET + 1 + (j * (CELL_WIDTH + 1)) + 30, Y_GRID_OFFSET + 1 + (ROWS * (CELL_HEIGHT + 1)) + 10, CELL_WIDTH, 40);
			bottomLabel[j].setEditable(false);
			bottomLabel[j].setBackground(getBackground());
			add(bottomLabel[j]);
		}
	}
	private void initFields() {
		fields = new JTextField[ROWS][COLS];
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{
				final int row = i;
				final int col = j;
				fields[i][j] = new JTextField();
				fields[i][j].setLocation(X_GRID_OFFSET + 1 + (j * (CELL_WIDTH + 1)), Y_GRID_OFFSET + 1 + (i * (CELL_HEIGHT + 1)));
				fields[i][j].setSize(CELL_WIDTH, CELL_HEIGHT);
				fields[i][j].setHorizontalAlignment(JTextField.CENTER);
				fields[i][j].setEditable(false);
				fields[i][j].setVisible(false);
				if ((i + j) % 2 == 0)
					fields[i][j].setBackground(brown);
				else fields[i][j].setBackground(lightBrown);
				fields[i][j].addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (enabled)
						{
							if (!pieceSelected)
							{
								if (!fields[row][col].getText().equals(""))
								{
									if (fields[row][col].getText().substring(0, 5).equals(turn))
									{
										selectedRow = row;
										selectedCol = col;
										pieceSelected = true;
										evaluateMoves();
										fields[row][col].setBackground(Color.CYAN);
									}
								}
							}
							else {
								if (row == selectedRow && col == selectedCol)
								{
									for (int k = 0; k < ROWS; k++)
										for (int l = 0; l < COLS; l++)
										{
											if (!((k + " " + l).equals(previousStart)) && !((k + " " + l).equals(previousEnd)))
											{
												if ((k + l) % 2 == 0)
													fields[k][l].setBackground(brown);
												else fields[k][l].setBackground(lightBrown);
											}
											else if ((k + " " + l).equals(previousStart))
												fields[k][l].setBackground(new Color(30, 144, 255));
											else if ((k + " " + l).equals(previousEnd) && !pieceTaken)
												fields[k][l].setBackground(new Color(0, 100, 0));
											else if ((k + " " + l).equals(previousEnd) && pieceTaken)
												fields[k][l].setBackground(new Color(128, 0, 0));
										}
									pieceSelected = false;
								}
								else if (fields[row][col].getBackground().equals(Color.green) || fields[row][col].getBackground().equals(Color.red))
									executeMove(row, col);
							}
						}
						else return;
					}
				});
				add(fields[i][j]);
			}
		}
	}
	public void paintComponent(Graphics g) {
		// change to your liking
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D) g;
		drawGrid(g2);
		drawCells(g2);
		try {
			drawPieces(g2);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (paintloop) {
			try {
				Thread.sleep(TIME_BETWEEN_REPLOTS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
		for (int i = 0; i < ROWS; i++)
			for (int j = 0; j < COLS; j++)
			{
				if (fields[i][j].getText().equals(""))
					fields[i][j].setForeground(getBackground());
				else if (fields[i][j].getText().substring(0,5).equals("White"))
					fields[i][j].setForeground(Color.white);
				else if (fields[i][j].getText().substring(0,5).equals("Black"))
					fields[i][j].setForeground(Color.black);
			}
	}


private void drawPieces(Graphics2D g2) throws IOException {
		// TODO Auto-generated method stub
	URL url = null;
	for (int i = 0; i < ROWS; i++)
		for (int j = 0; j < COLS; j++)
		{
			if (!fields[i][j].getText().equals(""))
				{
				url = new URL("file:/Volumes/95025242/workspace/ChessTest/ChessPieces/" + fields[i][j].getText() + ".png");
				Image img = ImageIO.read(url);
				g2.drawImage(img, X_GRID_OFFSET + (j * (CELL_WIDTH + 1)) + 3,
						Y_GRID_OFFSET + (i * (CELL_HEIGHT + 1)) + 3, CELL_WIDTH - 4, CELL_HEIGHT - 4, fields[i][j].getBackground(), this);
				}
		}
	}
	public void initCells() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				cell[row][col] = new Cell(row, col);
			}
		}
	}


	public void togglePaintLoop() {
		paintloop = !paintloop;
	}


	public void setPaintLoop(boolean value) {
		paintloop = value;
	}

	void drawGrid(Graphics2D g2) {
		g2.setStroke(new BasicStroke(5));
		for (int row = 0; row <= ROWS; row++) {
			g2.drawLine(X_GRID_OFFSET,
					Y_GRID_OFFSET + (row * (CELL_HEIGHT + 1)), X_GRID_OFFSET
					+ COLS * (CELL_WIDTH + 1), Y_GRID_OFFSET
					+ (row * (CELL_HEIGHT + 1)));
		}
		for (int col = 0; col <= COLS; col++) {
			g2.drawLine(X_GRID_OFFSET + (col * (CELL_WIDTH + 1)), Y_GRID_OFFSET,
					X_GRID_OFFSET + (col * (CELL_WIDTH + 1)), Y_GRID_OFFSET
					+ ROWS * (CELL_HEIGHT + 1));
		}
	}


	void drawCells(Graphics2D g) {
		// Have each cell draw itself
		g.setStroke(new BasicStroke(3));
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				// The cell cannot know for certain the offsets nor the height
				// and width; it has been set up to know its own position, so
				// that need not be passed as an argument to the draw method
				cell[row][col].draw(X_GRID_OFFSET, Y_GRID_OFFSET, CELL_WIDTH,
						CELL_HEIGHT, g, fields[row][col].getBackground());
			}
		}
	}
	public void promotePawn(int row, int col) {
		promoteRow = row;
		promoteCol = col;
		knight.setVisible(true);
		bishop.setVisible(true);
		rook.setVisible(true);
		queen.setVisible(true);
		enabled = false;
	}
	public void executeMove(int row, int col) {
		previousEnd = row + " " + col;
		previousStart = selectedRow + " " + selectedCol;
		for (int i = 0; i < moveList.length; i++)
		{
			if (i == moveList.length - 2)
				moveList[i] = previousStart;
			else if (i == moveList.length - 1)
				moveList[i] = previousEnd;
			else moveList[i] = moveList[i+ 2];
		}
		if ((fields[selectedRow][selectedCol].getText().equals("White pawn") && row == 0) || (fields[selectedRow][selectedCol].getText().equals("Black pawn") && row == 7))
		{
			if (fields[row][col].getBackground().equals(Color.red))
			{
				pieceTaken = true;
			}
			else pieceTaken = false;
			promotePawn(row, col);
		}
		else if (fields[selectedRow][selectedCol].getText().substring(6).equals("king") && Math.abs(col - selectedCol) == 2)
		{
			if (col - selectedCol == -2)
			{
				pieceTaken = false;
				fields[row][col].setText(fields[selectedRow][selectedCol].getText());
				fields[row][col + 1].setText(fields[selectedRow][selectedCol - 4].getText());
				fields[row][col - 2].setText("");
				fields[selectedRow][selectedCol].setText("");
				if (turn.equals("White"))
				{
					WhiteKingMoved = true;
					WhiteLeftRookMoved = true;
				}
				else
				{
					BlackKingMoved = true;
					BlackLeftRookMoved = true;
				}
				nextTurn();
			}
			else
			{
				pieceTaken = false;
				fields[row][col].setText(fields[selectedRow][selectedCol].getText());
				fields[row][col - 1].setText(fields[selectedRow][selectedCol + 3].getText());
				fields[row][col + 1].setText("");
				fields[selectedRow][selectedCol].setText("");
				if (turn.equals("White"))
				{
					WhiteKingMoved = true;
					WhiteRightRookMoved = true;
				}
				else
				{
					BlackKingMoved = true;
					BlackRightRookMoved = true;
				}
				nextTurn();
			}
		}
		else {
			if (fields[selectedRow][selectedCol].getText().equals("White king"))
				WhiteKingMoved = true;
			else if (fields[selectedRow][selectedCol].getText().equals("Black king"))
				BlackKingMoved = true;
			else if (selectedRow == 0 && selectedCol == 0)
				BlackLeftRookMoved = true;
			else if (selectedRow == 0 && selectedCol == 7)
				BlackRightRookMoved = true;
			else if (selectedRow == 7 && selectedCol == 0)
				WhiteLeftRookMoved = true;
			else if (selectedRow == 7 && selectedCol == 7)
				WhiteRightRookMoved = true;
			if (fields[row][col].getBackground().equals(Color.red) && fields[row][col].getText().equals(""))
			{
				fields[row][col].setText(fields[selectedRow][selectedCol].getText());
				fields[selectedRow][selectedCol].setText("");
				fields[selectedRow][col].setText("");
				pieceTaken = true;
			}
			else
			{
				if (fields[row][col].getBackground().equals(Color.red))
				{
					pieceTaken = true;
				}
				else pieceTaken = false;
				fields[row][col].setText(fields[selectedRow][selectedCol].getText());
				fields[selectedRow][selectedCol].setText("");
			}
			nextTurn();
		}
		repaint();
	}
	private void nextTurn() {
		if (turn.equals("White"))
			turn = "Black";
		else turn = "White";
		turnDisplay.setText("Turn: " + turn);
		evaluateStalemate();
		if (moveList[0].equals(moveList[8]) && moveList[8].equals(moveList[16]) && moveList[1].equals(moveList[9]) && moveList[9].equals(moveList[17]) && moveList[2].equals(moveList[10]) && moveList[3].equals(moveList[11]))
		{
			enabled = false;
			knight.setVisible(false);
			bishop.setVisible(false);
			rook.setVisible(false);
			queen.setVisible(false);
			victory.setVisible(true);
			victory.setText("Stalemate");
			for (int i = 0; i < ROWS; i++)
				for (int j = 0; j < COLS; j++)
				{
					if (!((i + " " + j).equals(previousStart) || (i + " " + j).equals(previousEnd)))
					{
						if ((i + j) % 2 == 0)
							fields[i][j].setBackground(brown);
						else fields[i][j].setBackground(lightBrown);
					}
					else if ((i + " " + j).equals(previousStart))
						fields[i][j].setBackground(new Color(30, 144, 255));
					else if ((i + " " + j).equals(previousEnd) && !pieceTaken)
						fields[i][j].setBackground(new Color(0, 100, 0));
					else if ((i + " " + j).equals(previousEnd) && pieceTaken)
						fields[i][j].setBackground(new Color(128, 0, 0));
				}
		}
		else if (!stalemate)
		{
			pieceSelected = false;
			for (int i = 0; i < ROWS; i++)
			{
				for (int j = 0; j < COLS; j++)
				{
					if (!((i + " " + j).equals(previousStart) || (i + " " + j).equals(previousEnd)))
					{
						if ((i + j) % 2 == 0)
							fields[i][j].setBackground(brown);
						else fields[i][j].setBackground(lightBrown);
					}
					else if ((i + " " + j).equals(previousStart))
						fields[i][j].setBackground(new Color(30, 144, 255));
					else if ((i + " " + j).equals(previousEnd) && !pieceTaken)
						fields[i][j].setBackground(new Color(0, 100, 0));
					else if ((i + " " + j).equals(previousEnd) && pieceTaken)
						fields[i][j].setBackground(new Color(128, 0, 0));
					if (fields[i][j].getText().equals(turn + " king"))
					{
						kingRow = i;
						kingCol = j;
					}
				}
			}
			knight.setVisible(false);
			bishop.setVisible(false);
			rook.setVisible(false);
			queen.setVisible(false);
			enabled = true;
		}
		else {
			for (int i = 0; i < ROWS; i++)
				for (int j = 0; j < COLS; j++)
				{
					possibleBoard[i][j] = fields[i][j].getText();
				}
			evaluateCheck(possibleBoard);
			if (inCheck)
			{
				enabled = false;
				knight.setVisible(false);
				bishop.setVisible(false);
				rook.setVisible(false);
				queen.setVisible(false);
				victory.setVisible(true);
				String opponent = "";
				if (turn.equals("White"))
					opponent = "Black";
				else opponent = "White";
				victory.setText(opponent + " Wins!");
				timer.stop();
				for (int i = 0; i < ROWS; i++)
					for (int j = 0; j < COLS; j++)
					{
						if (!((i + " " + j).equals(previousStart) || (i + " " + j).equals(previousEnd)))
						{
							if ((i + j) % 2 == 0)
								fields[i][j].setBackground(brown);
							else fields[i][j].setBackground(lightBrown);
						}
						else if ((i + " " + j).equals(previousStart))
							fields[i][j].setBackground(new Color(30, 144, 255));
						else if ((i + " " + j).equals(previousEnd) && !pieceTaken)
							fields[i][j].setBackground(new Color(0, 100, 0));
						else if ((i + " " + j).equals(previousEnd) && pieceTaken)
							fields[i][j].setBackground(new Color(128, 0, 0));
					}
			}
			else {
				enabled = false;
				knight.setVisible(false);
				bishop.setVisible(false);
				rook.setVisible(false);
				queen.setVisible(false);
				victory.setVisible(true);
				victory.setText("Stalemate");
				timer.stop();
				for (int i = 0; i < ROWS; i++)
					for (int j = 0; j < COLS; j++)
					{
						if (!((i + " " + j).equals(previousStart) || (i + " " + j).equals(previousEnd)))
						{
							if ((i + j) % 2 == 0)
								fields[i][j].setBackground(brown);
							else fields[i][j].setBackground(lightBrown);
						}
						else if ((i + " " + j).equals(previousStart))
							fields[i][j].setBackground(new Color(30, 144, 255));
						else if ((i + " " + j).equals(previousEnd) && !pieceTaken)
							fields[i][j].setBackground(new Color(0, 100, 0));
						else if ((i + " " + j).equals(previousEnd) && pieceTaken)
							fields[i][j].setBackground(new Color(128, 0, 0));
					}
			}
		}
	repaint();
	}
	private void evaluateMoves() {
		if (fields[selectedRow][selectedCol].getText().substring(6).equals("pawn"))
		{
			if (turn.equals("White"))
			{
				if (selectedRow > 0 && selectedCol > 0)
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - 1][selectedCol - 1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck && !fields[selectedRow - 1][selectedCol - 1].getText().equals("") && !fields[selectedRow - 1][selectedCol - 1].getText().substring(0, 5).equals(turn))
						fields[selectedRow - 1][selectedCol - 1].setBackground(Color.red);
					if (selectedRow == 3)
					{
						if (fields[selectedRow][selectedCol - 1].getBackground().equals(new Color(0, 100, 0)) && fields[selectedRow - 2][selectedCol - 1].getBackground().equals(new Color(30, 144, 255)) && fields[selectedRow][selectedCol - 1].getText().substring(6).equals("pawn"))
						{
							inCheck = false;
							for (int i = 0; i < ROWS; i++)
								for (int j = 0; j < COLS; j++)
								{
									possibleBoard[i][j] = fields[i][j].getText();
								}
							possibleBoard[selectedRow][selectedCol] = "";
							possibleBoard[selectedRow][selectedCol - 1] = "";
							possibleBoard[selectedRow - 1][selectedCol - 1] = fields[selectedRow][selectedCol].getText();
							evaluateCheck(possibleBoard);
							if (!inCheck)
								fields[selectedRow - 1][selectedCol - 1].setBackground(Color.red);
						}
					}
				}
				if (selectedRow > 0)
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - 1][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck && fields[selectedRow - 1][selectedCol].getText().equals(""))
					{
						fields[selectedRow - 1][selectedCol].setBackground(Color.green);
						if (selectedRow == 6)
						{
							inCheck = false;
							for (int i = 0; i < ROWS; i++)
								for (int j = 0; j < COLS; j++)
								{
									possibleBoard[i][j] = fields[i][j].getText();
								}
							possibleBoard[selectedRow][selectedCol] = "";
							possibleBoard[selectedRow - 2][selectedCol] = fields[selectedRow][selectedCol].getText();
							evaluateCheck(possibleBoard);
							if (!inCheck && fields[selectedRow - 2][selectedCol].getText().equals(""))
							{
								fields[selectedRow - 2][selectedCol].setBackground(Color.green);
							}
						}
					}
				}
				if (selectedRow > 0 && selectedCol < COLS - 1)
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - 1][selectedCol + 1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck && !fields[selectedRow - 1][selectedCol + 1].getText().equals("") && !fields[selectedRow - 1][selectedCol + 1].getText().substring(0, 5).equals(turn))
						fields[selectedRow - 1][selectedCol + 1].setBackground(Color.red);
					if (selectedRow == 3)
					{
						if (fields[selectedRow][selectedCol + 1].getBackground().equals(new Color(0, 100, 0)) && fields[selectedRow - 2][selectedCol + 1].getBackground().equals(new Color(30, 144, 255)) && fields[selectedRow][selectedCol + 1].getText().substring(6).equals("pawn"))
						{
							inCheck = false;
							for (int i = 0; i < ROWS; i++)
								for (int j = 0; j < COLS; j++)
								{
									possibleBoard[i][j] = fields[i][j].getText();
								}
							possibleBoard[selectedRow][selectedCol] = "";
							possibleBoard[selectedRow][selectedCol + 1] = "";
							possibleBoard[selectedRow - 1][selectedCol + 1] = fields[selectedRow][selectedCol].getText();
							evaluateCheck(possibleBoard);
							if (!inCheck)
								fields[selectedRow - 1][selectedCol + 1].setBackground(Color.red);
						}
					}
				}
			}
			else {
				if (selectedRow < ROWS - 1 && selectedCol > 0)
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + 1][selectedCol - 1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck && !fields[selectedRow + 1][selectedCol - 1].getText().equals("") && !fields[selectedRow + 1][selectedCol - 1].getText().substring(0, 5).equals(turn))
						fields[selectedRow + 1][selectedCol - 1].setBackground(Color.red);
					if (selectedRow == 4)
						if (fields[selectedRow][selectedCol - 1].getBackground().equals(new Color(0, 100, 0)) && fields[selectedRow + 2][selectedCol - 1].getBackground().equals(new Color(30, 144, 255)) && fields[selectedRow][selectedCol - 1].getText().substring(6).equals("pawn"))
						{
							inCheck = false;
							for (int i = 0; i < ROWS; i++)
								for (int j = 0; j < COLS; j++)
								{
									possibleBoard[i][j] = fields[i][j].getText();
								}
							possibleBoard[selectedRow][selectedCol] = "";
							possibleBoard[selectedRow][selectedCol - 1] = "";
							possibleBoard[selectedRow + 1][selectedCol - 1] = fields[selectedRow][selectedCol].getText();
							evaluateCheck(possibleBoard);
							if (!inCheck)
								fields[selectedRow + 1][selectedCol - 1].setBackground(Color.red);
						}
				}
				if (selectedRow < ROWS - 1)
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + 1][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck && fields[selectedRow + 1][selectedCol].getText().equals(""))
					{
						fields[selectedRow + 1][selectedCol].setBackground(Color.green);
						if (selectedRow == 1)
						{
							inCheck = false;
							for (int i = 0; i < ROWS; i++)
								for (int j = 0; j < COLS; j++)
								{
									possibleBoard[i][j] = fields[i][j].getText();
								}
							possibleBoard[selectedRow][selectedCol] = "";
							possibleBoard[selectedRow + 2][selectedCol] = fields[selectedRow][selectedCol].getText();
							evaluateCheck(possibleBoard);
							if (!inCheck && fields[selectedRow + 2][selectedCol].getText().equals(""))
							{
								fields[selectedRow + 2][selectedCol].setBackground(Color.green);
							}
						}
					}
				}
				if (selectedRow < ROWS - 1 && selectedCol < COLS - 1)
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + 1][selectedCol + 1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck && !fields[selectedRow + 1][selectedCol + 1].getText().equals("") && !fields[selectedRow + 1][selectedCol + 1].getText().substring(0, 5).equals(turn))
						fields[selectedRow + 1][selectedCol + 1].setBackground(Color.red);
					if (selectedRow == 4)
					{
						if (fields[selectedRow][selectedCol + 1].getBackground().equals(new Color(0, 100, 0)) && fields[selectedRow + 2][selectedCol + 1].getBackground().equals(new Color(30, 144, 255)) && fields[selectedRow][selectedCol + 1].getText().substring(6).equals("pawn"))
						{
							inCheck = false;
							for (int i = 0; i < ROWS; i++)
								for (int j = 0; j < COLS; j++)
								{
									possibleBoard[i][j] = fields[i][j].getText();
								}
							possibleBoard[selectedRow][selectedCol] = "";
							possibleBoard[selectedRow][selectedCol + 1] = "";
							possibleBoard[selectedRow + 1][selectedCol + 1] = fields[selectedRow][selectedCol].getText();
							evaluateCheck(possibleBoard);
							if (!inCheck)
								fields[selectedRow + 1][selectedCol + 1].setBackground(Color.red);
						}
					}
				}
			}
		}
		else if (fields[selectedRow][selectedCol].getText().substring(6).equals("knight"))
		{
			int up2 = selectedRow - 2;
			int up1 = selectedRow - 1;
			int down1 = selectedRow + 1;
			int down2 = selectedRow + 2;
			int left2 = selectedCol - 2;
			int left1 = selectedCol - 1;
			int right1 = selectedCol + 1;
			int right2 = selectedCol + 2;
			if (up2 >= 0 && left1 >= 0)
			{
				if (fields[up2][left1].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up2][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up2][left1].setBackground(Color.green);
					}
				}
				else if (!fields[up2][left1].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up2][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up2][left1].setBackground(Color.red);
					}
				}
			}
			if (up2 >= 0 && right1 < COLS)
			{
				if (fields[up2][right1].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up2][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up2][right1].setBackground(Color.green);
					}
				}
				else if (!fields[up2][right1].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up2][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up2][right1].setBackground(Color.red);
					}
				}
			}
			if (up1 >= 0 && left2 >= 0)
			{
				if (fields[up1][left2].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][left2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][left2].setBackground(Color.green);
					}
				}
				else if (!fields[up1][left2].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][left2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][left2].setBackground(Color.red);
					}
				}
			}
			if (up1 >= 0 && right2 < COLS)
			{
				if (fields[up1][right2].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][right2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][right2].setBackground(Color.green);
					}
				}
				else if (!fields[up1][right2].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][right2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][right2].setBackground(Color.red);
					}
				}
			}
			if (down1 < ROWS && left2 >= 0)
			{
				if (fields[down1][left2].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][left2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][left2].setBackground(Color.green);
					}
				}
				else if (!fields[down1][left2].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][left2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][left2].setBackground(Color.red);
					}
				}
			}
			if (down1 < ROWS && right2 < COLS)
			{
				if (fields[down1][right2].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][right2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][right2].setBackground(Color.green);
					}
				}
				else if (!fields[down1][right2].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][right2] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][right2].setBackground(Color.red);
					}
				}
			}
			if (down2 < ROWS && left1 >= 0)
			{
				if (fields[down2][left1].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down2][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down2][left1].setBackground(Color.green);
					}
				}
				else if (!fields[down2][left1].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down2][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down2][left1].setBackground(Color.red);
					}
				}
			}
			if (down2 < ROWS && right1 < COLS)
			{
				if (fields[down2][right1].getText().equals(""))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down2][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down2][right1].setBackground(Color.green);
					}
				}
				else if (!fields[down2][right1].getText().substring(0, 5).equals(turn))
				{
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down2][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down2][right1].setBackground(Color.red);
					}
				}
			}
		}
		else if (fields[selectedRow][selectedCol].getText().substring(6).equals("bishop"))
		{
			int limiter = 0;
			if (selectedRow > selectedCol)
				limiter = selectedCol;
			else limiter = selectedRow;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow - k][selectedCol - k].getText().equals(""))
				{
					if (!fields[selectedRow - k][selectedCol - k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow - k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow - k][selectedCol - k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow - k][selectedCol - k].setBackground(Color.green);
					}
				}
			}
			if (selectedRow > COLS - selectedCol - 1)
				limiter = COLS - selectedCol - 1;
			else limiter = selectedRow;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow - k][selectedCol + k].getText().equals(""))
				{
					if (!fields[selectedRow - k][selectedCol + k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow - k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow - k][selectedCol + k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow - k][selectedCol + k].setBackground(Color.green);
					}
				}
			}
			if (ROWS - selectedRow - 1 > selectedCol)
				limiter = selectedCol;
			else limiter = ROWS - selectedRow - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow + k][selectedCol - k].getText().equals(""))
				{
					if (!fields[selectedRow + k][selectedCol - k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow + k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow + k][selectedCol - k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow + k][selectedCol - k].setBackground(Color.green);
					}
				}
			}
			if (ROWS - selectedRow - 1 > COLS - selectedCol - 1)
				limiter = COLS - selectedCol - 1;
			else limiter = ROWS - selectedRow - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow + k][selectedCol + k].getText().equals(""))
				{
					if (!fields[selectedRow + k][selectedCol + k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow + k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow + k][selectedCol + k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow + k][selectedCol + k].setBackground(Color.green);
					}
				}
			}
		}
		else if (fields[selectedRow][selectedCol].getText().substring(6).equals("rook"))
		{
			int limiter = selectedRow;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow - k][selectedCol].getText().equals(""))
				{
					if (!fields[selectedRow - k][selectedCol].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow - k][selectedCol] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow - k][selectedCol].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - k][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow - k][selectedCol].setBackground(Color.green);
					}
				}
			}
			limiter = selectedCol;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow][selectedCol - k].getText().equals(""))
				{
					if (!fields[selectedRow][selectedCol - k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow][selectedCol - k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow][selectedCol - k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow][selectedCol - k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow][selectedCol - k].setBackground(Color.green);
					}
				}
			}
			limiter = COLS - selectedCol - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow][selectedCol + k].getText().equals(""))
				{
					if (!fields[selectedRow][selectedCol + k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow][selectedCol + k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow][selectedCol + k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow][selectedCol + k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow][selectedCol + k].setBackground(Color.green);
					}
				}
			}
			limiter = ROWS - selectedRow - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow + k][selectedCol].getText().equals(""))
				{
					if (!fields[selectedRow + k][selectedCol].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow + k][selectedCol] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow + k][selectedCol].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + k][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow + k][selectedCol].setBackground(Color.green);
					}
				}
			}
		}
		else if (fields[selectedRow][selectedCol].getText().substring(6).equals("queen"))
		{
			int limiter = selectedRow;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow - k][selectedCol].getText().equals(""))
				{
					if (!fields[selectedRow - k][selectedCol].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow - k][selectedCol] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow - k][selectedCol].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - k][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow - k][selectedCol].setBackground(Color.green);
					}
				}
			}
			limiter = selectedCol;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow][selectedCol - k].getText().equals(""))
				{
					if (!fields[selectedRow][selectedCol - k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow][selectedCol - k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow][selectedCol - k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow][selectedCol - k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow][selectedCol - k].setBackground(Color.green);
					}
				}
			}
			limiter = COLS - selectedCol - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow][selectedCol + k].getText().equals(""))
				{
					if (!fields[selectedRow][selectedCol + k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow][selectedCol + k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow][selectedCol + k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow][selectedCol + k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow][selectedCol + k].setBackground(Color.green);
					}
				}
			}
			limiter = ROWS - selectedRow - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow + k][selectedCol].getText().equals(""))
				{
					if (!fields[selectedRow + k][selectedCol].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow + k][selectedCol] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow + k][selectedCol].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + k][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow + k][selectedCol].setBackground(Color.green);
					}
				}
			}
			limiter = 0;
			if (selectedRow > selectedCol)
				limiter = selectedCol;
			else limiter = selectedRow;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow - k][selectedCol - k].getText().equals(""))
				{
					if (!fields[selectedRow - k][selectedCol - k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow - k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow - k][selectedCol - k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow - k][selectedCol - k].setBackground(Color.green);
					}
				}
			}
			if (selectedRow > COLS - selectedCol - 1)
				limiter = COLS - selectedCol - 1;
			else limiter = selectedRow;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow - k][selectedCol + k].getText().equals(""))
				{
					if (!fields[selectedRow - k][selectedCol + k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow - k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow - k][selectedCol + k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow - k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow - k][selectedCol + k].setBackground(Color.green);
					}
				}
			}
			if (ROWS - selectedRow - 1 > selectedCol)
				limiter = selectedCol;
			else limiter = ROWS - selectedRow - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow + k][selectedCol - k].getText().equals(""))
				{
					if (!fields[selectedRow + k][selectedCol - k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow + k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow + k][selectedCol - k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + k][selectedCol - k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow + k][selectedCol - k].setBackground(Color.green);
					}
				}
			}
			if (ROWS - selectedRow - 1 > COLS - selectedCol - 1)
				limiter = COLS - selectedCol - 1;
			else limiter = ROWS - selectedRow - 1;
			for (int k = 1; k <= limiter; k++)
			{
				if (!fields[selectedRow + k][selectedCol + k].getText().equals(""))
				{
					if (!fields[selectedRow + k][selectedCol + k].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow + k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow + k][selectedCol + k].setBackground(Color.red);
						}
					}
					break;
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow + k][selectedCol + k] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow + k][selectedCol + k].setBackground(Color.green);
					}
				}
			}
		}
		else {
			int up1 = selectedRow - 1;
			int down1 = selectedRow + 1;
			int left1 = selectedCol - 1;
			int right1 = selectedCol + 1;
			if (up1 >= 0 && left1 >= 0)
			{
				if (!fields[up1][left1].getText().equals(""))
				{
					if (!fields[up1][left1].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[up1][left1] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[up1][left1].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][left1].setBackground(Color.green);
					}
				}
			}
			if (up1 >= 0)
			{
				if (!fields[up1][selectedCol].getText().equals(""))
				{
					if (!fields[up1][selectedCol].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[up1][selectedCol] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[up1][selectedCol].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][selectedCol].setBackground(Color.green);
					}
				}
			}
			if (up1 >= 0 && right1 < ROWS)
			{
				if (!fields[up1][right1].getText().equals(""))
				{
					if (!fields[up1][right1].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[up1][right1] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[up1][right1].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[up1][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[up1][right1].setBackground(Color.green);
					}
				}
			}
			if (left1 >= 0)
			{
				if (!fields[selectedRow][left1].getText().equals(""))
				{
					if (!fields[selectedRow][left1].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow][left1] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow][left1].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow][left1].setBackground(Color.green);
					}
				}
			}
			if (right1 < ROWS)
			{
				if (!fields[selectedRow][right1].getText().equals(""))
				{
					if (!fields[selectedRow][right1].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[selectedRow][right1] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[selectedRow][right1].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[selectedRow][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[selectedRow][right1].setBackground(Color.green);
					}
				}
			}
			if (down1 < ROWS && left1 >= 0)
			{
				if (!fields[down1][left1].getText().equals(""))
				{
					if (!fields[down1][left1].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[down1][left1] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[down1][left1].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][left1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][left1].setBackground(Color.green);
					}
				}
			}
			if (down1 < ROWS)
			{
				if (!fields[down1][selectedCol].getText().equals(""))
				{
					if (!fields[down1][selectedCol].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[down1][selectedCol] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[down1][selectedCol].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][selectedCol] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][selectedCol].setBackground(Color.green);
					}
				}
			}
			if (down1 < ROWS && right1 < COLS)
			{
				if (!fields[down1][right1].getText().equals(""))
				{
					if (!fields[down1][right1].getText().substring(0, 5).equals(turn))
					{
						inCheck = false;
						for (int i = 0; i < ROWS; i++)
							for (int j = 0; j < COLS; j++)
							{
								possibleBoard[i][j] = fields[i][j].getText();
							}
						possibleBoard[selectedRow][selectedCol] = "";
						possibleBoard[down1][right1] = fields[selectedRow][selectedCol].getText();
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							fields[down1][right1].setBackground(Color.red);
						}
					}
				}
				else {
					inCheck = false;
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
						{
							possibleBoard[i][j] = fields[i][j].getText();
						}
					possibleBoard[selectedRow][selectedCol] = "";
					possibleBoard[down1][right1] = fields[selectedRow][selectedCol].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						fields[down1][right1].setBackground(Color.green);
					}
				}
			}
			if (turn.equals("Black") && !BlackLeftRookMoved && !BlackKingMoved)
			{
				kingRow = 0;
				kingCol = 4;
				if (fields[kingRow][kingCol-1].getText().equals("") && fields[kingRow][kingCol - 2].getText().equals("") && fields[kingRow][kingCol - 3].getText().equals(""))
				{
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
							possibleBoard[i][j] = fields[i][j].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						possibleBoard[kingRow][kingCol - 1] = possibleBoard[kingRow][kingCol];
						possibleBoard[kingRow][kingCol] = "";
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							possibleBoard[kingRow][kingCol - 1] = possibleBoard[kingRow][kingCol];
							possibleBoard[kingRow][kingCol] = "";
							evaluateCheck(possibleBoard);
							if (!inCheck)
							{
								possibleBoard[kingRow][kingCol - 1] = possibleBoard[kingRow][kingCol];
								possibleBoard[kingRow][kingCol] = "";
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									fields[kingRow][kingCol + 1].setBackground(Color.green);
								}
							}
						}
					}
				}
			}
			if (turn.equals("Black") && !BlackRightRookMoved && !BlackKingMoved)
			{
				kingRow = 0;
				kingCol = 4;
				if (fields[kingRow][kingCol+1].getText().equals("") && fields[kingRow][kingCol + 2].getText().equals(""))
				{
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
							possibleBoard[i][j] = fields[i][j].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						possibleBoard[kingRow][kingCol + 1] = possibleBoard[kingRow][kingCol];
						possibleBoard[kingRow][kingCol] = "";
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							possibleBoard[kingRow][kingCol + 1] = possibleBoard[kingRow][kingCol];
							possibleBoard[kingRow][kingCol] = "";
							evaluateCheck(possibleBoard);
							if (!inCheck)
							{
								fields[kingRow][kingCol].setBackground(Color.green);
							}
						}
					}
				}
			}
			if (turn.equals("White") && !WhiteLeftRookMoved && !WhiteKingMoved)
			{
				kingRow = 7;
				kingCol = 4;
				if (fields[kingRow][kingCol-1].getText().equals("") && fields[kingRow][kingCol - 2].getText().equals("") && fields[kingRow][kingCol - 3].getText().equals(""))
				{
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
							possibleBoard[i][j] = fields[i][j].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						possibleBoard[kingRow][kingCol - 1] = possibleBoard[kingRow][kingCol];
						possibleBoard[kingRow][kingCol] = "";
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							possibleBoard[kingRow][kingCol - 1] = possibleBoard[kingRow][kingCol];
							possibleBoard[kingRow][kingCol] = "";
							evaluateCheck(possibleBoard);
							if (!inCheck)
							{
								possibleBoard[kingRow][kingCol - 1] = possibleBoard[kingRow][kingCol];
								possibleBoard[kingRow][kingCol] = "";
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									fields[kingRow][kingCol + 1].setBackground(Color.green);
								}
							}
						}
					}
				}
			}
			if (turn.equals("White") && !WhiteRightRookMoved && !WhiteKingMoved)
			{
				kingRow = 7;
				kingCol = 4;
				if (fields[kingRow][kingCol + 1].getText().equals("") && fields[kingRow][kingCol + 2].getText().equals(""))
				{
					for (int i = 0; i < ROWS; i++)
						for (int j = 0; j < COLS; j++)
							possibleBoard[i][j] = fields[i][j].getText();
					evaluateCheck(possibleBoard);
					if (!inCheck)
					{
						possibleBoard[kingRow][kingCol + 1] = possibleBoard[kingRow][kingCol];
						possibleBoard[kingRow][kingCol] = "";
						evaluateCheck(possibleBoard);
						if (!inCheck)
						{
							possibleBoard[kingRow][kingCol + 1] = possibleBoard[kingRow][kingCol];
							possibleBoard[kingRow][kingCol] = "";
							evaluateCheck(possibleBoard);
							if (!inCheck)
							{
								fields[kingRow][kingCol].setBackground(Color.green);
							}
						}
					}
				}
			}
		}
		repaint();
	}
	private static void evaluateCheck(String[][] board) {
		inCheck = false;
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLS; j++)
			{
				if (board[i][j].equals(turn + " king"))
				{
					kingRow = i;
					kingCol = j;
				}
			}
		}
		String opponent = "";
		if (turn.equals("White"))
			opponent = "Black";
		else opponent = "White";
		boolean opponentWhite = opponent.equals("White");
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLS; j++)
			{
				if (!board[i][j].equals(""))
				{
					if (board[i][j].substring(0, 5).equals(opponent))
					{
						if (board[i][j].substring(6).equals("pawn"))
						{

							if (opponentWhite)
							{
								if (Math.abs(kingCol - j) == 1 && kingRow - i == -1)
								{
									inCheck = true;
								}
							}
							else {
								if (Math.abs(kingCol - j) == 1 && kingRow - i == 1)
								{
									inCheck = true;
								}
							}
						}
						else if (board[i][j].substring(6).equals("knight"))
						{

							int up2 = i - 2;
							int up1 = i - 1;
							int down1 = i + 1;
							int down2 = i + 2;
							int left2 = j - 2;
							int left1 = j - 1;
							int right1 = j + 1;
							int right2 = j + 2;
							if ((kingRow == up2 && kingCol == left1) || (kingRow == up2 && kingCol == right1) || (kingRow == up1 && kingCol == left2) || (kingRow == up1 && kingCol == right2) || (kingRow == down1 && kingCol == left2) || (kingRow == down1 && kingCol == right2) || (kingRow == down2 && kingCol == left1) || (kingRow == down2 && kingCol == right1))
							{
								inCheck = true;
							}
						}
						else if (board[i][j].substring(6).equals("bishop"))
						{

							int limiter = 0;
							if (i > j)
								limiter = j;
							else limiter = i;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i - k][j - k].equals(""))
								{
									if (i - k == kingRow && j - k == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							if (COLS - j - 1 > 0)
							{
								if (i > COLS - j - 1)
									limiter = COLS - j - 1;
								else limiter = i;
								for (int k = 1; k <= limiter; k++)
								{
									if (!board[i - k][j + k].equals(""))
									{
										if (i - k == kingRow && j + k == kingCol)
										{inCheck = true;
										}
										break;
									}
								}
							}
							if (ROWS - i - 1 > 0)
							{
								if (ROWS - i - 1 > j)
									limiter = j;
								else limiter = ROWS - i - 1;
								for (int k = 1; k <= limiter; k++)
								{
									if (!board[i + k][j - k].equals(""))
									{
										if (i + k == kingRow && j - k == kingCol)
										{
											inCheck = true;
										}
										break;
									}
								}
							}
							if (ROWS - i - 1 > 0 && COLS - j - 1 > 0)
							{
								if (ROWS - i - 1 > COLS - j - 1)
									limiter = COLS - j - 1;
								else limiter = ROWS - i - 1;
								for (int k = 1; k <= limiter; k++)
								{
									if (!board[i + k][j + k].equals(""))
									{
										if (i + k == kingRow && j + k == kingCol)
										{
											inCheck = true;
										}
										break;
									}
								}
							}
						}
						else if (board[i][j].substring(6).equals("rook"))
						{

							int limiter = i;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i - k][j].equals(""))
								{
									if (i - k == kingRow && j == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							limiter = j;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i][j - k].equals(""))
								{
									if (i == kingRow && j - k == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							limiter = COLS - j - 1;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i][j + k].equals(""))
								{
									if (i == kingRow && j + k == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							limiter = ROWS - i - 1;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i + k][j].equals(""))
								{
									if (i + k == kingRow && j == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
						}
						else if (board[i][j].substring(6).equals("queen"))
						{
							int limiter = i;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i - k][j].equals(""))
								{
									if (i - k == kingRow && j == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							limiter = j;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i][j - k].equals(""))
								{
									if (i == kingRow && j - k == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							limiter = COLS - j - 1;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i][j + k].equals(""))
								{
									if (i == kingRow && j + k == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							limiter = ROWS - i - 1;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i + k][j].equals(""))
								{
									if (i + k == kingRow && j == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							if (i > j)
								limiter = j;
							else limiter = i;
							for (int k = 1; k <= limiter; k++)
							{
								if (!board[i - k][j - k].equals(""))
								{
									if (i - k == kingRow && j - k == kingCol)
									{
										inCheck = true;
									}
									break;
								}
							}
							if (COLS - j - 1 > 0)
							{
								if (i > COLS - j - 1)
									limiter = COLS - j - 1;
								else limiter = i;
								for (int k = 1; k <= limiter; k++)
								{
									if (!board[i - k][j + k].equals(""))
									{
										if (i - k == kingRow && j + k == kingCol)
										{
											inCheck = true;
										}
										break;
									}
								}
							}
							if (ROWS - i - 1 > 0)
							{
								if (ROWS - i - 1 > j)
									limiter = j;
								else limiter = ROWS - i - 1;
								for (int k = 1; k <= limiter; k++)
								{
									if (!board[i + k][j - k].equals(""))
									{
										if (i + k == kingRow && j - k == kingCol)
										{
											inCheck = true;
										}
										break;
									}
								}
							}
							if (ROWS - i - 1 > 0 && COLS - j - 1 > 0)
							{
								if (ROWS - i - 1 > COLS - j - 1)
									limiter = COLS - j - 1;
								else limiter = ROWS - i - 1;
								for (int k = 1; k <= limiter; k++)
								{
									if (!board[i + k][j + k].equals(""))
									{
										if (i + k == kingRow && j + k == kingCol)
										{
											inCheck = true;
										}
										break;
									}
								}
							}
						}
						else {
							if (Math.abs(i - kingRow) == 1 && Math.abs(j - kingCol) == 1)
							{
								inCheck = true;
							}
							if (Math.abs(i - kingRow) == 1 && j == kingCol)
								inCheck = true;
							if (Math.abs(j - kingCol) == 1 && i == kingRow)
								inCheck = true;
						}
					}
				}
			}
		}
	}
	public void newGame()
	{
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLS; j++)
			{
				fields[i][j].setText("");
				{
					if ((i + j) % 2 == 0)
						fields[i][j].setBackground(brown);
					else fields[i][j].setBackground(lightBrown);
				}
			}
		}
		for (int i = 0; i < moveList.length; i++)
		{
			moveList[i] = "";
		}
		enabled = true;
		initBoard();
		victory.setVisible(false);
		kingRow = 7;
		kingCol = 4;
		timeElapsed = 0;
		timer.start();
		pieceSelected = false;
		inCheck = false;
		turn = "White";
		turnDisplay.setText("Turn:" + turn);
		pieceTaken = false;
		repaint();
	}
	public void mouseClicked(MouseEvent arg0) {
		int x= arg0.getX();
		int y = arg0.getY();
		if (x >= X_GRID_OFFSET && x <= (CELL_WIDTH + 1) * COLS + X_GRID_OFFSET)
			if (y >= Y_GRID_OFFSET && y <= (CELL_HEIGHT +1) * ROWS + Y_GRID_OFFSET)
			{
				int row = (int) (y - Y_GRID_OFFSET - 1) / (CELL_HEIGHT+1);
				int col = (int) (x - X_GRID_OFFSET - 1) / (CELL_WIDTH + 1);
				if (enabled)
				{
					if (!pieceSelected)
					{
						if (!fields[row][col].getText().equals(""))
						{
							if (fields[row][col].getText().substring(0, 5).equals(turn))
							{
								selectedRow = row;
								selectedCol = col;
								pieceSelected = true;
								evaluateMoves();
								fields[row][col].setBackground(Color.CYAN);
							}
						}
					}
					else {
						if (row == selectedRow && col == selectedCol)
						{
							for (int k = 0; k < ROWS; k++)
								for (int l = 0; l < COLS; l++)
								{
									if (!((k + " " + l).equals(previousStart)) && !((k + " " + l).equals(previousEnd)))
									{
										if ((k + l) % 2 == 0)
											fields[k][l].setBackground(brown);
										else fields[k][l].setBackground(lightBrown);
									}
									else if ((k + " " + l).equals(previousStart))
										fields[k][l].setBackground(new Color(30, 144, 255));
									else if ((k + " " + l).equals(previousEnd) && !pieceTaken)
										fields[k][l].setBackground(new Color(0, 100, 0));
									else if ((k + " " + l).equals(previousEnd) && pieceTaken)
										fields[k][l].setBackground(new Color(128, 0, 0));
								}
							pieceSelected = false;
							repaint();
						}
						else if (fields[row][col].getBackground().equals(Color.green) || fields[row][col].getBackground().equals(Color.red))
							executeMove(row, col);
					}
				}
				else return;
			}
	}
	public void evaluateStalemate() {
		stalemate = true;
		for (int l = 0; l < ROWS; l++)
			for (int m = 0; m < COLS; m++)
			{
				if (!fields[l][m].getText().equals("") && fields[l][m].getText().substring(0, 5).equals(turn))
				{
					if (fields[l][m].getText().substring(6).equals("pawn"))
					{
						if (turn.equals("White"))
						{
							if (l > 0 && m > 0)
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - 1][m - 1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck && !fields[l - 1][m - 1].getText().equals("") && !fields[l - 1][m - 1].getText().substring(0, 5).equals(turn))
								{
									stalemate = false;
								}
							}
							if (l > 0)
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - 1][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck && fields[l - 1][m].getText().equals(""))
								{
									{
										stalemate = false;
									}
									if (l == 6)
									{
										inCheck = false;
										for (int i = 0; i < ROWS; i++)
											for (int j = 0; j < COLS; j++)
											{
												possibleBoard[i][j] = fields[i][j].getText();
											}
										possibleBoard[l][m] = "";
										possibleBoard[l - 2][m] = fields[l][m].getText();
										evaluateCheck(possibleBoard);
										if (!inCheck && fields[l - 2][m].getText().equals(""))
										{
											stalemate = false;
										}
									}
								}
							}
							if (l > 0 && m < COLS - 1)
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - 1][m + 1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck && !fields[l - 1][m + 1].getText().equals("") && !fields[l - 1][m + 1].getText().substring(0, 5).equals(turn))
								{
									stalemate = false;
								}
							}
						}
						else {
							if (l < ROWS - 1 && m > 0)
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + 1][m - 1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck && !fields[l + 1][m - 1].getText().equals("") && !fields[l + 1][m - 1].getText().substring(0, 5).equals(turn))
									stalemate = false;
							}
							if (l < ROWS - 1)
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + 1][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck && fields[l + 1][m].getText().equals(""))
								{
									stalemate = false;
									if (l == 1)
									{
										inCheck = false;
										for (int i = 0; i < ROWS; i++)
											for (int j = 0; j < COLS; j++)
											{
												possibleBoard[i][j] = fields[i][j].getText();
											}
										possibleBoard[l][m] = "";
										possibleBoard[l + 2][m] = fields[l][m].getText();
										evaluateCheck(possibleBoard);
										if (!inCheck && fields[l + 2][m].getText().equals(""))
										{
											stalemate = false;
										}
									}
								}
							}
							if (l < ROWS - 1 && m < COLS - 1)
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + 1][m + 1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck && !fields[l + 1][m + 1].getText().equals("") && !fields[l + 1][m + 1].getText().substring(0, 5).equals(turn))
									stalemate = false;
							}
						}
					}
					else if (fields[l][m].getText().substring(6).equals("knight"))
					{
						int up2 = l - 2;
						int up1 = l - 1;
						int down1 = l + 1;
						int down2 = l + 2;
						int left2 = m - 2;
						int left1 = m - 1;
						int right1 = m + 1;
						int right2 = m + 2;
						if (up2 >= 0 && left1 >= 0)
						{
							if (fields[up2][left1].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up2][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[up2][left1].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up2][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (up2 >= 0 && right1 < COLS)
						{
							if (fields[up2][right1].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up2][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[up2][right1].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up2][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (up1 >= 0 && left2 >= 0)
						{
							if (fields[up1][left2].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][left2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[up1][left2].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][left2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (up1 >= 0 && right2 < COLS)
						{
							if (fields[up1][right2].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][right2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[up1][right2].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][right2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down1 < ROWS && left2 >= 0)
						{
							if (fields[down1][left2].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][left2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[down1][left2].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][left2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down1 < ROWS && right2 < COLS)
						{
							if (fields[down1][right2].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][right2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[down1][right2].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][right2] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down2 < ROWS && left1 >= 0)
						{
							if (fields[down2][left1].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down2][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[down2][left1].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down2][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down2 < ROWS && right1 < COLS)
						{
							if (fields[down2][right1].getText().equals(""))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down2][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
							else if (!fields[down2][right1].getText().substring(0, 5).equals(turn))
							{
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down2][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
					}
					else if (fields[l][m].getText().substring(6).equals("bishop"))
					{
						int limiter = 0;
						if (l > m)
							limiter = m;
						else limiter = l;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l - k][m - k].getText().equals(""))
							{
								if (!fields[l - k][m - k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l - k][m - k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - k][m - k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (l > COLS - m - 1)
							limiter = COLS - m - 1;
						else limiter = l;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l - k][m + k].getText().equals(""))
							{
								if (!fields[l - k][m + k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l - k][m + k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - k][m + k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (ROWS - l - 1 > m)
							limiter = m;
						else limiter = ROWS - l - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l + k][m - k].getText().equals(""))
							{
								if (!fields[l + k][m - k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l + k][m - k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + k][m - k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (ROWS - l - 1 > COLS - m - 1)
							limiter = COLS - m - 1;
						else limiter = ROWS - l - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l + k][m + k].getText().equals(""))
							{
								if (!fields[l + k][m + k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l + k][m + k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + k][m + k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
					}
					else if (fields[l][m].getText().substring(6).equals("rook"))
					{
						int limiter = l;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l - k][m].getText().equals(""))
							{
								if (!fields[l - k][m].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l - k][m] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - k][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = m;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l][m - k].getText().equals(""))
							{
								if (!fields[l][m - k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l][m - k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l][m - k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = COLS - m - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l][m + k].getText().equals(""))
							{
								if (!fields[l][m + k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l][m + k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l][m + k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = ROWS - l - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l + k][m].getText().equals(""))
							{
								if (!fields[l + k][m].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l + k][m] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + k][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
					}
					else if (fields[l][m].getText().substring(6).equals("queen"))
					{
						int limiter = l;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l - k][m].getText().equals(""))
							{
								if (!fields[l - k][m].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l - k][m] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - k][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = m;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l][m - k].getText().equals(""))
							{
								if (!fields[l][m - k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l][m - k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l][m - k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = COLS - m - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l][m + k].getText().equals(""))
							{
								if (!fields[l][m + k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l][m + k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l][m + k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = ROWS - l - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l + k][m].getText().equals(""))
							{
								if (!fields[l + k][m].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l + k][m] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + k][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						limiter = 0;
						if (l > m)
							limiter = m;
						else limiter = l;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l - k][m - k].getText().equals(""))
							{
								if (!fields[l - k][m - k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l - k][m - k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - k][m - k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (l > COLS - m - 1)
							limiter = COLS - m - 1;
						else limiter = l;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l - k][m + k].getText().equals(""))
							{
								if (!fields[l - k][m + k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l - k][m + k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l - k][m + k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (ROWS - l - 1 > m)
							limiter = m;
						else limiter = ROWS - l - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l + k][m - k].getText().equals(""))
							{
								if (!fields[l + k][m - k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l + k][m - k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + k][m - k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (ROWS - l - 1 > COLS - m - 1)
							limiter = COLS - m - 1;
						else limiter = ROWS - l - 1;
						for (int k = 1; k <= limiter; k++)
						{
							if (!fields[l + k][m + k].getText().equals(""))
							{
								if (!fields[l + k][m + k].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l + k][m + k] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
								break;
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l + k][m + k] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
					}
					else {
						int up1 = l - 1;
						int down1 = l + 1;
						int left1 = m - 1;
						int right1 = m + 1;
						if (up1 >= 0 && left1 >= 0)
						{
							if (!fields[up1][left1].getText().equals(""))
							{
								if (!fields[up1][left1].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[up1][left1] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (up1 >= 0)
						{
							if (!fields[up1][m].getText().equals(""))
							{
								if (!fields[up1][m].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[up1][m] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (up1 >= 0 && right1 < ROWS)
						{
							if (!fields[up1][right1].getText().equals(""))
							{
								if (!fields[up1][right1].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[up1][right1] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[up1][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (left1 >= 0)
						{
							if (!fields[l][left1].getText().equals(""))
							{
								if (!fields[l][left1].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l][left1] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (right1 < ROWS)
						{
							if (!fields[l][right1].getText().equals(""))
							{
								if (!fields[l][right1].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[l][right1] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[l][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down1 < ROWS && left1 >= 0)
						{
							if (!fields[down1][left1].getText().equals(""))
							{
								if (!fields[down1][left1].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[down1][left1] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][left1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down1 < ROWS)
						{
							if (!fields[down1][m].getText().equals(""))
							{
								if (!fields[down1][m].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[down1][m] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][m] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
						if (down1 < ROWS && right1 < COLS)
						{
							if (!fields[down1][right1].getText().equals(""))
							{
								if (!fields[down1][right1].getText().substring(0, 5).equals(turn))
								{
									inCheck = false;
									for (int i = 0; i < ROWS; i++)
										for (int j = 0; j < COLS; j++)
										{
											possibleBoard[i][j] = fields[i][j].getText();
										}
									possibleBoard[l][m] = "";
									possibleBoard[down1][right1] = fields[l][m].getText();
									evaluateCheck(possibleBoard);
									if (!inCheck)
									{
										stalemate = false;
									}
								}
							}
							else {
								inCheck = false;
								for (int i = 0; i < ROWS; i++)
									for (int j = 0; j < COLS; j++)
									{
										possibleBoard[i][j] = fields[i][j].getText();
									}
								possibleBoard[l][m] = "";
								possibleBoard[down1][right1] = fields[l][m].getText();
								evaluateCheck(possibleBoard);
								if (!inCheck)
								{
									stalemate = false;
								}
							}
						}
					}
				}
			}
	}


	public void mouseEntered(MouseEvent arg0) {

	}


	public void mouseExited(MouseEvent arg0) {

	}


	public void mousePressed(MouseEvent arg0) {

	}


	public void mouseReleased(MouseEvent arg0) {

	}


	public void mouseDragged(MouseEvent arg0) {

	}


	public void mouseMoved(MouseEvent arg0) {

	}
	private class KnightButton extends JButton implements ActionListener {
		KnightButton() {
			super("Knight");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0)
		{
			fields[selectedRow][selectedCol].setText("");
			fields[promoteRow][promoteCol].setText(turn + " knight");
			nextTurn();
		}
	}
	private class BishopButton extends JButton implements ActionListener {
		BishopButton() {
			super("Bishop");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0)
		{
			fields[selectedRow][selectedCol].setText("");
			fields[promoteRow][promoteCol].setText(turn + " bishop");
			nextTurn();
		}
	}
	private class RookButton extends JButton implements ActionListener {
		RookButton() {
			super("Rook");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0)
		{
			fields[selectedRow][selectedCol].setText("");
			fields[promoteRow][promoteCol].setText(turn + " rook");
			nextTurn();
		}
	}
	private class QueenButton extends JButton implements ActionListener {
		QueenButton() {
			super("Queen");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0)
		{
			fields[selectedRow][selectedCol].setText("");
			fields[promoteRow][promoteCol].setText(turn + " queen");
			nextTurn();
		}
	}
	private class NewGameButton extends JButton implements ActionListener{
		NewGameButton() {
			super("New Game");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0) {
			newGame();
			repaint();
		}
	}
	private class PauseButton extends JButton implements ActionListener{
		PauseButton() {
			super("Pause");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0) {
			if (getText().equals("Pause"))
			{
				for(int i = 0; i < fields.length; i++)
				{
					for (int j = 0; j < fields[0].length; j++)
					{
						fields[i][j].setVisible(false);
					}
				}
				setText("Resume");
				timer.stop();
				repaint();
			}
			else {
				for(int i = 0; i < fields.length; i++)
				{
					for (int j = 0; j < fields[0].length; j++)
					{
						fields[i][j].setVisible(true);
					}
				}
				setText("Pause");
				timer.start();
				repaint();
			}
		}
	}
	private class QuitButton extends JButton implements ActionListener {
		QuitButton() {
			super("Quit");
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}

}
