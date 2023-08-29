package org.dillon.fx;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessPane;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import fr.brouillard.oss.cssfx.CSSFX;
import fr.brouillard.oss.cssfx.api.URIToPathConverter;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dillon.fx.theme.ThemeManager;
import org.dillon.fx.view.window.WindowView;
import org.dillon.fx.view.window.WindowViewModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * JavaFX App
 */
public class AppUI extends Application {

    public static final boolean IS_DEV_MODE = "DEV".equalsIgnoreCase(
            Resources.getPropertyOrEnv("app.profiles.active", "app.profiles.active")
    );


    @Override
    public void start(Stage primaryStage) throws IOException {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(primaryStage));

        if ("DEV".equalsIgnoreCase(System.getProperty("app.profiles.active"))) {
            System.out.println("-------[WARNING] Application is running in development mode.");
        }
        loadApplicationProperties();

//        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());


        ViewTuple<WindowView, WindowViewModel> viewTuple = FluentViewLoader.fxmlView(WindowView.class).load();
        Parent mainView = viewTuple.getView();


        BorderlessScene scene = new BorderlessScene(primaryStage, StageStyle.TRANSPARENT, mainView, 250, 250);
        BorderlessPane rootPane = (BorderlessPane) scene.getRoot();
        rootPane.setPadding(new Insets(15, 15, 15, 15));

        var tm = ThemeManager.getInstance();
        tm.setScene(scene);
        tm.setTheme(tm.getDefaultTheme());
        if ("DEV".equalsIgnoreCase(System.getProperty("app.profiles.active"))) {
            startCssFX(scene);
        }
        scene.removeDefaultCSS();
        scene.setMoveControl(mainView);
        scene.setFill(Color.TRANSPARENT);
//        viewTuple.getViewModel().setTitle(System.getProperty("app.name"));
        primaryStage.setTitle(System.getProperty("app.name"));
        scene.getStylesheets().addAll(AppUI.class.getResource("/styles/index.css").toExternalForm());

        scene.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                rootPane.setPadding(new Insets(0, 0, 0, 0));
            } else {
                rootPane.setPadding(new Insets(15, 15, 15, 15));
            }
//            viewTuple.getViewModel().setMaximized(newVal);

        });


        PseudoClass CUSTOM_THEME = PseudoClass.getPseudoClass("custom-theme");
        scene.getRoot().pseudoClassStateChanged(CUSTOM_THEME, true);
        //Show
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);

//        primaryStage.titleProperty().bind(viewTuple.getViewModel().titleProperty());
        primaryStage.show();
    }

    private void loadApplicationProperties() {
        try {
            var properties = new Properties();
            properties.load(new InputStreamReader(Resources.getResourceAsStream("application.properties"), UTF_8));
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startCssFX(Scene scene) {
        URIToPathConverter fileUrlConverter = uri -> {
            try {
                if (uri != null && uri.startsWith("file:")) {
                    return Paths.get(URI.create(uri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        CSSFX.addConverter(fileUrlConverter).start();
        CSSFXLogger.setLoggerFactory(loggerName -> (level, message, args) ->
                System.out.println("[CSSFX] " + String.format(message, args))
        );
        CSSFX.start(scene);
    }

    public static void main(String[] args) {


        launch(args);
    }


}