package com.example.synthesizer;

import java.util.Arrays;

public class AudioClip {

    protected static final int sampleRate = 44100;
    protected static final int durationTime = 2;
    protected static final int sample = sampleRate * durationTime;
    protected byte[] samples = new byte[sample * 2];
    //private int samples;

    public int getSample(int index) {
        byte lower = samples[2*index];
        byte upper = samples[2*index+1];
        return (upper << 8) | (lower& 0xFF);
    }

    public void setSample(int index, int value) {
        byte lower = samples[2*index];
        byte upper = samples[2*index+1];
        samples[2*index] = (byte) (value & 0xFF);
        samples[2*index+1] = (byte) (value >> 8);
    }

    public static int getSR() {
        return sampleRate;
    }

    public static int getDurationTime() {
        return durationTime;
    }

    public byte[] getData() {
        byte[] bytesCopy = new byte[samples.length];
        bytesCopy = Arrays.copyOf(samples, samples.length);
        return bytesCopy;
    }

    public AudioClip getClip() {
        return this;
    }


}

