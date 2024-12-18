import javax.sound.sampled.*;

public class SynthTestVolumeAndMix {
    public static void main(String[] args) throws LineUnavailableException {
        Clip c = AudioSystem.getClip();
        AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

        SineWave sineWave1 = new SineWave(220); // Lower frequency
        SineWave sineWave2 = new SineWave(440); // Higher frequency

        VolumeAdjuster volumeAdjuster1 = new VolumeAdjuster(0.5); // Quieter
        volumeAdjuster1.connectInput(sineWave1);

        VolumeAdjuster volumeAdjuster2 = new VolumeAdjuster(0.5); // Quieter
        volumeAdjuster2.connectInput(sineWave2);

        Mixer mixer = new Mixer();
        mixer.connectInput(volumeAdjuster1);
        mixer.connectInput(volumeAdjuster2);

        AudioClip mixedClip = mixer.getClip();

        c.open(format16, mixedClip.getData(), 0, mixedClip.getData().length);
        c.start();
        c.loop(2);

        while (c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning()) {
            // Wait for the sound to finish
        }
        c.close();
    }
}