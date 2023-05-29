import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smile.clustering.KMeans;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("使用K-Means聚类算法从图像中提取主要颜色");

        StackPane stackPane = new StackPane();
        ImageView view = new ImageView("images/beijing.png");
        stackPane.getChildren().add(view);
        Scene scene = new Scene(stackPane, 600, 750);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void getIclabe(String url){
        int numColors = 5; // 要提取的主要颜色数量

        try {
            BufferedImage image = ImageIO.read(new File(url));

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

            KMeans kmeans = KMeans.fit(data, numColors);


            // 获取每个簇的中心颜色
            List<java.awt.Color> dominantColors = new ArrayList<>();
            for (int i = 0; i < numColors; i++) {
                double[] center = kmeans.centroids[i];
                int red = (int) center[0];
                int green = (int) center[1];
                int blue = (int) center[2];
                java.awt.Color color = new java.awt.Color(red, green, blue);
                dominantColors.add(color);
            }

            // 输出主要颜色
            System.out.println("Dominant Colors:");
            for (Color color : dominantColors) {
                System.out.println(color.getRGB());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        launch(args);
    }
}