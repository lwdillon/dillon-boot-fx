package com.lw.fx.client.view.dashboard;

import com.lw.fx.client.event.DefaultEventBus;
import com.lw.fx.client.event.ThemeEvent;
import com.lw.fx.client.theme.ThemeManager;
import de.saxsys.mvvmfx.FxmlView;
import eu.hansolo.fx.charts.data.MapConnection;
import eu.hansolo.fx.charts.data.WeightedMapPoints;
import eu.hansolo.fx.charts.tools.MapPoint;
import eu.hansolo.fx.charts.world.World;
import eu.hansolo.fx.charts.world.WorldBuilder;
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

    private World worldMap;
    private MapConnection animatedConnection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chartBox1.getChildren().add(createSmoothCurve(80d,25d,Color.web("#02A4FF"),Color.web("#7D40FF"),7d));
        chartBar2.getChildren().add(createSmoothCurve(120d,30d,Color.web("#7517F8"),Color.web("#E323FF"),5d));
        chartBar2.getChildren().add(createSmoothCurve(120d,30d,Color.web("#FF7D05"),Color.web("#FFD422"),5d));
        chartBox3.getChildren().add(createSmoothCurve(30d,15D,Color.web("#7517F8"),Color.web("#E323FF"),3d));
        chartBox4.getChildren().add(createSmoothCurve(30d,15d,Color.web("#02C751"),Color.web("#8AFF6C"),3d));
        chartBox5.getChildren().add(createSmoothCurve(30d,15d,Color.web("#FF7D05"),Color.web("#FFD422"),3d));


        initWoldMap();

        // update code view color theme on app theme change
        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            if (ThemeManager.getInstance().getTheme() != null) {
//                countryPane.setFill(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#1F1F43") : Color.web("#ffffff"));
                worldMap.setFillColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#1F1F43") : Color.web("#ffffff"));
                worldMap.setTextColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#1F1F43") : Color.web("#ffffff"));
                worldMap.setStrokeColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#000000"));
            }
        });
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
        countryPane.setFill(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#000000"));
        countryStackPane.getChildren().add(countryPane);


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

    public void initWoldMap() {
        worldMap = WorldBuilder.create()
                .resolution(World.Resolution.HI_RES)
                .zoomEnabled(false)
                .hoverEnabled(true)
                .selectionEnabled(true)
                .backgroundColor(Color.TRANSPARENT)
                .fillColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#1F1F43") : Color.web("#ffffff"))
                .connectionWidth(2)
                .weightedMapPoints(WeightedMapPoints.NONE)
                .weightedMapConnections(true)
                .arrowsVisible(true)
                .drawImagePath(true)
                .mapPointTextVisible(true)
                .textColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#000000"))
                .strokeColor(ThemeManager.getInstance().getTheme().isDarkMode() ? Color.web("#ffffff") : Color.web("#000000"))
                .build();

        MapPoint calgary = new MapPoint("Calgary", Color.RED, 51.08299176, -114.0799982);
        MapPoint san_francisco = new MapPoint("San Francisco", Color.BLUE, 37.74000775, -122.4599777);
        MapPoint new_york = new MapPoint("New York", Color.BLUE, 40.74997906, -73.98001693);
        MapPoint chicago = new MapPoint("Chicago", Color.BLUE, 41.82999066, -87.75005497);
        MapPoint denver = new MapPoint("Denver", Color.BLUE, 39.73918805, -104.984016);

        MapPoint mexico_city = new MapPoint("Mexico City", Color.GREEN, 19.44244244, -99.1309882);
        mexico_city.setFill(Color.GREEN);
        MapPoint buenos_aires = new MapPoint("Buenos Aires", Color.LIGHTBLUE, -34.60250161, -58.39753137);
        MapPoint santiago_de_chile = new MapPoint("Santiago de Chile", Color.BLUE, -33.45001382, -70.66704085);
        MapPoint sao_paulo = new MapPoint("Sao Paulo", Color.GREEN, -23.55867959, -46.62501998);

        MapPoint berlin = new MapPoint("Berlin", Color.DARKORANGE, 52.52181866, 13.40154862);
        MapPoint paris = new MapPoint("Paris", Color.DARKBLUE, 48.86669293, 2.333335326);
        MapPoint madrid = new MapPoint("Madrid", Color.YELLOW, 40.40002626, -3.683351686);

        MapPoint johannesburg = new MapPoint("Johannesburg", Color.BROWN, -26.17004474, 28.03000972);
        MapPoint casablanca = new MapPoint("Casablanca", Color.SADDLEBROWN, 33.59997622, -7.616367433);
        MapPoint tunis = new MapPoint("Tunis", Color.DARKGREEN, 36.80277814, 10.1796781);
        MapPoint alexandria = new MapPoint("Alexandria", Color.BLACK, 31.20001935, 29.94999589);
        MapPoint nairobi = new MapPoint("Nairobi", Color.LIGHTBLUE, -1.283346742, 36.81665686);
        MapPoint abidjan = new MapPoint("Abidjan", Color.IVORY, 5.319996967, -4.04004826);

        MapPoint moscow = new MapPoint("Moscow", Color.RED, 55.75216412, 37.61552283);
        MapPoint novosibirsk = new MapPoint("Novosibirsk", Color.RED, 55.02996014, 82.96004187);
        MapPoint magadan = new MapPoint("Magadan", Color.RED, 59.57497988, 150.8100089);

        MapPoint abu_dabi = new MapPoint("Abu Dhabi", Color.GOLD, 24.46668357, 54.36659338);
        MapPoint mumbai = new MapPoint("Mumbai", Color.GOLD, 19.01699038, 72.8569893);
        MapPoint hyderabad = new MapPoint("Hyderabad", Color.GOLD, 17.39998313, 78.47995357);

        MapPoint beijing = new MapPoint("Beijing", Color.DARKRED, 39.92889223, 116.3882857);
        MapPoint chongqing = new MapPoint("Chongqing", Color.DARKRED, 29.56497703, 106.5949816);
        MapPoint hong_kong = new MapPoint("Hong Kong", Color.DARKRED, 22.3049809, 114.1850093);
        MapPoint singapore = new MapPoint("Singapore", Color.CRIMSON, 1.293033466, 103.8558207);
        MapPoint tokio = new MapPoint("Tokio", Color.RED, 35.652832, 139.839478);

        MapPoint sydney = new MapPoint("Sydney", Color.BLUE, -33.865143, 151.209900);
        MapPoint perth = new MapPoint("Perth", Color.BLUE, -31.95501463, 115.8399987);
        MapPoint christchurch = new MapPoint("Christchurch", Color.BLUE, -43.53503131, 172.6300207);


        List<MapPoint> northAmerica = List.of(calgary, san_francisco, chicago, new_york, denver);
        List<MapPoint> southAmerica = List.of(mexico_city, buenos_aires, santiago_de_chile, sao_paulo);
        List<MapPoint> europe = List.of(madrid, paris, berlin);
        List<MapPoint> afrika = List.of(johannesburg, casablanca, tunis, alexandria, nairobi, abidjan);
        List<MapPoint> russia = List.of(moscow, novosibirsk, magadan);
        List<MapPoint> india = List.of(abu_dabi, mumbai, hyderabad);
        List<MapPoint> asia = List.of(beijing, hong_kong, singapore, tokio, chongqing);
        List<MapPoint> australia = List.of(sydney, perth, christchurch);

        worldMap.addMapPoints(berlin, paris, san_francisco, abu_dabi, new_york, chicago, denver, sao_paulo, madrid, calgary,
                mexico_city, buenos_aires, santiago_de_chile, johannesburg, moscow, novosibirsk, magadan,
                mumbai, beijing, hong_kong, sydney, christchurch, tokio, singapore, casablanca, tunis, alexandria, nairobi,
                abidjan, hyderabad, chongqing, perth);

        
        northAmerica.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(berlin, mapPoint, random.nextInt(130) + 10, berlin.getFill(), Color.ORANGERED, true));
        });
        asia.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(berlin, mapPoint, random.nextInt(130) + 10, berlin.getFill(), Color.ORANGERED, true));
        });
        australia.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(beijing, mapPoint, random.nextInt(130) + 10, beijing.getFill(), Color.PURPLE, true));
            worldMap.addMapConnections(new MapConnection(hong_kong, mapPoint, random.nextInt(130) + 10, beijing.getFill(), Color.PURPLE, true));
        });
        europe.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(johannesburg, mapPoint, random.nextInt(130) + 10, johannesburg.getFill(), Color.ORANGE, true));
        });
        southAmerica.forEach(mapPoint -> {
            worldMap.addMapConnections(new MapConnection(johannesburg, mapPoint, random.nextInt(130) + 10, johannesburg.getFill(), Color.ORANGE, true));
        });
        

        MapConnection sanfrancisco_mumbai = new MapConnection(san_francisco, mumbai, 90, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_newyork = new MapConnection(san_francisco, new_york, 100, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_abudabi = new MapConnection(san_francisco, abu_dabi, 60, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_mexicocity = new MapConnection(san_francisco, mexico_city, 30, Color.ORANGERED, Color.BLUE, true);
        MapConnection sanfrancisco_santiago = new MapConnection(san_francisco, santiago_de_chile, 70, Color.ORANGERED, Color.BLUE, true);


        animatedConnection = new MapConnection(berlin, christchurch, 3, Color.CRIMSON);
        animatedConnection.setLineWidth(5);

        worldMap.addMapConnections(sanfrancisco_mumbai, sanfrancisco_abudabi, sanfrancisco_newyork, sanfrancisco_mexicocity, sanfrancisco_santiago);

        countryStackPane.getChildren().add(worldMap);
    }
}
