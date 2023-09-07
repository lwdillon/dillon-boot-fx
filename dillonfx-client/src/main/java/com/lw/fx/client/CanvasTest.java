package com.lw.fx.client;

import com.lw.fx.client.CanvasControl;
import eu.hansolo.fx.charts.data.MapConnection;
import eu.hansolo.fx.charts.data.WeightedMapPoints;
import eu.hansolo.fx.charts.tools.MapPoint;
import eu.hansolo.fx.charts.world.World;
import eu.hansolo.fx.charts.world.World.Resolution;
import eu.hansolo.fx.charts.world.WorldBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 30.11.20
 * Time: 13:21
 */
public class CanvasTest extends Application {

    @Override
    public void init() {


    }

    @Override
    public void start(Stage stage) {
        StackPane pane = new StackPane(new CanvasControl());

        Scene scene = new Scene(pane);

        stage.setTitle("Worldmap Connections");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}