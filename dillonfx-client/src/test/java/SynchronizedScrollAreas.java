import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SynchronizedScrollAreas extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextArea textArea1 = new TextArea();
        TextArea textArea2 = new TextArea();

        // Create a ScrollPane for each TextArea
        ScrollPane scrollPane1 = new ScrollPane();
        scrollPane1.setContent(textArea1);
        ScrollPane scrollPane2 = new ScrollPane();
        scrollPane2.setContent(textArea2);

        // Listen for changes in the vertical scroll value of the first TextArea
        textArea1.scrollTopProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Apply the scroll value to the second TextArea
                textArea2.setScrollTop(newValue.doubleValue());
            }
        });

        // Listen for changes in the vertical scroll value of the second TextArea
        textArea2.scrollTopProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Apply the scroll value to the first TextArea
                textArea1.setScrollTop(newValue.doubleValue());
            }
        });

        // Create a layout to arrange everything
        HBox root = new HBox(10);
        root.getChildren().addAll(scrollPane1, scrollPane2);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Synchronized Scroll Areas");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
