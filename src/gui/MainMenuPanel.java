package gui;

import game.DrawUtils;
import game.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by drake on 09/08/17.
 */
public class MainMenuPanel extends GuiPanel {

    private Font titleFont = Game.main.deriveFont(100f);
    private Font creatorFont = Game.main.deriveFont(24f);
    private String title = String.valueOf(Game.wonValue);
    private String creator = "By DRAKE";
    private int buttonWidth = 220;
    private int buttonHeight = 90;
    private int spacing = 120;

    public MainMenuPanel() {
        GuiButton playButton = new GuiButton(
                Game.WIDTH / 2 - buttonWidth / 2, 200,
                buttonWidth, buttonHeight);
        GuiButton scoreButton = new GuiButton(
                Game.WIDTH / 2 - buttonWidth / 2, playButton.getY() + spacing,
                buttonWidth, buttonHeight);
        GuiButton quitButton = new GuiButton(
                Game.WIDTH / 2 - buttonWidth / 2, scoreButton.getY() + spacing,
                buttonWidth, buttonHeight);

        playButton.setText("Play");
        scoreButton.setText("Score");
        quitButton.setText("Qiit");

        add(playButton);
        add(scoreButton);
        add(quitButton);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Play");
            }
        });

        scoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Leaderboards");
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        g.setFont(titleFont);
        g.setColor(Color.black);
        g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 150);
        g.setFont(creatorFont);
        g.drawString(creator, 20, Game.HEIGHT - 10);
    }
}
