package com.example.synthesizer;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class AudioComponentWidget {

    protected AnchorPane anchorPane;
    protected AnchorPane parentAnchorPane;
    protected AudioComponent audioComponent;
    protected String name;
    protected int frequency;
    protected HBox baseLayout;
    protected Clip clip;
    protected Slider frequencySlider;
    protected AudioClip audioClip;
    protected AudioFormat format16;
    protected Circle connect;
    protected Button create;
    protected Line line;
    protected boolean isConnected;
    protected boolean isFilterConnected;
    private final double[] mouseOffsetX = {0};
    private final double[] mouseOffsetY = {0};


    AudioComponentWidget(AnchorPane pane, String name) {
        this.parentAnchorPane = pane;
        this.name = name;
        frequencySlider = new Slider();
        anchorPane = new AnchorPane();
        audioComponent = new SineWave(440);
        connect = new Circle();
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        audioClip = audioComponent.getClip();
        format16 = new AudioFormat(44100, 16, 1, true, false);

        if (baseLayout == null) {
            baseLayout = new HBox();
            baseLayout.setAlignment(Pos.CENTER);
            baseLayout.setSpacing(10);
            baseLayout.setPadding(new Insets(10, 10, 10, 10));
            baseLayout.setPrefHeight(100);
            baseLayout.setPrefWidth(400);
            baseLayout.setStyle("-fx-background-color: #f6fdff;");
            //window drag
            windowDrag();

            anchorPane.getChildren().add(baseLayout);

            // Left with a name on the top and slider on the bottom
            VBox leftSide = new VBox();
            leftSide.setAlignment(Pos.CENTER);
            leftSide.setSpacing(10);
            leftSide.setPrefHeight(50);
            leftSide.setPrefWidth(300);
            baseLayout.getChildren().add(leftSide);
            Label title = new Label(name);
            leftSide.getChildren().add(title);
            leftSide.getChildren().add(frequencySlider);

            // Right with close and circle
            VBox rightSide = new VBox();
            baseLayout.getChildren().add(rightSide);
            rightSide.setAlignment(Pos.CENTER);
            rightSide.setSpacing(10);
            //close button and connect ball
            Button close = new Button("x");
            close.setOnAction(e -> destroyWidget());
            rightSide.getChildren().add(close);

            connect.setRadius(10);
            connect.setFill(Color.LIGHTBLUE);
            rightSide.getChildren().add(connect);
            connect.setOnMousePressed(this::createdLine);

        }
    }

    protected void destroyWidget() {
        if (baseLayout != null) {
            anchorPane.getChildren().remove(baseLayout);
            baseLayout = null;
            if (line != null) {
                parentAnchorPane.getChildren().remove(line);
                line = null;
                isConnected = false;
                isFilterConnected = false;
            }
        }
    }

    protected void createdLine(MouseEvent e) {
        // remove the line if exist
        if (line != null) {
            parentAnchorPane.getChildren().remove(line);
            line = null;
            isConnected = false;
            isFilterConnected = false;
        }
        baseLayout.setOnMouseDragged(null);


        // get the start point by circle's center
        Bounds bounds = connect.localToScene(connect.getBoundsInLocal());

        // create a line
        line = new Line();
        line.setStrokeWidth(4);
        line.setStartX(bounds.getCenterX());
        line.setStartY(bounds.getCenterY());
        line.setEndX(e.getSceneX());
        line.setEndY(e.getSceneY());
        parentAnchorPane.getChildren().add(line);


        // set the line movement
        parentAnchorPane.setOnMouseDragged(dragEvent -> {
            line.setEndX(dragEvent.getSceneX());
            line.setEndY(dragEvent.getSceneY());
        });

        // release window drag
        parentAnchorPane.setOnMouseReleased(releaseEvent -> {
            line.setEndX(releaseEvent.getSceneX());
            line.setEndY(releaseEvent.getSceneY());
            isConnected = true;

            baseLayout.setOnMouseDragged(mouseEvent -> {
                baseLayout.setLayoutX(mouseEvent.getSceneX() - mouseOffsetX[0]);
                baseLayout.setLayoutY(mouseEvent.getSceneY() - mouseOffsetY[0]);

                Bounds bounds2 = connect.localToScene(connect.getBoundsInLocal());
                if (line != null) {
                    line.setStartX(bounds2.getCenterX());
                    line.setStartY(bounds2.getCenterY());
                }


                mouseEvent.consume();
            });

            parentAnchorPane.setOnMouseDragged(null);
            parentAnchorPane.setOnMouseReleased(null);
        });

        e.consume();
    }

    protected void updateClip() throws LineUnavailableException {
        if (clip.isOpen()) {
            clip.close();
        }

    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public Button getButton() {
        return create;
    }

    public Clip getClip() {
        return clip;
    }

    public AudioComponent getAudioComponent() {
        return this.audioComponent;
    }

    public Slider getFrequencySlider() {
        return frequencySlider;
    }

    public Circle getConnect() {
        return connect;
    }

    protected void windowDrag(){
        baseLayout.setOnMousePressed(mouseEvent -> {
            mouseOffsetX[0] = mouseEvent.getSceneX() - baseLayout.getLayoutX();
            mouseOffsetY[0] = mouseEvent.getSceneY() - baseLayout.getLayoutY();
            mouseEvent.consume();
        });

        baseLayout.setOnMouseDragged(mouseEvent -> {
            baseLayout.setLayoutX(mouseEvent.getSceneX() - mouseOffsetX[0]);
            baseLayout.setLayoutY(mouseEvent.getSceneY() - mouseOffsetY[0]);
            mouseEvent.consume();
        });
    }

}


