package game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by drake on 06/08/17.
 */
public class Tile {

    public static final int WIDTH = 80;
    public static final int HEIGHT = 80;
    public static final int SLIDE_WIDTH = 20;
    public static final int ARC_WIDTH = 15;
    public static final int ARC_HEIGHT = 15;


    private int value;
    private BufferedImage tileImage;
    private Color backgroundColor;
    private Color textColor;
    private Font font;
    private int x;
    private int y;

    public Tile(int value, int x, int y) {
        this.x = x;
        this.y = y;
        this.value = value;
        tileImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        drawImage();
    }

    private void drawImage() {

        Graphics2D g = (Graphics2D)tileImage.getGraphics();

        switch(value) {
            case 2:
                backgroundColor = new Color(0xe9e9e9);
                textColor = new Color(0, 0, 0);
                break;
            case 4:
                backgroundColor = new Color(0xe6daab);
                textColor = new Color(0,0,0);
                break;
            case 8:
                backgroundColor = new Color(0xf70d3d);
                textColor = new Color(0xffffff);
                break;
            case 16:
                backgroundColor = new Color(0xf28007);
                textColor = new Color(0xffffff);
                break;
            case 32:
                backgroundColor = new Color(0xf55e3b);
                textColor = new Color(0xffffff);
                break;
            case 64:
                backgroundColor = new Color(0xff0000);
                textColor = new Color(0xffffff);
                break;
            case 128:
                backgroundColor = new Color(0xe9de84);
                textColor = new Color(0xffffff);
                break;
            case 256:
                backgroundColor = new Color(0xf6e873);
                textColor = new Color(0xffffff);
                break;
            case 512:
                backgroundColor = new Color(0xf5e455);
                textColor = new Color(0xffffff);
                break;
            case 1024:
                backgroundColor = new Color(0xf7e12c);
                textColor = new Color(0xffffff);
                break;
            case 2048:
                backgroundColor = new Color(0xffe400);
                textColor = new Color(0xffffff);
                break;
            default:
                backgroundColor = Color.BLACK;
                textColor = Color.WHITE;
                break;
        }

        g.setColor(new Color(0,0,0));
        g.fillRect(0,0,WIDTH, HEIGHT);

        g.setColor(backgroundColor);
        g.fillRoundRect(0,0,WIDTH, HEIGHT, ARC_WIDTH, ARC_HEIGHT);

        g.setColor(textColor);

        if (value <= 64) {
            font = Game.main.deriveFont(36f);
        } else
            font = Game.main;

        g.setFont(font);

        // centred message
        int drawX = WIDTH / 2 - DrawUtils.getMessageWidth(String.valueOf(value), font, g) / 2;
        int drawY = HEIGHT / 2 + DrawUtils.getMessageHeight(String.valueOf(value), font, g);

        g.drawString(String.valueOf(value), drawX, drawY);
        g.dispose();
    }
}
