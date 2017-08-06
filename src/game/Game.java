package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * Created by drake on 06/08/17.
 */
public class Game extends JPanel implements KeyListener, Runnable{

    private static final long serialVersionUID = 1L;
    // because not resizeable
    public static final int WIDTH = 480;
    public static final int HEIGHT = 640;
    public static final Font main = new Font("Comic Sans MS", Font.PLAIN, 28);
    private Thread game;
    private boolean running;
    // buffered image for the screen
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    // time for update
    private long startTime;
    private long elapsed;
    private boolean set;

    public Game() {
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
    }

    private void update() {

    }

    private void render() {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
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

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {

        int fps = 0;
        int updates = 0;
        long fpsTimer = System.currentTimeMillis();
        // how many nanosec between updates
        double nsPerUpdate = 1000000000.0 / 60;

        //last update time in nanosec
        double then = System.nanoTime();
        double unprocessed = 0;

        while (running) {

            boolean shouldRender = false;
            double now = System.nanoTime();
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
}
