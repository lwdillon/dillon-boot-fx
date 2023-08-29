package com.lw.fx.client.view.home;

import com.kitfox.svg.app.beans.SVGIcon;
import de.saxsys.mvvmfx.FxmlView;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.chart.RadarChartMode;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;


public class HomeView implements FxmlView<HomeViewModel>, Initializable {
    private static final Random RND = new Random();

    private Tile radialChartTile;


    @FXML
    private VBox chartPane1;
    @FXML
    private VBox chartPane2;
    @FXML
    private VBox chartPane3;
    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;
    @FXML
    private ImageView imgView1;
    @FXML
    private ImageView imgView2;
    @FXML
    private ImageView imgView3;
    @FXML
    private ImageView imgView4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        SVGIcon svgIcon1 = new SVGIcon();
        SVGIcon svgIcon2 = new SVGIcon();
        SVGIcon svgIcon3 = new SVGIcon();
        SVGIcon svgIcon4 = new SVGIcon();

        try {
            svgIcon1.setSvgURI(HomeView.class.getResource("/images/aaa.svg").toURI());
            svgIcon2.setSvgURI(HomeView.class.getResource("/images/bbb.svg").toURI());
            svgIcon3.setSvgURI(HomeView.class.getResource("/images/ccc.svg").toURI());
            svgIcon4.setSvgURI(HomeView.class.getResource("/images/ddd.svg").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        imgView1.setImage(SwingFXUtils.toFXImage((BufferedImage) svgIcon1.getImage(), null));
        imgView2.setImage(SwingFXUtils.toFXImage((BufferedImage) svgIcon2.getImage(), null));
        imgView3.setImage(SwingFXUtils.toFXImage((BufferedImage) svgIcon3.getImage(), null));
        imgView4.setImage(SwingFXUtils.toFXImage((BufferedImage) svgIcon4.getImage(), null));

        ChartData chartData1 = new ChartData("Item 1", 24.0, Tile.GREEN);
        ChartData chartData2 = new ChartData("Item 2", 10.0, Tile.BLUE);
        ChartData chartData3 = new ChartData("Item 3", 12.0, Tile.RED);
        ChartData chartData4 = new ChartData("Item 4", 13.0, Tile.YELLOW_ORANGE);
        ChartData chartData5 = new ChartData("Item 5", 13.0, Tile.BLUE);
        ChartData chartData6 = new ChartData("Item 6", 13.0, Tile.BLUE);
        ChartData chartData7 = new ChartData("Item 7", 13.0, Tile.BLUE);
        ChartData chartData8 = new ChartData("Item 8", 13.0, Tile.BLUE);


        ChartData smoothChartData1 = new ChartData("Item 1", RND.nextDouble() * 25, Tile.BLUE);
        ChartData smoothChartData2 = new ChartData("Item 2", RND.nextDouble() * 25, Tile.BLUE);
        ChartData smoothChartData3 = new ChartData("Item 3", RND.nextDouble() * 25, Tile.BLUE);
        ChartData smoothChartData4 = new ChartData("Item 4", RND.nextDouble() * 25, Tile.BLUE);

        radialChartTile = TileBuilder.create().skinType(Tile.SkinType.RADIAL_CHART)
                .textVisible(false)
                .backgroundColor(Color.TRANSPARENT)
                .textColor(Color.RED)
                .chartData(chartData1, chartData2, chartData3, chartData4)
                .build();
        HBox.setHgrow(radialChartTile, Priority.ALWAYS);
        chartPane3.getChildren().add(radialChartTile);


        Tile donutChartTile = TileBuilder.create()
                .skinType(Tile.SkinType.DONUT_CHART)
                .textVisible(false)
                .backgroundColor(Color.TRANSPARENT)
                .textColor(Color.RED)
                .valueColor(Color.GREEN)
                .chartData(chartData1, chartData2, chartData3, chartData4)
                .build();

        HBox.setHgrow(donutChartTile, Priority.ALWAYS);
        chartPane2.getChildren().add(donutChartTile);


        Tile radarChartTile = TileBuilder.create().skinType(Tile.SkinType.RADAR_CHART)
                .minValue(0)
                .maxValue(50)
                .textVisible(false)
                .backgroundColor(Color.TRANSPARENT)
                .unit("Unit")
                .radarChartMode(RadarChartMode.SECTOR)
                .gradientStops(new Stop(0.00000, Color.TRANSPARENT),
                        new Stop(0.00001, Color.web("#3552a0")),
                        new Stop(0.09090, Color.web("#456acf")),
                        new Stop(0.27272, Color.web("#45a1cf")),
                        new Stop(0.36363, Color.web("#30c8c9")),
                        new Stop(0.45454, Color.web("#30c9af")),
                        new Stop(0.50909, Color.web("#56d483")),
                        new Stop(0.72727, Color.web("#9adb49")),
                        new Stop(0.81818, Color.web("#efd750")),
                        new Stop(0.90909, Color.web("#ef9850")),
                        new Stop(1.00000, Color.web("#ef6050")))
                .text("Test")
                .chartData(chartData1, chartData2, chartData3, chartData4,
                        chartData5, chartData6, chartData7, chartData8)
                .tooltipText("")
                .animated(true)
                .build();

        HBox.setHgrow(radarChartTile, Priority.ALWAYS);
        chartPane1.getChildren().add(radarChartTile);


        tab1.setContent(stackedAreaChart());
        tab2.setContent(barChart());
    }

    private StackedAreaChart stackedAreaChart() {
        //snippet_2:start
        var x = new NumberAxis(1, 31, 1);

        var y = new NumberAxis();

        var april = new XYChart.Series<Number, Number>();
        april.setName("April");
        IntStream.range(1, 32).forEach(i -> april.getData().add(
                new XYChart.Data<>(i, RND.nextDouble() * 20000)
        ));

        var may = new XYChart.Series<Number, Number>();
        may.setName("May");
        IntStream.range(1, 32).forEach(i -> may.getData().add(
                new XYChart.Data<>(i, RND.nextDouble() * 80000)
        ));

        StackedAreaChart chart = new StackedAreaChart<>(x, y);
        chart.setCreateSymbols(false);
        chart.setMinHeight(300);
//        chart.setStyle("-fx-stroke-type: transparent; -fx-fill: rgba(0, 0, 255, 0.3); -fx-stroke: rgba(0, 0, 255, 0.5); -fx-stroke-width: 1px;");
        chart.getData().addAll(april, may);
        //snippet_2:end


        return chart;
    }

    private BarChart barChart() {
        //snippet_3:start


        var x = new CategoryAxis();

        var y = new NumberAxis();


        var january = new XYChart.Series<String, Number>();
        for (int i = 1; i < 13; i++) {
            january.getData().add(new XYChart.Data(i + "月", RND.nextInt(600, 8000)));
        }

        var chart = new BarChart<>(x, y);
        chart.setMinHeight(300);
        chart.getData().addAll(january);


        return chart;
    }
}
