package com.example.synthesizer;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.LineUnavailableException;

public class SineWaveWidget extends AudioComponentWidget {

    SineWaveWidget(AnchorPane pane, String name) throws LineUnavailableException {
        super(pane, name);
        frequencySlider.setMin(20);
        frequencySlider.setMax(2000);
        frequencySlider.setValue(440);
        frequencySlider.setShowTickLabels(true);
        frequencySlider.setShowTickMarks(true);
        frequencySlider.setMajorTickUnit(500);
        frequencySlider.setMinorTickCount(50);
        frequencySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.frequency = newValue.intValue();
            try {
                updateClip();
                System.out.println("Frequency updated: " + this.frequency);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        });
        // Create a clip and open it
        audioClip = audioComponent.getClip();
        clip.open( format16, audioClip.getData(), 0, audioClip.getData().length );
        baseLayout.setOnMouseClicked(event -> {
            System.out.println("has been clicked.");
        });

    }

    protected void updateClip() throws LineUnavailableException {
        super.updateClip();
            audioComponent.setFrequency(frequency);
            audioClip = audioComponent.getClip();

            clip.open(format16, audioClip.getData(), 0, audioClip.getData().length);
            System.out.println("Clip updated for frequency: " + frequency);
             System.out.println("updated one new Sine wave.");
    }

    protected void destroyWidget() {
        super.destroyWidget();
        if (clip != null) {
            if (clip.isOpen()) {
                clip.stop();
                clip.close();
            }
            clip = null;
        }
    }

    public AudioComponent getAudioComponent() {
        return super.getAudioComponent();
    }

    @Override
    protected void createdLine(MouseEvent e) {
        super.createdLine(e);
    }
}
