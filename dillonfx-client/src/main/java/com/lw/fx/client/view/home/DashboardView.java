package com.lw.fx.client.view.home;

import atlantafx.base.theme.Styles;
import com.lw.fx.client.Resources;
import com.lw.fx.client.domain.ActiveProjects;
import com.lw.fx.client.domain.Roects;
import com.lw.fx.client.event.DefaultEventBus;
import com.lw.fx.client.event.EventBus;
import com.lw.fx.client.event.ThemeEvent;
import com.lw.fx.client.theme.ThemeManager;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.Helper;
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
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;


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
    private ChartData chartData5;

    private ChartData smoothChartData1;
    private ChartData smoothChartData2;
    private ChartData smoothChartData3;
    private ChartData smoothChartData4;
    @InjectViewModel
    private DashboardViewModel dashboardViewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

                .chartData( new ChartData("Item 1", random.nextDouble() * 100, Color.web("#22C55E")),  new ChartData("Item 1", random.nextDouble() * 100,  Color.web("#22C55E")),  new ChartData("Item 1", random.nextDouble() * 100,  Color.web("#22C55E")),  new ChartData("Item 1", random.nextDouble() * 100,  Color.web("#22C55E")))
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
    }

    private void barChart() {
        //snippet_3:start
        final var rnd = new Random();
        final var faker = new Faker();
        final var countries = IntStream.range(0, 5).boxed()
                .map(i -> faker.country().countryCode3().toUpperCase())
                .toList();

        var x = new CategoryAxis();
        x.setLabel("Country");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Value");

        var january = new XYChart.Series<String, Number>();
        january.setName("January");
        IntStream.range(0, countries.size()).forEach(i -> january.getData().add(
                new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));


        var february = new XYChart.Series<String, Number>();
        february.setName("February");
        IntStream.range(0, countries.size()).forEach(i -> february.getData().add(
                new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var march = new XYChart.Series<String, Number>();
        march.setName("March");
        IntStream.range(0, countries.size()).forEach(i -> march.getData().add(
                new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));


        barChart.getData().addAll(january, february, march);


    }
}
