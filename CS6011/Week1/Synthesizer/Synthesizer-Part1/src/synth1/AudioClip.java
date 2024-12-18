package synth1;

import javax.swing.*;

public class AudioClip {
    // Static constants
    public static final int SAMPLE_RATE = 44100;  // Samples per second
    public static final double DURATION = 2.0;    // Duration in seconds
    private static final int TOTAL_SAMPLES = (int) (SAMPLE_RATE * DURATION);
    private static final int TOTAL_BYTES = TOTAL_SAMPLES * 2;  // Each sample is 2 bytes (16 bits)

    // Member variable to store the actual audio data as bytes
    private byte[] data;

    // Constructor to initialize the data array
    public AudioClip() {
        this.data = new byte[TOTAL_BYTES];
    }

    // Get the sample at a given index
    public int getSample(int index) {
        int lowByte = Byte.toUnsignedInt(data[2 * index]);       // Lower 8 bits (stored first)
        int highByte = data[2 * index + 1];  // Upper 8 bits (sign-extended automatically)
        return (highByte << 8) | lowByte;    // Combine bytes into a 16-bit signed value
    }

    // Set the sample at a given index
    public void setSample(int index, int value) {
        data[2 * index] = (byte) (value & 0xFF);            // Lower 8 bits (least significant byte)
        data[2 * index + 1] = (byte) ((value >> 8) & 0xFF); // Upper 8 bits (most significant byte)
    }

    // Return a copy of the byte array to avoid aliasing
    public byte[] getData() {
        return java.util.Arrays.copyOf(data, data.length);
    }
}

