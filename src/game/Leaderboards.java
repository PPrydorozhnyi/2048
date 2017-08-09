package game;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by drake on 09/08/17.
 */

//TODO create a class to read and write files
public class Leaderboards {

    private static Leaderboards lBoard;

    private String filePath;
    private String highScores;

    // all time leaderboards
    private ArrayList<Integer> topScores;
    private ArrayList<Integer> topTiles;
    private ArrayList<Long> topTimes;

    private Leaderboards() {

        filePath = new File("").getAbsolutePath();
        highScores = "Scores";

        topScores = new ArrayList<Integer>();
        topTiles = new ArrayList<Integer>();
        topTimes = new ArrayList<Long>();
    }

    public Leaderboards getInstance() {
        if (lBoard == null)
            lBoard = new Leaderboards();

        return lBoard;
    }

    public void addScore(int score) {
        for (int i = 0; i < topScores.size(); i++) {
            if (score > topScores.get(i)) {
                topScores.remove(topScores.size() - 1);
                topScores.add(i, score);
                // because won`t stop add higher score
                return;
            }
        }
    }

    public void addTile(int tileValue) {
        for (int i = 0; i < topTiles.size(); i++) {
            if (tileValue > topTiles.get(i)) {
                topTiles.remove(topTiles.size() - 1);
                topTiles.add(i, tileValue);
                return;
            }
        }
    }

    public void addTime(long millis) {
        for (int i = 0; i < topTimes.size(); i++) {
            if (millis < topTimes.get(i)) {
                topTimes.remove(topTimes.size() - 1);
                topTimes.add(i, millis);
                return;
            }
        }
    }

    public void loadScores() {
        try {
            File f = new File(filePath, highScores);
            if (!f.isFile()) {
                createSaveData();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            topScores.clear();
            topTiles.clear();
            topTimes.clear();

            String[] scores = reader.readLine().split("-");
            String[] tiles = reader.readLine().split("-");
            String[] times = reader.readLine().split("-");

            for (int i = 0; i < scores.length; i++ ) {
                topScores.add(Integer.parseInt(scores[i]));
            }

            for (int i = 0; i < tiles.length; i++ ) {
                topTiles.add(Integer.parseInt(tiles[i]));
            }

            for (int i = 0; i < times.length; i++ ) {
                topTimes.add(Long.parseLong(times[i]));
            }

            reader.close();

        } catch (Exception e) {

        }
    }

    public void saveScores() {
        FileWriter output = null;

        try {
            // TODO refactor add separate method for writing
            File f = new File(filePath, highScores);
            output = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(output);

            for (int i = 0; i < topScores.size(); i++) {
                writer.write(topScores.get(i));
                if (i != topScores.size() - 1)
                    writer.write("-");
            }

            writer.newLine();

            for (int i = 0; i < topTiles.size(); i++) {
                writer.write(topTiles.get(i));
                if (i != topTiles.size() - 1)
                    writer.write("-");
            }

            writer.newLine();

            for (int i = 0; i < topTimes.size(); i++) {
                writer.write("" + topTimes.get(i));
                if (i != topTimes.size() - 1)
                    writer.write("-");
            }

            writer.close();

        } catch (Exception e) {

        }
    }

    private void createSaveData() {

        FileWriter output = null;

        try {
            // TODO refactor add separate method for writing
            File f = new File(filePath, highScores);
            output = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(output);

            writer.write("0-0-0-0-0");

            writer.newLine();

            writer.write("0-0-0-0-0");

            writer.newLine();

            writer.write(Long.MAX_VALUE + "-" + Long.MAX_VALUE + "-" + Long.MAX_VALUE + "-" + Long.MAX_VALUE + "-" + Long.MAX_VALUE );

            writer.close();

        } catch (Exception e) {

        }
    }

    public int getHighScore() {
        return topScores.get(0);
    }

    public long getFastestTime() {
        return topTimes.get(0);
    }

    public int getHighestTile() {
        return topTiles.get(0);
    }

    public ArrayList<Integer> getTopScores() {
        return topScores;
    }

    public ArrayList<Integer> getTopTiles() {
        return topTiles;
    }

    public ArrayList<Long> getTopTimes() {
        return topTimes;
    }
}

