import eu.hansolo.fx.countries.Country;
import eu.hansolo.fx.countries.CountryPane;
import eu.hansolo.fx.countries.CountryPaneBuilder;
import eu.hansolo.fx.countries.PropertyManager;
import eu.hansolo.fx.countries.tools.*;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.toolboxfx.geom.Poi;
import eu.hansolo.toolboxfx.geom.PoiBuilder;
import eu.hansolo.toolboxfx.geom.PoiSize;
import eu.hansolo.toolboxfx.geom.Point;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DemoCountryPane extends Application {
    public static final String VERSION;
    private Country country;
    private CountryPane countryPane;

    public DemoCountryPane() {
    }

    public void init() {
        this.country = Country.DE;
        List<Poi> pois = new ArrayList();
        List<Point> heatmapSpots = new ArrayList();

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.BLUE));
        CLocation fmo = CLocationBuilder.create().name("FMO").latitude(52.1307).longitude(7.6941).connectionPartType(ConnectionPartType.SOURCE).build();
        CLocation muc = CLocationBuilder.create().name("MUC").latitude(48.351).longitude(11.7764).connectionPartType(ConnectionPartType.TARGET).build();
        Connection fmoToMuc = ConnectionBuilder.create(fmo, muc).arrowsVisible(true).lineWidth(4.0).sourceColor(Color.web("#23F2F5")).targetColor(Color.web("#7517F8")).gradientFill(true).build();
        Poi muenster = PoiBuilder.create().lat(51.91183747470598).lon(7.633806255269727).name("Münster").image(Country.DE.getFlag().getImage()).svgPath("M9.998,0.004l2.24,6.908l7.252,0l-5.867,4.27l2.241,6.909l-5.866,-4.27l-5.867,4.27l2.241,-6.909l-5.867,-4.27l7.252,0l2.241,-6.908Z").svgPathDim(new Dimension2D(20.0, 20.0)).build();
        Helper.getCities().stream().filter((city) -> {
            return city.country() == this.country;
        }).filter((city) -> {
            return city.population() > 300000L;
        }).forEach((city) -> {
            Poi poi = PoiBuilder.create().lat(city.lat()).lon(city.lon()).name(city.name()).fill(Color.RED).pointSize(PoiSize.NORMAL).build();
            pois.add(poi);
//            heatmapSpots.add(new Point(city.lon(), city.lat()));
        });
        this.countryPane = CountryPaneBuilder.create(this.country).pois(pois).poisVisible(true).poiTextVisible(true).heatmapVisible(true).heatmapSpots(heatmapSpots).heatmapSpotRadius(10.0).connections(List.of(fmoToMuc)).overlayVisible(true).build();
        countryPane.setStyle("-fx-background-color: transparent");
    }

    public void start(Stage stage) {

     Tile   countryTile = TileBuilder.create().skinType(Tile.SkinType.COUNTRY)
//                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .minValue(0)
                .maxValue(40)
                .title("Country Tile")
                .unit("Unit")
                .country(Country.CN)
                .tooltipText("")
                .animated(true).backgroundColor(Color.TRANSPARENT).barColor(Color.RED)
                .build();
        StackPane pane = new StackPane(new Node[]{countryPane});
        pane.setPrefSize(400.0, 400.0);
        pane.setPadding(new Insets(10.0));
        Scene scene = new Scene(pane);
        stage.setTitle("CountryPane Version: " + VERSION);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

    static {
        VERSION = PropertyManager.INSTANCE.getVersionNumber();
    }
}