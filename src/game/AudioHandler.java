package game;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by drake on 07/08/17.
 */
public class AudioHandler {
    private static AudioHandler ourInstance;
    private Map<String, Clip> sounds;

    public static AudioHandler getInstance() {

        if (ourInstance == null)
            ourInstance = new AudioHandler();

        return ourInstance;
    }

    private AudioHandler() {
        sounds = new HashMap<String, Clip>();
    }

    public void load(String resorcePath, String name) {
        URL resource = AudioHandler.class.getClassLoader().getResource(resorcePath);

        AudioInputStream input = null;

        try {

            input = AudioSystem.getAudioInputStream(resource);

        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioFormat baseFormat = input.getFormat();

        if (baseFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(input);
                sounds.put(name, clip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AudioFormat decodeFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
        );

        AudioInputStream decodeIn = AudioSystem.getAudioInputStream(decodeFormat, input );

        try {
            Clip c = AudioSystem.getClip();
            c.open(decodeIn);
            sounds.put(name, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(String name, int loopCount) {

        if (sounds.get(name).isRunning())
            sounds.get(name).stop();

        sounds.get(name).setFramePosition(0);
        sounds.get(name).loop(loopCount);
    }

    public void adjustVolume(String name, int value) {

        FloatControl control = (FloatControl) sounds.get(name).getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(value);

    }
}
