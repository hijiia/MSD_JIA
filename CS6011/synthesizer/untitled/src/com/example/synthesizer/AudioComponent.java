package com.example.synthesizer;

public interface AudioComponent {
    AudioClip getClip();
    boolean hasInput(); //return false
    void connectInput (AudioComponent input);
    void setFrequency(float frequency);

    // removeInput
}

