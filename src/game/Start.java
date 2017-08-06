package game;

import javax.swing.*;

/**
 * Created by drake on 06/08/17.
 */
public class Start {

    public static void main(String[] args) {
        Game game = new Game();

        JFrame window = new JFrame("2048");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(game);
        window.pack();
        //center it on the screen
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        game.start();
    }

}
