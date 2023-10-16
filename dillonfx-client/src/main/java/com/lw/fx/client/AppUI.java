/* SPDX-License-Identifier: MIT */

package com.lw.fx.client;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessPane;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.lw.fx.client.event.BrowseEvent;
import com.lw.fx.client.event.DefaultEventBus;
import com.lw.fx.client.event.HotkeyEvent;
import com.lw.fx.client.event.Listener;
import com.lw.fx.client.theme.ThemeManager;
import com.lw.fx.client.view.window.WindowView;
import com.lw.fx.client.view.window.WindowViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import fr.brouillard.oss.cssfx.CSSFX;
import fr.brouillard.oss.cssfx.api.URIToPathConverter;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger.LogLevel;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AppUI extends Application {

    public static final boolean IS_DEV_MODE = "DEV".equalsIgnoreCase(
            Resources.getPropertyOrEnv("Datlantafx.mode", "DATLANTAFX_MODE")
    );

    public static final List<KeyCodeCombination> SUPPORTED_HOTKEYS = List.of(
            new KeyCodeCombination(KeyCode.SLASH),
            new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN),
            new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN)
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        loadApplicationProperties();

        if (IS_DEV_MODE) {
            System.out.println("[WARNING] Application is running in development mode.");
        }

        var antialiasing = Platform.isSupported(ConditionalFeature.SCENE3D)
                ? SceneAntialiasing.BALANCED
                : SceneAntialiasing.DISABLED;

        ViewTuple<WindowView, WindowViewModel> viewTuple = FluentViewLoader.fxmlView(WindowView.class).load();
        Parent mainView = viewTuple.getView();
        BorderlessScene scene = new BorderlessScene(stage, StageStyle.TRANSPARENT, mainView, 1200 + 80, 768);
        BorderlessPane rootPane = (BorderlessPane) scene.getRoot();
        rootPane.setPadding(new Insets(15, 15, 15, 15));

        scene.setOnKeyPressed(this::dispatchHotkeys);
        scene.removeDefaultCSS();
        scene.setMoveControl(mainView);
        scene.setFill(Color.TRANSPARENT);

        var tm = ThemeManager.getInstance();
        tm.setScene(scene);
        tm.setTheme(tm.getDefaultTheme());
        if (IS_DEV_MODE) {
            startCssFX(scene);
        }
        scene.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                rootPane.setPadding(new Insets(0, 0, 0, 0));
            } else {
                rootPane.setPadding(new Insets(15, 15, 15, 15));
            }

        });
        scene.getStylesheets().addAll(Resources.resolve("assets/styles/index.css"));

        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        loadIcons(stage);
        stage.setResizable(true);
        stage.setOnCloseRequest(t -> Platform.exit());

        // register event listeners
        DefaultEventBus.getInstance().subscribe(BrowseEvent.class, this::onBrowseEvent);

        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }




    private void loadIcons(Stage stage) {
        int iconSize = 16;
        while (iconSize <= 1024) {
            // we could use the square icons for Windows here
//            stage.getIcons().add(new Image(Resources.getResourceAsStream("assets/icon-rounded-" + iconSize + ".png")));
            iconSize *= 2;
        }
    }

    private void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(Resources.getResourceAsStream("/application.properties"),
                UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("CatchAndPrintStackTrace")
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
        CSSFXLogger.setLoggerFactory(loggerName -> (level, message, args) -> {
            if (level.ordinal() <= LogLevel.INFO.ordinal()) {
                System.out.println("[" + level + "] CSSFX: " + String.format(message, args));
            }
        });
        CSSFX.start(scene);
    }

    private void dispatchHotkeys(KeyEvent event) {
        for (KeyCodeCombination k : SUPPORTED_HOTKEYS) {
            if (k.match(event)) {
                DefaultEventBus.getInstance().publish(new HotkeyEvent(k));
                return;
            }
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Listener
    private void onBrowseEvent(BrowseEvent event) {
        getHostServices().showDocument(event.getUri().toString());
    }
}
