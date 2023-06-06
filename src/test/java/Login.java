import atlantafx.base.controls.Message;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import smile.clustering.KMeans;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends Application {


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("使用K-Means聚类算法从图像中提取主要颜色");

        StackPane stackPane = new StackPane();
        var msg = new Notification(
                "aaa",
                new FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        );

        msg.getStyleClass().add(Styles.DANGER);
        msg.setPrefHeight(Region.USE_PREF_SIZE);
        msg.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(msg, Pos.TOP_RIGHT);
        StackPane.setMargin(msg, new Insets(10, 10, 0, 0));

        var btn = new Button("Show");

        msg.setOnClose(e -> {
            var out = Animations.slideInDown(msg, Duration.millis(250));
            out.setOnFinished(f -> stackPane.getChildren().remove(msg));
            out.playFromStart();
        });
        btn.setOnAction(e -> {
            var in = Animations.slideInDown(msg, Duration.millis(250));
            if (!stackPane.getChildren().contains(msg)) {
                stackPane.getChildren().add(msg);
            }
            in.playFromStart();
        });
        //snippet_4:end

        var box = new VBox( btn);
        box.setPadding(new Insets(0, 0, 10, 0));

        ImageView view = new ImageView("images/beijing.png");
        stackPane.getChildren().add(box);
        Scene scene = new Scene(stackPane, 600, 750);
        primaryStage.setScene(scene);

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}