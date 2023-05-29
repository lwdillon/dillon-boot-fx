import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import smile.clustering.KMeans;

public class DominantColorsTest extends Application {
    private static final int NUM_COLORS = 5; // 要提取的主要颜色数量

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String imagePath = "/Users/wenli/ProjectWorkspace/study/dillon-boot/dillon-boot-fx/src/main/resources/images/test.jpg"; // 图片路径

        try {
            BufferedImage image = ImageIO.read(new File(imagePath));

            // 将图像像素转换为RGB颜色值
            List<double[]> pixels = new ArrayList<>();
            int width = image.getWidth();
            int height = image.getHeight();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    double[] pixel = { red, green, blue };
                    pixels.add(pixel);
                }
            }

            // 使用K-Means聚类算法提取主要颜色
            double[][] data = new double[pixels.size()][3];
            for (int i = 0; i < pixels.size(); i++) {
                data[i] = pixels.get(i);
            }

            KMeans kmeans = KMeans.fit(data, NUM_COLORS);



            // 获取每个簇的中心颜色
            List<Color> dominantColors = new ArrayList<>();
            for (int i = 0; i < NUM_COLORS; i++) {
                double[] center = kmeans.centroids[i];
                int red = (int) center[0];
                int green = (int) center[1];
                int blue = (int) center[2];
                Color color = Color.rgb(red, green, blue);
                dominantColors.add(color);
            }

            // 创建JavaFX界面显示主要颜色
            FlowPane colorPane = new FlowPane();
            for (Color color : dominantColors) {
                Label colorLabel = new Label();
                colorLabel.setPrefSize(50, 50);
                colorLabel.setStyle("-fx-background-color: " + toHexCode(color) + ";");
                colorPane.getChildren().add(colorLabel);
            }
            colorPane.setAlignment(Pos.TOP_RIGHT);

            Pane pane = new Pane();
            // 创建渐变对象
            LinearGradient gradient = new LinearGradient(
                    0, 0,                      // 起始点坐标
                    1, 1,                      // 终点坐标
                    true,                      // 是否循环
                    javafx.scene.paint.CycleMethod.NO_CYCLE,  // 循环模式
                    new Stop(0, dominantColors.get(0)),     // 渐变的起始颜色和位置
                    new Stop(1, dominantColors.get(1))   // 渐变的结束颜色和位置
            );

            // 将渐变对象作为背景填充给Pane
            pane.setBackground(new javafx.scene.layout.Background(
                    new javafx.scene.layout.BackgroundFill(gradient, null, null)
            ));

            ImageView imageView = new ImageView("images/test.jpg");
            imageView.setFitWidth(200); // 设置适应的宽度
            imageView.setFitHeight(200); // 设置适应的高度
            Scene scene = new Scene(new StackPane(pane,imageView,colorPane), 600, 750);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dominant Colors");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toHexCode(Color color) {
        String red = Integer.toHexString((int) Math.round(color.getRed() * 255));
        String green = Integer.toHexString((int) Math.round(color.getGreen() * 255));
        String blue = Integer.toHexString((int) Math.round(color.getBlue() * 255));
        return "#" + padZero(red) + padZero(green) + padZero(blue);
    }

    private String padZero(String s) {
        return s.length() == 1 ? "0" + s : s;
    }
}
