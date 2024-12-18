public class VolumeAdjuster implements AudioComponent {
    private final double scale;
    private AudioComponent input;

    public VolumeAdjuster(double scale) {
        this.scale = scale;
    }

    @Override
    public AudioClip getClip() {
        AudioClip inputClip = input.getClip();
        AudioClip outputClip = new AudioClip();
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int adjustedSample = (int) (scale * inputClip.getSample(i));
            adjustedSample = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, adjustedSample)); // Clamping
            outputClip.setSample(i, adjustedSample);
        }
        return outputClip;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input) {
        this.input = input;
    }
}
