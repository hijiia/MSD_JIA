import javax.sound.sampled.*;

public class SynthTest {
    public static void main(String[] args) throws LineUnavailableException {
        Clip c = AudioSystem.getClip();
        AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

        SineWave sineWave = new SineWave(440);
        AudioClip clip = sineWave.getClip();

        c.open(format16, clip.getData(), 0, clip.getData().length);
        c.start();
        c.loop(2);

        while (c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning()) {
            // Wait for the sound to finish
        }
        c.close();
    }
}