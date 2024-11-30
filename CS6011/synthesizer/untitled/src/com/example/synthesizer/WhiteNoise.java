package com.example.synthesizer;

import java.util.Random;

public class WhiteNoise implements AudioComponent {
    private AudioComponent input;
    //private final float frequency;

    public WhiteNoise(AudioComponent input) {
//        this.frequency = frequency;
        this.input = input;
    }

    @Override
    public AudioClip getClip() {
        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();
        Random random = new Random();
        int originalSample, afterSample;
        for (int i = 0; i < AudioClip.sample; i++ ) {
            originalSample = original.getSample(i);
            int randomsSample = random.nextInt(Short.MAX_VALUE)-Short.MAX_VALUE;
            afterSample = (int)((double)originalSample/ Short.MAX_VALUE * randomsSample);
            result.setSample(i, afterSample);
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return input.getClip() != null;
    }

    @Override
    public void connectInput(AudioComponent input) {
        this.input = input;

    }

    @Override
    public void setFrequency(float frequency) {

    }
}
