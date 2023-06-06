import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BorderWithTitleExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // 创建一个StackPane作为容器
        StackPane stackPane = new StackPane();

        // 创建一个矩形作为边框
        Rectangle border = new Rectangle(200, 100);
        border.setStroke(Color.BLACK);
        border.setFill(Color.TRANSPARENT);

        // 创建一个Label节点作为文字
        Label label = new Label("这是在边框上方的文字");
        label.setFont(Font.font("Arial", 16));
        label.setTextFill(Color.BLACK);

        // 将矩形和文字添加到StackPane中
        stackPane.getChildren().addAll(border, label);

        root.setCenter(stackPane);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Text on Top of Border Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
