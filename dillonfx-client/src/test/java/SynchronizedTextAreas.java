import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SynchronizedTextAreas extends Application {

    @Override
    public void start(Stage primaryStage) {

        ScrollPane scrollPane1 = new ScrollPane();
        ScrollPane scrollPane2 = new ScrollPane();
        scrollPane1.setFitToWidth(true);
        scrollPane1.setFitToHeight(true);
        scrollPane2.setFitToWidth(true);
        scrollPane2.setFitToHeight(true);
        TextArea textArea1 = new TextArea();
        TextArea textArea2 = new TextArea();
        scrollPane1.setContent(textArea1);
        scrollPane2.setContent(textArea2);
        // Create some content for the text areas
        for (int i = 1; i <= 50; i++) {

            textArea2.appendText( (i) + "\n");
        }

        textArea2.textProperty().addListener((observableValue, s, t1) -> {
           int l= textArea2.getText().split("\n").length;
            textArea1.clear();
            for (int i = 0; i <l ; i++) {
                textArea1.appendText((i+1)+"\n");
            }
        });

        // Create scroll bars for both scroll panes
        ScrollBar scrollBar1 = new ScrollBar();
        ScrollBar scrollBar2 = new ScrollBar();

        // Bind the scroll bars to the respective scroll panes
        scrollPane1.vvalueProperty().bindBidirectional(scrollBar1.valueProperty());
        scrollPane2.vvalueProperty().bindBidirectional(scrollBar2.valueProperty());



        // Create a layout to arrange the text areas
        HBox root = new HBox(10);
        root.getChildren().addAll(textArea1, textArea2);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Synchronized Text Areas");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
