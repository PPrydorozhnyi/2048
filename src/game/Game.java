package game;

import gui.GuiScreen;
import gui.MainMenuPanel;
import gui.PlayPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Created by drake on 06/08/17.
 */
public class Game extends JPanel implements KeyListener, Runnable, MouseListener, MouseMotionListener{

    public static int wonValue = 2048;

    private static final long serialVersionUID = 1L;
    // because not resizeable
    public static final int WIDTH = 550;
    public static final int HEIGHT = 600;
    public static final Font main = new Font("Comic Sans MS", Font.PLAIN, 28);
    private Thread game;
    private boolean running;
    // buffered image for the screen
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    // time for update
    private long startTime;
    private long elapsed;
    private boolean set;

    //GUI
    private GuiScreen screen;

    public Game() {
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        screen = GuiScreen.getInstance();
        screen.add("Menu", new MainMenuPanel());
        screen.add("Play", new PlayPanel());
        screen.setCurrentPanel("Menu");
    }

    private void update() {
        screen.update();
        Keyboard.update();

    }

    private void render() {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        screen.render(g);
        g.dispose();
        // render board

        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keyboard.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keyboard.keyReleased(e);
    }

    @Override
    public void run() {

        //TODO check fps/ because keyboard coold not update in time
        int fps = 0;
        int updates = 0;
        long fpsTimer = System.currentTimeMillis();
        // how many nanosec between updates
        double nsPerUpdate = 1000000000.0 / 60;
        double now;
        boolean shouldRender;

        //last update time in nanosec
        double then = System.nanoTime();
        double unprocessed = 0;

        while (running) {

            shouldRender = false;
            now = System.nanoTime();
            unprocessed += (now - then) / nsPerUpdate;
            then = now;

            // update queue
            // control updates
            while (unprocessed >= 1) {
                updates++;
                update();
                unprocessed--;
                shouldRender = true;
            }

            //render
            if (shouldRender) {
                fps++;
                render();
                shouldRender = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //FPS Timer
        if (System.currentTimeMillis() - fpsTimer > 1000) {
            System.out.printf("%d fps %d updates", fps, updates);
            System.out.println();
            fps = 0;
            updates = 0;
            fpsTimer += 1000;
        }
    }

    public synchronized void start() {
        if (running)
            return;
        running = true;
        game = new Thread(this, "game");
        game.start();
    }

    public synchronized void stop() {

        if (!running)
            return;
        running = false;
        System.exit(0);

    }

    // for GUI
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        screen.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        screen.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        screen.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        screen.mouseMoved(e);
    }
}
