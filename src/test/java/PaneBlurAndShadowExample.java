import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PaneBlurAndShadowExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setPrefSize(400, 300);

        // 创建一个 StackPane 作为毛玻璃效果的背景
        StackPane blurPane = new StackPane();
        blurPane.setPrefSize(400, 300);
        BoxBlur blurEffect = new BoxBlur(10, 10, 3);
        blurPane.setEffect(blurEffect);
        blurPane.setStyle("-fx-background-color: rgba(63,62,62,0.5);"); // 设置背景颜色和透明度

        // 创建一个 StackPane 作为内容，并添加阴影效果
        StackPane contentPane = new StackPane();
        contentPane.setPrefSize(300, 200);
        contentPane.setStyle("-fx-background-color: white;"); // 设置内容面板颜色
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(100);
        dropShadow.setSpread(0.5);
        contentPane.setEffect(dropShadow);

        // 将内容面板添加到毛玻璃面板中
        blurPane.getChildren().add(contentPane);

        // 将毛玻璃面板添加到根 StackPane 中
        root.getChildren().add(blurPane);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Pane Blur and Shadow Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
