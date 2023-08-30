package com.lw.fx.client.view.home;

import atlantafx.base.theme.Styles;
import com.lw.fx.client.Resources;
import com.lw.fx.client.domain.ActiveProjects;
import com.lw.fx.client.domain.Roects;
import com.lw.fx.client.event.DefaultEventBus;
import com.lw.fx.client.event.EventBus;
import com.lw.fx.client.event.ThemeEvent;
import com.lw.fx.client.theme.ThemeManager;
import com.lw.fx.client.util.NodeUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.Helper;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.datafaker.Faker;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import static atlantafx.base.theme.Tweaks.*;


public class DashboardView implements FxmlView<DashboardViewModel>, Initializable {
    private static final EventBus EVENT_BUS = DefaultEventBus.getInstance();

    private final Random random = new Random();

    @FXML
    private TableColumn<ActiveProjects, String> assigneeCol;

    @FXML
    private TableView<ActiveProjects> activeProjectsTableView;

    @FXML
    private BarChart barChart;

    @FXML
    private VBox circularProgressTileBox;

    @FXML
    private TableColumn<?, ?> clientsCol;

    @FXML
    private VBox currentStaticsticBox;
    @FXML
    private VBox smoothAreaBox;
    @FXML
    private VBox smoothCharBox2;

    @FXML
    private TableColumn<?, ?> dueDateCol;

    @FXML
    private TableColumn<?, ?> noCol;

    @FXML
    private TableColumn<ActiveProjects, Double> progressCol;

    @FXML
    private TableColumn<ActiveProjects, String> projectLeadCol;

    @FXML
    private TableColumn<?, ?> projectNameCol;

    @FXML
    private TableColumn<?, ?> roectProjectNameCol;

    @FXML
    private TableColumn<?, ?> roectsDueDateCol;

    @FXML
    private TableColumn<?, ?> roectsStartDateCol;

    @FXML
    private TableColumn<Roects, String> roectsStatusCol;

    @FXML
    private TableView<Roects> roectsTableView;

    @FXML
    private TableColumn<ActiveProjects, String> statusCol;

    private Tile circularProgressTile;
    private Tile radialChartTile;
    private Tile smoothAreaChartTile;
    private Tile smoothAreaChartTile2;
    private ChartData chartData1;
    private ChartData chartData2;
    private ChartData chartData3;
    private ChartData chartData4;

    private ChartData smoothChartData1;
    private ChartData smoothChartData2;
    private ChartData smoothChartData3;
    private ChartData smoothChartData4;
    private ChartData smoothChartData5;
    private ChartData smoothChartData6;
    private ChartData smoothChartData7;
    private ChartData smoothChartData8;

    private XYChart.Series<String, Number> january;
    private XYChart.Series<String, Number> february;
    private XYChart.Series<String, Number> march;
    @InjectViewModel
    private DashboardViewModel dashboardViewModel;

    private long lastTimerCall;

    private List<String> countries;

    private AnimationTimer timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final var faker = new Faker();
        countries = IntStream.range(0, 5).boxed()
                .map(i -> faker.country().countryCode3().toUpperCase())
                .toList();

        circularProgressTile = TileBuilder.create()
                .skinType(Tile.SkinType.CIRCULAR_PROGRESS)
                .prefSize(250, 350)

//                .title("CircularProgress Tile")
//                .text("Some text")
                .unit(Helper.PERCENTAGE)
                .backgroundColor(Color.TRANSPARENT)
                .barBackgroundColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#334155") : Color.web("#afb8c1"))
                .valueColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))
                .unitColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))

                .build();
        circularProgressTile.setValue(62.5D);
        circularProgressTileBox.getChildren().addAll(circularProgressTile);

        barChart();

        // Chart Data
        chartData1 = new ChartData("Item 1", 24.0, Color.web("#3B82F6"));
        chartData2 = new ChartData("Item 2", 70.0, Color.web("#22C55E"));
        chartData3 = new ChartData("Item 3", 62.0, Color.web("#F97316"));
        chartData4 = new ChartData("Item 4", 33.0, Color.web("#EF4444"));


        radialChartTile = TileBuilder.create()
                .skinType(Tile.SkinType.RADIAL_CHART)
                .prefSize(100, 515)
//                .title("RadialChart Tile")
//                .text("Some text")
                .textColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))
                .backgroundColor(Color.TRANSPARENT)
                .textVisible(false)
                .chartData(chartData1, chartData2, chartData3, chartData4)
                .build();

        currentStaticsticBox.getChildren().add(radialChartTile);


        noCol.setCellValueFactory(new PropertyValueFactory<>("no"));
        roectProjectNameCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        roectsStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        roectsDueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        roectsStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        roectsStatusCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button button = new Button(item);
                        int index = random.nextInt(3);
                        switch (index) {
                            case 0:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.SUCCESS);
                                break;
                            case 1:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.DANGER);
                                break;
                            case 2:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
                                break;
                            default:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
                        }
                        HBox box = new HBox(button);
                        box.setAlignment(Pos.CENTER);
                        box.setPadding(new Insets(10, 10, 10, 10));
                        setGraphic(box);
                    }
                }
            };
        });
        clientsCol.setCellValueFactory(new PropertyValueFactory<>("clients"));


        projectNameCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectLeadCol.setCellValueFactory(new PropertyValueFactory<>("projectLead"));
        projectLeadCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Image image = new Image(Resources.getResourceAsStream("images/avatars/avatar" + (random.nextInt(5) + 1) + ".png"));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitHeight(40);
                        imageView.setFitWidth(40);
                        HBox box = new HBox(10, imageView, new Label(item));
                        box.setAlignment(Pos.CENTER_LEFT);
                        box.setPadding(new Insets(5, 5, 5, 5));
                        setGraphic(box);
                    }
                }
            };
        });
        progressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressCol.setCellFactory(ProgressBarTableCell.forTableColumn());

        assigneeCol.setCellValueFactory(new PropertyValueFactory<>("assignee"));
        assigneeCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox box = new HBox();
                        for (int i = 0; i < random.nextInt(3) + 1; i++) {
                            Image image = new Image(Resources.getResourceAsStream("images/avatars/avatar" + (random.nextInt(5) + 1) + ".png"));
                            ImageView imageView = new ImageView(image);
                            imageView.setFitHeight(35);
                            imageView.setFitWidth(35);
                            box.getChildren().add(imageView);
                        }


                        box.setAlignment(Pos.CENTER_LEFT);
                        box.setPadding(new Insets(10, 10, 10, 10));
                        setGraphic(box);
                    }
                }
            };
        });
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button button = new Button(item);
                        int index = random.nextInt(3);
                        switch (index) {
                            case 0:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.SUCCESS);
                                break;
                            case 1:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.DANGER);
                                break;
                            case 2:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
                                break;
                            default:
                                button.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
                        }
                        HBox box = new HBox(button);
                        box.setAlignment(Pos.CENTER);
                        box.setPadding(new Insets(5, 5, 5, 5));
                        setGraphic(box);
                    }
                }
            };
        });
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));


        roectsTableView.setItems(dashboardViewModel.getRoects());
        activeProjectsTableView.setItems(dashboardViewModel.getActiveProjects());


        smoothChartData1 = new ChartData("Item 1", random.nextDouble() * 90, Color.web("#3B82F6"));
        smoothChartData2 = new ChartData("Item 2", random.nextDouble() * 90, Color.web("#3B82F6"));
        smoothChartData3 = new ChartData("Item 3", random.nextDouble() * 90, Color.web("#3B82F6"));
        smoothChartData4 = new ChartData("Item 4", random.nextDouble() * 90, Color.web("#3B82F6"));

        smoothChartData5 = new ChartData("Item 1", random.nextDouble() * 90, Color.web("#3B82F6"));
        smoothChartData6 = new ChartData("Item 2", random.nextDouble() * 90, Color.web("#3B82F6"));
        smoothChartData7 = new ChartData("Item 3", random.nextDouble() * 90, Color.web("#3B82F6"));
        smoothChartData8 = new ChartData("Item 4", random.nextDouble() * 90, Color.web("#3B82F6"));

        smoothAreaChartTile = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .prefSize(150.0, 150.0)
                .minValue(0)
                .maxValue(40)
                .animated(true)
                .textColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))
                .backgroundColor(Color.TRANSPARENT)
                .valueColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))

                .chartData(smoothChartData1, smoothChartData2, smoothChartData3, smoothChartData4)
                .tooltipText("")
                .animated(true)
                .build();
        smoothAreaBox.getChildren().add(smoothAreaChartTile);


        smoothAreaChartTile2 = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .prefSize(150.0, 150.0)
                .minValue(0)
                .maxValue(40)
                .animated(true)
                .textColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))
                .backgroundColor(Color.TRANSPARENT)
                .valueColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"))

                .chartData(smoothChartData5, smoothChartData6, smoothChartData7, smoothChartData8)
                .tooltipText("")
                .animated(true)
                .build();
        smoothCharBox2.getChildren().add(smoothAreaChartTile2);


        // update code view color theme on app theme change
        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            if (ThemeManager.getInstance().getTheme() != null) {
                circularProgressTile.setBarBackgroundColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#334155") : Color.web("#afb8c1"));
                circularProgressTile.setValueColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));
                circularProgressTile.setUnitColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));

                radialChartTile.setBarBackgroundColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#334155") : Color.web("#afb8c1"));
                radialChartTile.setTextColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));

                smoothAreaChartTile.setBarBackgroundColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#334155") : Color.web("#afb8c1"));
                smoothAreaChartTile.setTextColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));
                smoothAreaChartTile.setUnitColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));
                smoothAreaChartTile.setValueColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));

                smoothAreaChartTile2.setBarBackgroundColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#334155") : Color.web("#afb8c1"));
                smoothAreaChartTile2.setTextColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));
                smoothAreaChartTile2.setUnitColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));
                smoothAreaChartTile2.setValueColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#475569"));

            }
        });
        Styles.toggleStyleClass(activeProjectsTableView, Styles.STRIPED);
        Styles.toggleStyleClass(roectsTableView, Styles.STRIPED);

        for (TableColumn<?, ?> c : activeProjectsTableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }
        for (TableColumn<?, ?> c : roectsTableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now > lastTimerCall + 3_500_000_000L) {

                    chartData1.setValue(random.nextDouble() * 50);
                    chartData2.setValue(random.nextDouble() * 50);
                    chartData3.setValue(random.nextDouble() * 50);
                    chartData4.setValue(random.nextDouble() * 50);


                    circularProgressTile.setValue(random.nextDouble() * 100);


                    smoothChartData1.setValue(random.nextDouble() * 90);
                    smoothChartData2.setValue(random.nextDouble() * 80);
                    smoothChartData3.setValue(random.nextDouble() * 40);
                    smoothChartData4.setValue(random.nextDouble() * 25);

                    smoothChartData5.setValue(smoothChartData6.getValue());
                    smoothChartData6.setValue(smoothChartData7.getValue());
                    smoothChartData7.setValue(smoothChartData8.getValue());
                    smoothChartData8.setValue(random.nextDouble() * 50);


                    for (int i = 0; i < january.getData().size(); i++) {
                        january.getData().get(i).setYValue(random.nextInt(10, 80));
                    }
                    for (int i = 0; i < february.getData().size(); i++) {
                        february.getData().get(i).setYValue(random.nextInt(10, 80));
                    }
                    for (int i = 0; i < march.getData().size(); i++) {
                        march.getData().get(i).setYValue(random.nextInt(10, 80));
                    }


                    lastTimerCall = now;
                }
            }
        };
        timer.start();
    }

    private void barChart() {
        //snippet_3:start


        january = new XYChart.Series<String, Number>();
        january.setName("January");
        IntStream.range(0, countries.size()).forEach(i -> january.getData().add(
                new XYChart.Data<>(countries.get(i), random.nextInt(10, 80))
        ));
        january.getNode();

        february = new XYChart.Series<String, Number>();
        february.setName("February");
        IntStream.range(0, countries.size()).forEach(i -> february.getData().add(
                new XYChart.Data<>(countries.get(i), random.nextInt(10, 80))
        ));

        march = new XYChart.Series<String, Number>();
        march.setName("March");
        IntStream.range(0, countries.size()).forEach(i -> march.getData().add(
                new XYChart.Data<>(countries.get(i), random.nextInt(10, 80))
        ));



        barChart.getData().clear();
        barChart.getData().setAll(january, february, march);


    }
}
