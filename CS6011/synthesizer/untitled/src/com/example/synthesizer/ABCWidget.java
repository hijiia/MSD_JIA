package com.example.synthesizer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javax.sound.sampled.*;

public class ABCWidget {
    private AnchorPane anchorPane;
    private AudioComponent audioComponent;
    private String name;
    private int frequency;
    private VBox baseLayout;
    private Clip clip;
    private AudioClip audioClip;

    public void destroyWidget(){
        if (baseLayout != null) {
            anchorPane.getChildren().remove(baseLayout);
            if(clip != null && clip.isOpen()){
                clip.stop();
                clip.close();
                clip = null;
            }
            baseLayout = null;
        }
    }

    ABCWidget(String name, int frequency){
        this.name = name;
        this.frequency = frequency;
        anchorPane = new AnchorPane();

        Label label = new Label(name);
        Button create = new Button();
        create.setPrefHeight(30);
        create.setPrefWidth(30);
        create.setOnAction(e -> {
            try {
                extendedWidget();
            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(label, create);
        anchorPane.getChildren().add(buttonBox);
    }

    //should have different acw classes for com.example.synthesizer.SineWave, com.example.synthesizer.VolumeAdjuster etc...
    public void extendedWidget() throws LineUnavailableException {
        if(baseLayout == null){
            baseLayout = new VBox();
            baseLayout.setAlignment(Pos.CENTER);
            baseLayout.setSpacing(10);
            baseLayout.setPadding(new Insets(100, 100, 100, 100));
            baseLayout.setPrefHeight(300);
            baseLayout.setPrefWidth(400);

            anchorPane.getChildren().add(baseLayout);

            // TOP
            HBox topSide = new HBox();
            baseLayout.getChildren().add(topSide);
            topSide.setAlignment(Pos.CENTER);
            topSide.setSpacing(10);
            //close button and connect ball
            Button close = new Button("x");
            close.setOnAction(e -> destroyWidget());
            topSide.getChildren().add(close);

            Circle connect = new Circle();
            connect.setRadius(10);
            connect.setFill(Color.LIGHTBLUE);
            topSide.getChildren().add(connect);

            //Bottom
            VBox bottomSide = new VBox();
            baseLayout.getChildren().add(bottomSide);
            bottomSide.setAlignment(Pos.CENTER);
            //create a name title and a slider
            Label title = new Label("Sine Wave " + this.name);
            title.autosize();
            bottomSide.getChildren().add(title);

            audioComponent = new SineWave(this.frequency);
            audioClip = audioComponent.getClip();
            clip = AudioSystem.getClip();
            AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );
            clip.open( format16, audioClip.getData(), 0, audioClip.getData().length );
        }
    }


    public AudioClip getAudioClip() {
        return audioClip;
    }

    public AudioComponent getAudioComponent() {
        return audioComponent;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public Clip getClip() {
        return clip;
    }
}