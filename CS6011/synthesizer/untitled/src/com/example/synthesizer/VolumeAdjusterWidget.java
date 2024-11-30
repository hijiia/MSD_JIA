package com.example.synthesizer;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

public class VolumeAdjusterWidget extends AudioComponentWidget {

    private double volume;
    private VolumeAdjuster volumeAdjuster;
    private AudioComponent audioComponent;

    VolumeAdjusterWidget(String name, AudioComponent inputComponent, AnchorPane pane) {
        super(pane, name);
        this.volume = 1.0;
        this.audioComponent = inputComponent;

        volumeAdjuster = new VolumeAdjuster(audioComponent, volume);

        frequencySlider.setMin(0);
        frequencySlider.setMax(2);
        frequencySlider.setValue(volume);
        frequencySlider.setShowTickLabels(true);
        frequencySlider.setMajorTickUnit(0.2);
        frequencySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.volume = newValue.doubleValue();
            try {
                updateClip();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void updateClip() throws LineUnavailableException {
        super.updateClip();
        if (volumeAdjuster == null || volumeAdjuster.getClip() == null) {
            throw new IllegalStateException("com.example.synthesizer.VolumeAdjuster or its input is not initialized.");
        }

        if (clip == null) {
            clip = AudioSystem.getClip();
            format16 = new AudioFormat(44100, 16, 1, true, false);
            System.out.println("Try to initialize clip in volume adjuster");
        }


        volumeAdjuster.setVolume(volume);
        clip.open(format16, volumeAdjuster.getClip().getData(), 0, volumeAdjuster.getClip().getData().length);
        System.out.println("Clip updated for volume: " + volume);
    }

    public void updateInputComponent(AudioComponent newInput) {
        this.audioComponent = newInput;
        this.volumeAdjuster.connectInput(newInput);
    }

    @Override
    protected void createdLine(MouseEvent e) {
        parentAnchorPane.setOnMouseReleased(releaseEvent -> {
            VolumeAdjusterWidget volumeAdjuster = new VolumeAdjusterWidget("Volume Adjuster", this.getAudioComponent(), parentAnchorPane);
            parentAnchorPane.getChildren().add(volumeAdjuster.getAnchorPane());
        });

    }

}
