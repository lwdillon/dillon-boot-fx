import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LineNumberedTextArea extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        TextArea textArea = new TextArea();
        textArea.textProperty().addListener((observable, oldValue, newValue) -> updateLineNumbers(textArea));

        VBox lineNumberArea = new VBox();
        lineNumberArea.setAlignment(Pos.TOP_RIGHT);
        lineNumberArea.setStyle("-fx-background-color: #f4f4f4;");

        root.setCenter(textArea);
        root.setLeft(lineNumberArea);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateLineNumbers(TextArea textArea) {
        String[] lines = textArea.getText().split("\n");
        StringBuilder lineNumbersText = new StringBuilder();

        for (int i = 1; i <= lines.length; i++) {
            lineNumbersText.append(i).append("\n");
        }

        Label lineNumbersLabel = new Label(lineNumbersText.toString());
        lineNumbersLabel.setStyle("-fx-font-family: monospace;");
        ((VBox) ((BorderPane) textArea.getParent()).getLeft()).getChildren().setAll(lineNumbersLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
