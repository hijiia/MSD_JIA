package com.example.synthesizer;

public class SquareWave implements AudioComponent {
    private AudioComponent input;
    public SquareWave(AudioComponent input) {
//        this.frequency = frequency;
        this.input = input;
    }

    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        int sample;
        double frequency = ((SineWave)input).getFrequency();
        for (int i = 0; i < AudioClip.sample; i++ ) {
            if(  ( frequency * i / AudioClip.sampleRate) % 1 > 0.5) {
                sample = Short.MAX_VALUE;
            }
            else {
                sample = -Short.MAX_VALUE;
            }
            clip.setSample(i, sample);
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return input.getClip() != null;
    }

    @Override
    public void connectInput(AudioComponent input) {
        assert (false);
    }

    @Override
    public void setFrequency(float frequency) {

    }
}
