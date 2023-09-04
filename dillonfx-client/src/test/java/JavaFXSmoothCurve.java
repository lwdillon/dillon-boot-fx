import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.CubicCurveTo;
import javafx.stage.Stage;

public class JavaFXSmoothCurve extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        Path path = new Path();
        path.getElements().add(new MoveTo(50, 150));
        path.getElements().add(new CubicCurveTo(150, 50, 250, 250, 350, 150));
        path.getElements().add(new CubicCurveTo(450, 50, 550, 250, 650, 150));
        path.setStroke(Color.web("#6a6a9f"));
        path.setStrokeWidth(2);
        path.setFill(null);

        root.getChildren().add(path);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Smooth Curve");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
