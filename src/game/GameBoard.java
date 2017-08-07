package game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by drake on 07/08/17.
 */
public class GameBoard {

    public static final int ROWS = 4;
    public static final int COLS = 4;
    // spacing between tiles
    private static int SPACING = 10;
    public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
    public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;

    private final int startingTiles = 2;
    private Tile[][] board;
    private boolean lost;
    private boolean won;
    private boolean hasStarted;
    private BufferedImage gameBoard;
    private BufferedImage finalBoard;
    private int x;
    private int y;

    public GameBoard(int x, int y) {

        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS];
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);

        createBoardImage();
    }

    private void createBoardImage() {
        int x, y;

        Graphics2D g = (Graphics2D)gameBoard.getGraphics();
        g.setColor(Color.darkGray);
        g.fillRect(0, 0 , BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(Color.lightGray);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                x = SPACING + SPACING * col + Tile.WIDTH * col;
                y = SPACING + SPACING * row + Tile.HEIGHT * row;
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);

            }
        }
    }

    public void render(Graphics2D g) {
        Graphics2D g2d = (Graphics2D)finalBoard.getGraphics();
        g2d.drawImage(gameBoard, 0, 0, null);

        //draw tiles
        g.drawImage(finalBoard, x, y, null);
        g2d.dispose();
    }

    public void update() {
        checkKeys();
    }

    public void checkKeys() {
        // move tiles left
        if(Keyboard.typed(KeyEvent.VK_LEFT)) {
            if(!hasStarted)
                hasStarted = true;
        }

        if(Keyboard.typed(KeyEvent.VK_RIGHT)) {
            if(!hasStarted)
                hasStarted = true;
        }

        if(Keyboard.typed(KeyEvent.VK_UP)) {
            if(!hasStarted)
                hasStarted = true;
        }

        if(Keyboard.typed(KeyEvent.VK_DOWN)) {
            if(!hasStarted)
                hasStarted = true;
        }
    }

}
