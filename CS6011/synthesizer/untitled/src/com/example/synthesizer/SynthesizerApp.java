package com.example.synthesizer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.sound.sampled.*;

public class SynthesizerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create UI components
        Button playSineButton = new Button("Play Sine Wave");
        Button playSquareButton = new Button("Play Square Wave");
        Button playWhiteNoiseButton = new Button("Play White Noise");

        Slider frequencySlider = new Slider(100, 1000, 440); // Frequency range from 100 Hz to 1000 Hz
        frequencySlider.setShowTickLabels(true);
        frequencySlider.setShowTickMarks(true);

        // Set button actions
        playSineButton.setOnAction(e -> playSound(new SineWave((int) frequencySlider.getValue())));
        playSquareButton.setOnAction(e -> playSound(new SquareWave(new SineWave((int) frequencySlider.getValue()))));
        playWhiteNoiseButton.setOnAction(e -> playSound(new WhiteNoise(new SineWave(440)))); // Example for noise

        // Layout setup
        VBox layout = new VBox(10);
        layout.getChildren().addAll(frequencySlider, playSineButton, playSquareButton, playWhiteNoiseButton);

        // Scene setup
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Synthesizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playSound(AudioComponent component) {
        try {
            AudioClip clip = component.getClip();
            AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);
            Clip c = AudioSystem.getClip();
            c.open(format16, clip.getData(), 0, clip.getData().length);

            System.out.println("Playing sound...");
            c.start();
            c.loop(0);

            // Wait for the clip to finish
            new Thread(() -> {
                while (c.isRunning() || c.isActive()) {
                    // Wait for the sound to play
                }
                c.close();
            }).start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}