package com.example.synthesizer;

public class VolumeAdjuster implements AudioComponent {
    private AudioComponent input;
    private double volume;

    public VolumeAdjuster(AudioComponent input, double volume) {
        this.input = input;
       if (volume < 0.0) {
           this.volume = 0.0;
       }else if (volume > 2.0) {
           this.volume = 2.0;
       }
       else{
           this.volume = volume;
       }
    }

    public VolumeAdjuster(AudioClip clip, double volume) {
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public AudioClip getClip() {

        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();

        for (int i = 0; i < AudioClip.sample; i++){
            int originalSample = original.getSample(i);
            int adjustedSample = (int) (originalSample * volume);
            if (adjustedSample > Short.MAX_VALUE) {
                adjustedSample = Short.MAX_VALUE;
            } else if (adjustedSample < Short.MIN_VALUE) {
                adjustedSample = Short.MIN_VALUE;
            }
            result.setSample(i, adjustedSample);
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return input.getClip() != null;
    }

    @Override
    public void connectInput(AudioComponent input) {
        //connect to the sine wave
        //and ask audio clip from the sine wave
        this.input = input;
    }

    @Override
    public void setFrequency(float frequency) {

    }


}
