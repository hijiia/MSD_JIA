import java.util.ArrayList;
import java.util.List;

public class Mixer implements AudioComponent {
    private final List<AudioComponent> inputs = new ArrayList<>();

    public void connectInput(AudioComponent input) {
        inputs.add(input);
    }

    @Override
    public AudioClip getClip() {
        AudioClip outputClip = new AudioClip();
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int mixedSample = 0;
            for (AudioComponent input : inputs) {
                mixedSample += input.getClip().getSample(i);
            }
            mixedSample = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, mixedSample)); // Clamping
            outputClip.setSample(i, mixedSample);
        }
        return outputClip;
    }

    @Override
    public boolean hasInput() {
        return true;
    }
}