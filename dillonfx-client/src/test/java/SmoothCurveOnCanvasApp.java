import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SmoothCurveOnCanvasApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a canvas
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Generate sample data
        List<Double> data = generateSampleData();

        // Calculate canvas dimensions and scaling
        double xScale = canvas.getWidth() / (data.size() - 1);
        double yScale = canvas.getHeight() / (getMaxValue(data) - getMinValue(data));

        // Start drawing
        gc.beginPath();
        for (int i = 0; i < data.size(); i++) {
            double x = i * xScale;
            double y = canvas.getHeight() - (data.get(i) - getMinValue(data)) * yScale;
            if (i == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }

        // Set line style and stroke
        gc.setStroke(javafx.scene.paint.Color.BLUE);
        gc.setLineWidth(2);
        gc.stroke();

        // Create the scene
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, 800, 600);

        // Set the scene on the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smooth Curve on Canvas Example");
        primaryStage.show();
    }

    // Generate sample data (sine wave)
    private List<Double> generateSampleData() {
        Random random = new Random();
        List<Double> data = new ArrayList<>();
        for (double x = 0; x <= 10; x += 0.1) {
            double y = Math.sin(random.nextDouble()*50);
            data.add(y);
        }
        return data;
    }

    // Find the minimum value in a list of doubles
    private double getMinValue(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    // Find the maximum value in a list of doubles
    private double getMaxValue(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
