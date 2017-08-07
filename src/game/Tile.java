package game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


/**
 * Created by drake on 06/08/17.
 */
public class Tile {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    public static final int SLIDE_SPEED = 30;
    public static final int ARC_WIDTH = 15;
    public static final int ARC_HEIGHT = 15;


    private int value;
    private BufferedImage tileImage;
    private Color backgroundColor;
    private Color textColor;
    private Font font;
    private Point slideTo;
    private boolean canCombine = true;
    private int x;
    private int y;

    // for animation
    private boolean beginningAnimation;
    private double scaleFirst = 0.1;
    private BufferedImage beginningImage;
    private boolean combineAnimation = false;
    private double scaleCombine = 1.3;
    private BufferedImage combineImage;

    public Point getSlideTo() {
        return slideTo;
    }

    public void setSlideTo(Point slideTo) {
        this.slideTo = slideTo;
    }

    public boolean canCombine() {
        return canCombine;
    }

    public void setCanCombine(boolean canCombine) {
        this.canCombine = canCombine;
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

    public Tile(int value, int x, int y) {
        this.x = x;
        this.y = y;
        this.value = value;
        slideTo = new Point(x, y);

        beginningImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        combineImage = new BufferedImage(WIDTH * 2, HEIGHT * 2, BufferedImage.TYPE_INT_ARGB);

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
                backgroundColor = new Color(0xf79d3d);
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

        //g.setColor(new Color(0,0,0));
        g.setColor(Color.darkGray);
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
        int drawY = HEIGHT / 2 + DrawUtils.getMessageHeight(String.valueOf(value), font, g) / 2;

        g.drawString(String.valueOf(value), drawX, drawY);
        g.dispose();
    }

    public void update() {

        AffineTransform transform = new AffineTransform();

        if (beginningAnimation) {
            // for scaling
            //AffineTransform transform = new AffineTransform();
            transform.translate(WIDTH / 2 - scaleFirst * WIDTH / 2, HEIGHT / 2 - scaleFirst * HEIGHT / 2 );
            transform.scale(scaleFirst, scaleFirst);
            Graphics2D g2d = (Graphics2D)beginningImage.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setColor(new Color(0,0,0,0));
            g2d.fillRect(0,0,WIDTH, HEIGHT);
            g2d.drawImage(tileImage, transform, null);
            scaleFirst += 0.1;
            g2d.dispose();

            if (scaleFirst >= 1)
                beginningAnimation = false;
        } else if (combineAnimation) {

            transform.translate(WIDTH / 2 - scaleCombine * WIDTH / 2, HEIGHT / 2 - scaleCombine * HEIGHT / 2 );
            transform.scale(scaleCombine, scaleCombine);
            Graphics2D g2d = (Graphics2D)combineImage.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setColor(new Color(0,0,0,0));
            g2d.fillRect(0,0,WIDTH, HEIGHT);
            g2d.drawImage(tileImage, transform, null);
            scaleCombine -= 0.05;
            g2d.dispose();

            if (scaleCombine <= 1)
                combineAnimation = false;

        }

    }

    public void render(Graphics2D g) {
        if (beginningAnimation) {
            g.drawImage(beginningImage, x, y, null);
        } else if (combineAnimation) {
            g.drawImage(combineImage, (int) (x + WIDTH / 2 - scaleCombine * WIDTH / 2),
                    (int) (y + HEIGHT / 2 - scaleCombine * HEIGHT / 2), null);
        } else
            g.drawImage(tileImage, x, y, null);

    }

    public boolean isCombineAnimation() {
        return combineAnimation;
    }

    public void setCombineAnimation(boolean combineAnimation) {
        this.combineAnimation = combineAnimation;
        if (combineAnimation)
            scaleCombine = 1.3;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        drawImage();
    }

}
