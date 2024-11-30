package com.example.synthesizer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayList;


public class SynthesizeApplication extends Application {

    private Line currentLine;

    public void handleSlider(){
        //
    }

    private void play(ArrayList<AudioComponentWidget> allWidgets) throws LineUnavailableException {
        for (AudioComponentWidget widget : allWidgets) {
            Clip clip = widget.getClip();
            if (clip != null) {
                clip.start();
                while (clip.getFramePosition() < AudioClip.sample || clip.isActive() || clip.isRunning()) {

                }
                System.out.println("Clip is playing.");
                clip.stop();
                System.out.println("Clip stopped.");
                clip.setFramePosition(0);
                System.out.println("Clip set position: " + clip.getFramePosition());
            }
        }

    }

    // and have all the component widget in an arraylist
    // class of component widget, have audioComponent and getter and a ap and apGetter, then draw everything on the ap

    @Override
    public void start(Stage stage) throws IOException, LineUnavailableException {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);
        stage.setTitle("Audio Synthesizer");
        stage.setScene(scene);

        //right
        VBox rightPane = new VBox();
        borderPane.setRight(rightPane);
        rightPane.setStyle("-fx-background-color: #8ad5df;");


        //center
        centerPane = new AnchorPane(); // a pane can let the widget go where user wants
        borderPane.setCenter(centerPane);
        allWidgets = new ArrayList<>();
        centerPane.setStyle("-fx-background-color: #c9e9f4;");

//        com.example.synthesizer.ABCWidget widgetA = new com.example.synthesizer.ABCWidget("A", 440);
//        centerPane.getChildren().add(widgetA.getAnchorPane());
//        AnchorPane.setTopAnchor(widgetA.getAnchorPane(), 30.0);  // position
//        AnchorPane.setLeftAnchor(widgetA.getAnchorPane(), 50.0);
////        allWidgets.add(widgetA);
//
//        com.example.synthesizer.ABCWidget widgetB = new com.example.synthesizer.ABCWidget("B", 330);
//        centerPane.getChildren().add(widgetB.getAnchorPane());
//        AnchorPane.setTopAnchor(widgetB.getAnchorPane(), 30.0);  // position
//        AnchorPane.setLeftAnchor(widgetB.getAnchorPane(), 100.0);
//        allWidgets.add(widgetB);

        //sine wave
        Button SineWaveButton = new Button("Sine Wave");
        SineWaveButton.setOnAction(e -> {
            try {
                SineWaveWidget sineWave = new SineWaveWidget(centerPane, "Sine Wave");
                centerPane.getChildren().add(sineWave.getAnchorPane());
                AnchorPane.setTopAnchor(sineWave.getAnchorPane(), 100.0);
                allWidgets.add(sineWave);
                selectedWidget = sineWave;
            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
        });
        rightPane.getChildren().add(SineWaveButton);

        //volume adjuster
        Button volumeButton = new Button("Volume Adjuster");
        volumeButton.setOnAction(e -> {
            if (selectedWidget != null) {
                allWidgets.remove(selectedWidget);
                VolumeAdjusterWidget volumeAdjuster = new VolumeAdjusterWidget("Volume Adjuster", selectedWidget.getAudioComponent(), centerPane);
                centerPane.getChildren().add(volumeAdjuster.getAnchorPane());
                AnchorPane.setTopAnchor(volumeAdjuster.getAnchorPane(), 300.0);
                allWidgets.add(volumeAdjuster);


                selectedWidget.getFrequencySlider().valueProperty().addListener((observable, oldValue, newValue) -> {
                    volumeAdjuster.updateInputComponent(selectedWidget.getAudioComponent());
                    System.out.println("Updated com.example.synthesizer.VolumeAdjuster with new com.example.synthesizer.SineWave frequency: " + newValue);
                });

            } else {
                System.out.println("No audio component.");
            }
        });
        rightPane.getChildren().add(volumeButton);

        //White Noise
        Button whiteNoiseButton = new Button("White Noise");
        whiteNoiseButton.setOnAction(e -> {
            if (selectedWidget != null) {
                allWidgets.remove(selectedWidget);
                WhiteNoiseWidget whiteNoise = new WhiteNoiseWidget("White Noise", selectedWidget.getAudioComponent(), centerPane);
                centerPane.getChildren().add(whiteNoise.getAnchorPane());
                AnchorPane.setTopAnchor(whiteNoise.getAnchorPane(), 300.0);
                AnchorPane.setLeftAnchor(whiteNoise.getAnchorPane(), 200.0);
                allWidgets.add(whiteNoise);

                selectedWidget.getFrequencySlider().valueProperty().addListener((observable, oldValue, newValue) -> {
                    whiteNoise.updateInputComponent(selectedWidget.getAudioComponent());
                    System.out.println("Updated com.example.synthesizer.WhiteNoise with new com.example.synthesizer.SineWave frequency: " + newValue);
                });

            } else {
                System.out.println("No audio component.");
            }
        });
        rightPane.getChildren().add(whiteNoiseButton);



        HBox hbox = new HBox();
        borderPane.setBottom(hbox);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(20, 0, 20, 0));
        Button playButton = new Button("play");
        playButton.setStyle("-fx-background-color: #c9e9f4;");
        playButton.setMinWidth(100);
        playButton.setMaxWidth(150);
        playButton.setPrefHeight(30);
        playButton.setOnAction(event -> {
            try {
                play(allWidgets);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        });
        hbox.getChildren().add(playButton);

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    private AnchorPane centerPane; // accept different widgets in the method
    private ArrayList<AudioComponentWidget> allWidgets; // add widgets and release them in method
    private AudioComponentWidget selectedWidget;

}