import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LineNumberTextAreaExample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TextArea textArea = new TextArea();
        Text lineNumberText = new Text();

        // 创建一个HBox来容纳行号和TextArea
        HBox hbox = new HBox();
        hbox.getChildren().addAll(lineNumberText, textArea);

        // 监听TextArea的文本更改事件
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers(textArea, lineNumberText);
        });

        // 初始化行号
        updateLineNumbers(textArea, lineNumberText);

        Scene scene = new Scene(hbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Line Number TextArea Example");
        primaryStage.show();
    }

    // 更新行号
    private void updateLineNumbers(TextArea textArea, Text lineNumberText) {
        String text = textArea.getText();
        int numLines = text.isEmpty() ? 0 : text.split("\n").length;
        StringBuilder lineNumbers = new StringBuilder();
        for (int i = 1; i <= numLines; i++) {
            lineNumbers.append(i).append("\n");
        }
        lineNumberText.setText(lineNumbers.toString());
    }
}
