import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class JavaFXRingWithArc extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        Circle outerCircle = new Circle(150, 150, 100);
        outerCircle.setFill(null);
        outerCircle.setStroke(Color.BLACK);
        outerCircle.setStrokeWidth(5);

        Arc arc = new Arc(150, 150, 90, 90, 45, 180);
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.RED);
        arc.setStrokeWidth(10);
        arc.setFill(null);

        root.getChildren().addAll(outerCircle, arc);

        Scene scene = new Scene(root, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Ring with Arc");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
