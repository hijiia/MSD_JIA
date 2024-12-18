import java.util.Arrays;

public class AudioClip {
    public static final double DURATION_SECONDS = 2.0;
    public static final int SAMPLE_RATE = 44100;
    public static final int TOTAL_SAMPLES = (int) (SAMPLE_RATE * DURATION_SECONDS);
    private final byte[] data = new byte[TOTAL_SAMPLES * 2]; // 16-bit per sample (2 bytes)

    public int getSample(int index) {
        int low = data[2 * index] & 0xFF; // lower byte (masked to prevent sign extension)
        int high = data[2 * index + 1] << 8; // upper byte
        return high | low;
    }

    public void setSample(int index, int value) {
        data[2 * index] = (byte) (value & 0xFF);         // lower byte
        data[2 * index + 1] = (byte) ((value >> 8) & 0xFF); // upper byte
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }
}
