package gui;

import game.AudioHandler;
import game.DrawUtils;
import game.Game;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by drake on 09/08/17.
 */
public class GuiButton {

    private State currentState = State.RELEASED;
    private Rectangle clickBox;
    private ArrayList<ActionListener> actionListeners;
    private String text = "";

    private Color released;
    private Color hover;
    private Color pressed;
    private Font font = Game.main.deriveFont(22f);
    private AudioHandler audio;

    public GuiButton(int x, int y, int width, int height) {
        clickBox = new Rectangle(x, y, width, height);
        actionListeners = new ArrayList<ActionListener>();
        released = new Color(173, 177, 179);
        hover = Color.darkGray;
        pressed = new Color(11, 116, 117);

        audio = AudioHandler.getInstance();
        audio.load("multimedia_button_click_024.mp3", "pick");
        audio.adjustVolume("pick", -15);

    }

    public void update() {

    }

    public void render(Graphics2D g) {

        switch (currentState) {
            case RELEASED:
                g.setColor(released);
                break;
            case PRESSED:
                g.setColor(pressed);
                break;
            case HOVER:
                g.setColor(hover);
                break;
            default:
        }

        g.fill(clickBox);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(text,
                clickBox.x + clickBox.width / 2 - DrawUtils.getMessageWidth(text, font, g) / 2,
                clickBox.y + clickBox.height / 2 + DrawUtils.getMessageHeight(text, font, g) / 2
        );

    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void mousePressed(MouseEvent e) {
        if (clickBox.contains(e.getPoint()))
            currentState = State.PRESSED;
    }

    public void mouseReleased(MouseEvent e) {
        if (clickBox.contains(e.getPoint())) {
            for (ActionListener al : actionListeners)
                al.actionPerformed(null);
            audio.play("pick", 0);
        }
        currentState = State.RELEASED;

    }

    public void mouseDragged(MouseEvent e) {
        if (clickBox.contains(e.getPoint()))
            currentState = State.PRESSED;
        else
            currentState = State.RELEASED;
    }

    public void mouseMoved(MouseEvent e) {
        if (clickBox.contains(e.getPoint()))
            currentState = State.HOVER;
        else
            currentState = State.RELEASED;
    }

    public int getX() {

        return clickBox.x;

    }

    public int getY() {

        return clickBox.y;

    }

    public int getWidth() {

        return clickBox.width;

    }

    public int getHeight() {

        return clickBox.height;

    }

    public void setText(String text) {

        this.text = text;

    }
}
