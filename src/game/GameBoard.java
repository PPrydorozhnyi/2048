package game;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

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

    private int scoreHeight = 40;
    private int timeHeight = 90;
    private ScoreManager scores;
    private Leaderboards lBoard;
    private int saveCount;

    // for saving
    private String fileName = "SaveData.txt";
    private String saveDataPath;

    //for time
    private long elapsedMS;
    private long startTime;


    //sounds
    private AudioHandler audio;

    public GameBoard(int x, int y) {

        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS];
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);

        createBoardImage();

        audio = AudioHandler.getInstance();
        audio.load("Cool-intro-music-118-bpm.mp3", "background");
        audio.load("sound_ex_machina_Buttons - Stone Button.mp3", "click");
        audio.adjustVolume("background", -30);
        audio.adjustVolume("click", -15);
        audio.play("background", Clip.LOOP_CONTINUOUSLY);

        // new scores
        lBoard = Leaderboards.getInstance();
        lBoard.loadScores();
        scores = new ScoreManager(this);
        scores.loadGame();
        scores.setBestTime(lBoard.getFastestTime());
        scores.setCurrentTopScore(lBoard.getHighScore());
        if (scores.newGame()) {
            start();
            scores.saveGame();
        } else {
            for (int i = 0; i < scores.getBoard().length; i++ ) {
                if (scores.getBoard()[i] == 0)
                    continue;
                spawn(i / ROWS, i % COLS, scores.getBoard()[i]);
            }
            // prevent game loop
            lost = checkDead();
            won = checkWon();
        }
    }

    public void reset() {
        board = new Tile[ROWS][COLS];
        start();
        scores.saveGame();
        lost = false;
        won = false;
        hasStarted = false;
        startTime = System.nanoTime();
        elapsedMS = 0;
        saveCount = 0;
    }

    private void createBoardImage() {
        int x, y;

        Graphics2D g = (Graphics2D) gameBoard.getGraphics();
        g.setColor(Color.darkGray);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(Color.lightGray);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                x = SPACING + SPACING * col + Tile.WIDTH * col;
                y = SPACING + SPACING * row + Tile.HEIGHT * row;
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);

            }
        }
    }

    private void start() {
        for (int i = 0; i < startingTiles; i++)
            spawnRandom();
    }

    private void  spawn(int row, int col, int value) {
        board[row][col] = new Tile(value, getTileX(col), getTileY(row));
    }

    private void spawnRandom() {

        int row, col, location, value;
        Tile current, tile;

        Random random = new Random();
        boolean isValid = true;

        while (isValid) {
            location = random.nextInt(ROWS * COLS);
            row = location / ROWS;
            col = location % COLS;

            current = board[row][col];
            // picking value of the tile
            if (current == null) {
                value = random.nextInt(10) < 9 ? 2 : 4;
                //value = random.nextInt(10) < 9 ? 512 : 1024;
                tile = new Tile(value, getTileX(col), getTileY(row));
                board[row][col] = tile;
                isValid = false;
            }
        }
    }

    public int getTileX(int col) {

        return SPACING + col * Tile.WIDTH + col * SPACING;

    }

    public int getTileY(int row) {

        return SPACING + row * Tile.HEIGHT + row * SPACING;

    }

    public void render(Graphics2D g) {

        Tile current;

        Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
        g2d.drawImage(gameBoard, 0, 0, null);

        //draw tiles
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                current = board[row][col];
                if (current == null)
                    continue;
                current.render(g2d);
            }
        }

        g.drawImage(finalBoard, x, y, null);
        g2d.dispose();
    }

    public void update() {

        Tile current;

        saveCount++;
        if (saveCount >= 120) {
            saveCount = 0;
            scores.saveGame();
        }

        if (!won && !lost)
            if (hasStarted) {
                elapsedMS = (System.nanoTime() - startTime) / 1000000;
                scores.setTime(elapsedMS);
            } else
                startTime = System.nanoTime();

        checkKeys();

        if (scores.getCurrentScore() >= scores.getCurrentTopScore()) {
            scores.setCurrentTopScore(scores.getCurrentScore());
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                current = board[row][col];
                if (current == null)
                    continue;
                current.update();
                //reset position
                resetPosition(current, row, col);
                if (current.getValue() == Game.wonValue)
                    setWon(true);
            }
        }
    }

    // move to new position
    private void resetPosition(Tile current, int row, int col) {
        if (current == null)
            return;

        int x = getTileX(col);
        int y = getTileY(row);

        int distX = current.getX() - x;
        int distY = current.getY() - y;

        //align tile postion by x
        if (Math.abs(distX) < Tile.SLIDE_SPEED) {
            current.setX(current.getX() - distX);
        }

        //align tile postion by y
        if (Math.abs(distY) < Tile.SLIDE_SPEED) {
            current.setY(current.getY() - distY);
        }

        if (distX < 0)
            current.setX(current.getX() + Tile.SLIDE_SPEED);

        if (distY < 0)
            current.setY(current.getY() + Tile.SLIDE_SPEED);

        if (distX > 0)
            current.setX(current.getX() - Tile.SLIDE_SPEED);

        if (distY > 0)
            current.setY(current.getY() - Tile.SLIDE_SPEED);
    }

    public void checkKeys() {
        // move tiles left
        if (Keyboard.typed(KeyEvent.VK_LEFT)) {
            moveTiles(Direction.LEFT);
        }

        if (Keyboard.typed(KeyEvent.VK_RIGHT)) {
            moveTiles(Direction.RIGHT);
        }

        if (Keyboard.typed(KeyEvent.VK_UP)) {
            moveTiles(Direction.UP);
        }

        if (Keyboard.typed(KeyEvent.VK_DOWN)) {
            moveTiles(Direction.DOWN);
        }

        if (!hasStarted)
            hasStarted = !lost;
    }

    public int getHighestTileValue() {
        int value = 2;

        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == null)
                    continue;
                if (board[row][col].getValue() > value)
                    value = board[row][col].getValue();
            }

        return value;

    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        if (!this.lost && lost) {
            lBoard.addTile(getHighestTileValue());
            lBoard.addScore(scores.getCurrentScore());
            lBoard.saveScores();
        }

        this.lost = lost;

    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        if (!this.won && won) {
            lBoard.addTime(scores.getTime());
            lBoard.saveScores();
        }

        this.won = won;
    }

    public ScoreManager getScores() {
        return scores;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    private void moveTiles(Direction dir) {
        //because everytime start with border
        boolean canMove = false;
        int horizontDirection = 0;
        int verticalDirection = 0;
        Tile current;

        if (dir == Direction.LEFT) {
            horizontDirection = -1;
            for (int row = 0; row < ROWS; row++)
                for (int col = 0; col < COLS; col++)
                    if (!canMove)
                        canMove = move(row, col, horizontDirection, verticalDirection, dir);
                    else
                        move(row, col, horizontDirection, verticalDirection, dir);
        } else if (dir == Direction.RIGHT) {
            horizontDirection = 1;
            for (int row = 0; row < ROWS; row++)
                for (int col = COLS - 1; col >= 0; col--)
                    if (!canMove)
                        canMove = move(row, col, horizontDirection, verticalDirection, dir);
                    else
                        move(row, col, horizontDirection, verticalDirection, dir);
        } else if (dir == Direction.UP) {
            verticalDirection = -1;
            for (int row = 0; row < ROWS; row++)
                for (int col = 0; col < COLS; col++)
                    if (!canMove)
                        canMove = move(row, col, horizontDirection, verticalDirection, dir);
                    else
                        move(row, col, horizontDirection, verticalDirection, dir);
        } else if (dir == Direction.DOWN) {
            verticalDirection = 1;
            for (int row = ROWS - 1; row >= 0; row--)
                for (int col = 0; col < COLS; col++)
                    if (!canMove)
                        canMove = move(row, col, horizontDirection, verticalDirection, dir);
                    else
                        move(row, col, horizontDirection, verticalDirection, dir);
        } else
            System.out.println(dir + " is not a valid direction");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                current = board[row][col];
                if (current == null)
                    continue;
                current.setCanCombine(true);
            }
        }

        if (canMove) {
            // play once and end
            audio.play("click", 0);
            spawnRandom();
            // check dead
            setLost(checkDead());
        }

    }

    private boolean checkDead() {
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++) {
            if (board[row][col] == null)
                return false;
            if (checkSurroundingTiles(row, col, board[row][col]))
                return false;
            }

        return true;
    }

    public boolean checkWon() {
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == null)
                    continue;
                if (board[row][col].getValue() >= Game.wonValue)
                    return true;
            }
        return false;
    }

    private boolean checkSurroundingTiles(int row, int col, Tile current) {

        if (row > 0) {
            Tile check = board[row - 1][col];
            if (check == null)
                return true;
            if (current.getValue() == check.getValue())
                return true;
        }

        // not ot get out of bound exception
        if (row < ROWS - 1) {
            Tile check = board[row + 1][col];
            if (check == null)
                return true;
            if (current.getValue() == check.getValue())
                return true;
        }

        if (col > 0) {
            Tile check = board[row][col - 1];
            if (check == null)
                return true;
            if (current.getValue() == check.getValue())
                return true;
        }

        if (col < COLS - 1) {
            Tile check = board[row][col + 1];
            if (check == null)
                return true;
            if (current.getValue() == check.getValue())
                return true;
        }

        return false;
    }

    private boolean checkOutOfBounds(Direction dir, int row, int col) {

        boolean isOutOfBounds = false;

        switch (dir) {
            case LEFT:
                isOutOfBounds = col < 0;
                break;
            case RIGHT:
                isOutOfBounds = col > COLS - 1;
                break;
            case UP:
                isOutOfBounds = row < 0;
                break;
            case DOWN:
                isOutOfBounds = row > ROWS - 1;
                break;
        }

        return isOutOfBounds;
    }

    private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction dir) {

        boolean canMove = false;
        Tile current = board[row][col];
        Point temp = new Point(row, col);
        if(current == null)
            return false;
        boolean move = true;
        int newCol = col;
        int newRow = row;

        // move until all tiles are combined
        while (move) {
            newCol += horizontalDirection;
            newRow += verticalDirection;

            if (checkOutOfBounds(dir, newRow, newCol))
                break;
            // if empty tile
            if (board[newRow][newCol] == null) {

                board[newRow][newCol] = current;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                temp.setRow(newRow);
                temp.setCol(newCol);
                board[newRow][newCol].setSlideTo(temp);
                canMove = true;

            }
            // if tiles with same values
            else if(board[newRow][newCol].getValue() == current.getValue()
                    && board[newRow][newCol].canCombine()) {
                board[newRow][newCol].setCanCombine(false);
                board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                temp.setRow(newRow);
                temp.setCol(newCol);
                board[newRow][newCol].setSlideTo(temp);
                board[newRow][newCol].setCombineAnimation(true);
                // add to score
                scores.setCurrentScore(scores.getCurrentScore() +
                        board[newRow][newCol].getValue());
            } // can not combine
            else
                move = false;
        }


        return canMove;
    }

    public Tile[][] getBoard() {
        return board;
    }

}
