public class SineWave implements AudioComponent {
    private final int frequency;

    public SineWave(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double value = Short.MAX_VALUE * Math.sin(2 * Math.PI * frequency * i / AudioClip.SAMPLE_RATE);
            clip.setSample(i, (int) value);
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        throw new UnsupportedOperationException("SineWave does not accept input");
    }
}