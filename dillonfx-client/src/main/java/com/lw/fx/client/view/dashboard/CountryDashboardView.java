package com.lw.fx.client.view.dashboard;

import com.lw.fx.client.event.DefaultEventBus;
import com.lw.fx.client.event.ThemeEvent;
import com.lw.fx.client.theme.ThemeManager;
import de.saxsys.mvvmfx.FxmlView;
import eu.hansolo.fx.countries.Country;
import eu.hansolo.fx.countries.CountryPane;
import eu.hansolo.fx.countries.CountryPaneBuilder;
import eu.hansolo.fx.countries.tools.*;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.toolboxfx.geom.Poi;
import eu.hansolo.toolboxfx.geom.PoiBuilder;
import eu.hansolo.toolboxfx.geom.PoiSize;
import eu.hansolo.toolboxfx.geom.Point;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class CountryDashboardView implements FxmlView<CountryDashboardViewModel>, Initializable {


    @FXML
    private VBox countryBox;

    @FXML
    private StackPane countryStackPane;

    @FXML
    private Group chartBar2;

    @FXML
    private HBox chartBox1;
    @FXML
    private HBox chartBox3;
    @FXML
    private HBox chartBox4;
    @FXML
    private HBox chartBox5;


    @FXML
    private Path ndicatorsPath;

    @FXML
    private Path predictPath;

    private Country country;

    private CountryPane countryPane;

    private Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chartBox1.getChildren().add(createSmoothCurve(80d,25d,Color.web("#02A4FF"),Color.web("#7D40FF"),7d));
        chartBar2.getChildren().add(createSmoothCurve(120d,30d,Color.web("#7517F8"),Color.web("#E323FF"),5d));
        chartBar2.getChildren().add(createSmoothCurve(120d,30d,Color.web("#FF7D05"),Color.web("#FFD422"),5d));
        chartBox3.getChildren().add(createSmoothCurve(30d,15D,Color.web("#7517F8"),Color.web("#E323FF"),3d));
        chartBox4.getChildren().add(createSmoothCurve(30d,15d,Color.web("#02C751"),Color.web("#8AFF6C"),3d));
        chartBox5.getChildren().add(createSmoothCurve(30d,15d,Color.web("#FF7D05"),Color.web("#FFD422"),3d));


      init();
    }

    public void init() {
        this.country = Country.CN;
        List<Poi> pois = new ArrayList();
        List<Point> heatmapSpots = new ArrayList();

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.BLUE));
        CLocation fmo = CLocationBuilder.create().name("FMO").latitude(39.9042 ).longitude(116.4074 ).connectionPartType(ConnectionPartType.SOURCE).build();
        CLocation muc = CLocationBuilder.create().name("MUC").latitude(23.1291).longitude(113.2644).connectionPartType(ConnectionPartType.TARGET).build();
        Connection fmoToMuc = ConnectionBuilder.create(fmo, muc).arrowsVisible(true).lineWidth(4.0).sourceColor(Color.web("#7517F8")).targetColor(Color.web("#E323FF")).gradientFill(true).build();

        CLocation muc1 = CLocationBuilder.create().name("MUC").latitude(26.0769 ).longitude(119.2965 ).connectionPartType(ConnectionPartType.TARGET).build();
        Connection fmoToMuc1 = ConnectionBuilder.create(fmo, muc1).arrowsVisible(true).lineWidth(4.0).sourceColor(Color.web("#7517F8")).targetColor(Color.web("#E323FF")).gradientFill(true).build();

        CLocation muc2 = CLocationBuilder.create().name("MUC").latitude(30.5928).longitude(114.3055).connectionPartType(ConnectionPartType.TARGET).build();
        Connection fmoToMuc2 = ConnectionBuilder.create(fmo, muc2).arrowsVisible(true).lineWidth(4.0).sourceColor(Color.web("#7517F8")).targetColor(Color.web("#E323FF")).gradientFill(true).build();

        CLocation muc3 = CLocationBuilder.create().name("MUC").latitude(30.5728).longitude(104.0668 ).connectionPartType(ConnectionPartType.TARGET).build();
        Connection fmoToMuc3 = ConnectionBuilder.create(fmo, muc3).arrowsVisible(true).lineWidth(4.0).sourceColor(Color.web("#7517F8")).targetColor(Color.web("#E323FF")).gradientFill(true).build();

        CLocation muc4 = CLocationBuilder.create().name("MUC").latitude(31.2304).longitude(121.4737 ).connectionPartType(ConnectionPartType.TARGET).build();
        Connection fmoToMuc4 = ConnectionBuilder.create(fmo, muc4).arrowsVisible(true).lineWidth(4.0).sourceColor(Color.web("#7517F8")).targetColor(Color.web("#E323FF")).gradientFill(true).build();

        Poi muenster = PoiBuilder.create().lat(51.91183747470598).lon(7.633806255269727).name("Münster").image(Country.DE.getFlag().getImage()).svgPath("M9.998,0.004l2.24,6.908l7.252,0l-5.867,4.27l2.241,6.909l-5.866,-4.27l-5.867,4.27l2.241,-6.909l-5.867,-4.27l7.252,0l2.241,-6.908Z").svgPathDim(new Dimension2D(20.0, 20.0)).build();
        Helper.getCities().stream().filter((city) -> {
            return city.country() == this.country;
        }).filter((city) -> {
            return city.population() > 300000L;
        }).forEach((city) -> {
            Poi poi = PoiBuilder.create().lat(city.lat()).lon(city.lon()).name(city.name()).fill(Color.RED).pointSize(PoiSize.NORMAL).build();
            pois.add(poi);
            heatmapSpots.add(new Point(city.lon(), city.lat()));
        });
        this.countryPane = CountryPaneBuilder.create(this.country).pois(pois).poisVisible(true).poiTextVisible(true).connections(List.of(fmoToMuc,fmoToMuc1,fmoToMuc2,fmoToMuc3,fmoToMuc4)).overlayVisible(true).build();
        countryPane.setStyle("-fx-background-color: transparent");
        countryPane.setFill(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#1F1F43") : Color.web("#ffffff"));
        countryStackPane.getChildren().add(countryPane);

        // update code view color theme on app theme change
        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            if (ThemeManager.getInstance().getTheme() != null) {
                countryPane.setFill(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#1F1F43") : Color.web("#ffffff"));
            }
        });
    }

    private Path createSmoothCurve(Double hight,Double xW,Color color1 ,Color color2,double storeSize) {
        Path path = new Path();

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2));
        path.setStroke(gradient);
        path.setStrokeWidth(storeSize);
        // Move to the first data point
        path.getElements().add(new MoveTo(0, random.nextDouble() * hight));

        // Create cubic curve segments between data points
        for (int i = 0; i < 10 - 1; i++) {
            double x0 = i * xW;
            double y0 = random.nextDouble() * hight;
            double x1 = (i + 1) * xW;
            double y1 = random.nextDouble() * hight;
            path.getElements().add(new CubicCurveTo(x0 + xW/2, y0, x1 - xW/2, y1, x1, y1));
        }

        return path;
    }
}
