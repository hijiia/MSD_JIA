package com.example.synthesizer;

public class SineWave implements AudioComponent {
    private float frequency;


    public SineWave(float frequency) {
        this.frequency = frequency;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    @Override
    public AudioClip getClip(){
        double sampleValue;
        int sample;
        AudioClip clip = new AudioClip();
        for (int i = 0; i < AudioClip.sample; i++ ) {
            sampleValue = Math.sin(2 * Math.PI * frequency * i / AudioClip.getSR());
            sample = (int)(sampleValue * Short.MAX_VALUE);
            clip.setSample(i, sample);
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        assert (false);
    }
}
