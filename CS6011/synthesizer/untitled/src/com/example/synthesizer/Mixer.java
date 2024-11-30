package com.example.synthesizer;

import java.util.ArrayList;

public class Mixer implements AudioComponent {
    private ArrayList<AudioComponent> inputs;

    public Mixer() {
        inputs = new ArrayList<>();
    }

    @Override
    public AudioClip getClip() {
        AudioClip result = new AudioClip();
        int original, inputSample, mixed;
        for (AudioComponent input : inputs) {
            AudioClip inputClip = input.getClip();
            for (int i = 0; i < AudioClip.sample; i++) {
                original = result.getSample(i);
                inputSample = inputClip.getSample(i);
                mixed = original + inputSample;
                if (mixed > Short.MAX_VALUE) {
                    mixed = Short.MAX_VALUE;
                } else if (mixed < Short.MIN_VALUE) {
                    mixed = Short.MIN_VALUE;
                }
                result.setSample(i, mixed); //updated in each loop
            }
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return !inputs.isEmpty();
    }

    @Override
    public void connectInput(AudioComponent input) {
        inputs.add(input);
    }

    @Override
    public void setFrequency(float frequency) {

    }
}
