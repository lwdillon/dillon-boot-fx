import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class CameraApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建JavaFX窗口
        primaryStage.setTitle("Camera App");
        StackPane root = new StackPane();

        // 创建一个Media对象来捕捉摄像头视频流
        Media media = new Media("avcapture://0"); // 请根据你的摄像头设备路径进行调整

        // 创建一个MediaPlayer来播放摄像头视频流
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        // 创建一个MediaView来显示视频流
        MediaView mediaView = new MediaView(mediaPlayer);
        root.getChildren().add(mediaView);

        // 创建一个Scene并将其设置到Stage中
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);

        // 关闭窗口时停止摄像头
        primaryStage.setOnCloseRequest(event -> mediaPlayer.stop());

        // 显示窗口
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
