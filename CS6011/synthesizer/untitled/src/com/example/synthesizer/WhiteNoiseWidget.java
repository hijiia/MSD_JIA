package com.example.synthesizer;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

public class WhiteNoiseWidget extends AudioComponentWidget {

    private WhiteNoise whiteNoise;
    private AudioComponent audioComponent;

    WhiteNoiseWidget(String name, AudioComponent inputComponent, AnchorPane pane) {
        super(pane, name);
        this.audioComponent = inputComponent;
        whiteNoise = new WhiteNoise(audioComponent);
        try {
            updateClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateClip() throws LineUnavailableException {
        super.updateClip();
        if (whiteNoise == null || whiteNoise.getClip() == null) {
            throw new IllegalStateException("com.example.synthesizer.WhiteNoise or its input is not initialized.");
        }

        if (clip == null) {
            clip = AudioSystem.getClip();
            format16 = new AudioFormat(44100, 16, 1, true, false);
            System.out.println("Try to initialize clip in com.example.synthesizer.WhiteNoise");
        }

        clip.open(format16, whiteNoise.getClip().getData(), 0, whiteNoise.getClip().getData().length);
        System.out.println("Clip updated for white noise");
    }

    public void updateInputComponent(AudioComponent newInput) {
        this.audioComponent = newInput;
        this.whiteNoise.connectInput(newInput);
    }

    @Override
    protected void createdLine(MouseEvent e) {
        parentAnchorPane.setOnMouseReleased(releaseEvent -> {
            WhiteNoiseWidget whiteNoiseWidget = new WhiteNoiseWidget("White Noise", this.getAudioComponent(), parentAnchorPane);
            parentAnchorPane.getChildren().add(whiteNoiseWidget.getAnchorPane());
        });

    }

}
