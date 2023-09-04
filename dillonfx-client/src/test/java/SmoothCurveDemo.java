import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SmoothCurveDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a list of data points (y-values)
        List<Double> dataPoints = new ArrayList<>();
        dataPoints.add(50.0);
        dataPoints.add(100.0);
        dataPoints.add(120.0);
        dataPoints.add(150.0);
        dataPoints.add(160.0);
        dataPoints.add(190.0);
        dataPoints.add(220.0);
        // Add more data points as needed
        
        // Create a Path to represent the smooth curve
        Path path = createSmoothCurve(dataPoints);
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.BLUE));
        path.setStroke(gradient);
        path.setStrokeWidth(5);
        // Create a Pane and add the path to it
        Pane root = new Pane(path);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smooth Curve Demo");
        primaryStage.show();
    }

    // Create a smooth curve Path based on data points
    private Path createSmoothCurve(List<Double> dataPoints) {
        Path path = new Path();
        path.setStroke(Color.BLUE);
        path.setStrokeWidth(2);

        // Move to the first data point
        path.getElements().add(new MoveTo(0, dataPoints.get(0)));

        // Create cubic curve segments between data points
        for (int i = 0; i < dataPoints.size() - 1; i++) {
            double x0 = i * 50;
            double y0 = dataPoints.get(i);
            double x1 = (i + 1) * 50;
            double y1 = dataPoints.get(i + 1);
            path.getElements().add(new CubicCurveTo(x0 + 25, y0, x1 - 25, y1, x1, y1));
        }

        return path;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
