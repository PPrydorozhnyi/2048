package game;

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

    private int score = 0;
    private int highScore = 0;
    private Font scoreFont;
    private int scoreHeight = 40;
    private int timeHeight = 90;

    // for saving
    private String fileName = "SaveData.txt";
    private String saveDataPath;

    //for time
    private long elapsedMS;
    private long fastestMS;
    private long startTime;
    // pattern minutes:seconds:ms
    private String formattedTime = "00:00:000";

    public GameBoard(int x, int y) {

        try {

            // keep together with game
            saveDataPath = GameBoard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();


        } catch (Exception e) {
            e.printStackTrace();
        }

        scoreFont = Game.main.deriveFont(24f);

        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS];
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);

        loadHighScore();

        startTime = System.nanoTime();

        createBoardImage();
        start();
    }

    private void createSaveData() {

        try {
            File file = new File(saveDataPath, fileName);

            FileWriter output = new FileWriter(file);

            BufferedWriter writer = new BufferedWriter(output);
            writer.write("" + 0);
            //create fastest time
            writer.newLine();
            writer.write("" + Integer.MAX_VALUE);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadHighScore() {
        try {
            File f = new File(saveDataPath, fileName);

            if (!f.isFile()) {
                createSaveData();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            highScore = Integer.parseInt(reader.readLine());
            //read fastest time
            fastestMS = Long.parseLong(reader.readLine());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHighScore() {
        FileWriter output = null;

        try {
            File f = new File(saveDataPath, fileName);
            output = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(output);

            writer.write("" + highScore);
            writer.newLine();
            if (elapsedMS <= fastestMS && won)
                writer.write("" + elapsedMS);
            else
                writer.write("" + fastestMS);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTime(long millis) {
        String formattedTime;

        String hourFormat = "";
        int hours = (int) (millis / 3600000);
        if (hours >= 1) {
            millis -= hours * 3600000;

            if (hours < 10)
                hourFormat = "0" + hours;
            else
                hourFormat = "" + hours;

            hourFormat += ":";
        }


        String minuteFormat;
        int minutes = (int) (millis / 60000);
        if (minutes >= 1) {
            millis -= minutes * 60000;

            if (minutes < 10)
                minuteFormat = "0" + minutes;
            else
                minuteFormat = "" + minutes;
        }
        else
            minuteFormat = "00";

        String secondFormat;
        int seconds = (int) (millis / 1000);
        if (seconds >= 1) {
            millis -= seconds * 1000;

            if (minutes < 10)
                secondFormat = "0" + seconds;
            else
                secondFormat = "" + seconds;
        }
        else
            secondFormat = "00";

        String milliFormat;
        if (millis > 99)
            milliFormat = "" + millis;
        else if (millis > 9)
            milliFormat = "0" + millis;
        else
            milliFormat = "00" + 0;

        formattedTime = hourFormat + minuteFormat + ":" + secondFormat + ":" + milliFormat;

        return formattedTime;

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

        g.setColor(Color.lightGray);
        g.setFont(scoreFont);
        g.drawString("" + score, 30, scoreHeight);
        g.setColor(Color.red);
        g.drawString("High Score: " + highScore,
                Game.WIDTH - DrawUtils.getMessageWidth("High Score: " + highScore, scoreFont, g) - 20, scoreHeight);

        g.setColor(Color.black);
        g.drawString("Time: " + formattedTime, 30, timeHeight);
        g.setColor(Color.red);
        //g.drawString("Best time: " + formattedTime,
                //Game.WIDTH + DrawUtils.getMessageWidth("Best time: " + formattedTime, scoreFont, g), timeHeight);
        g.drawString("Best time: " + formatTime(fastestMS),
                Game.WIDTH - DrawUtils.getMessageWidth("Best time: " + formatTime(fastestMS), scoreFont, g) - 20, timeHeight);
    }

    public void update() {

        Tile current;

        if (!won && !lost)
            if (hasStarted) {
                elapsedMS = (System.nanoTime() - startTime) / 1000000;
                formattedTime = formatTime(elapsedMS);
            } else
                startTime = System.nanoTime();

        checkKeys();

        if (score >= highScore)
            highScore = score;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                current = board[row][col];
                if (current == null)
                    continue;
                current.update();
                //reset position
                resetPosition(current, row, col);
                if (current.getValue() == 2048)
                    won = true;
                setHighScore();
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
            hasStarted = true;
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
            spawnRandom();
            // check dead
            checkDead();
        }

    }

    private void checkDead() {
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++) {
            if (board[row][col] == null)
                return;
            if (checkSurroundingTiles(row, col, board[row][col]))
                return;
            }

            lost = true;

        if (score >= highScore)
            highScore = score;

        //setHighScore(score);
        setHighScore();
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
                score += board[newRow][newCol].getValue();
            } // can not combine
            else
                move = false;
        }


        return canMove;
    }

}
