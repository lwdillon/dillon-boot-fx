/* SPDX-License-Identifier: MIT */
package org.dillon.fx.view.main;

import atlantafx.base.controls.Spacer;
import cn.hutool.core.util.StrUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.dillon.fx.theme.ThemeManager;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static atlantafx.base.theme.Styles.*;
import static javafx.geometry.Pos.CENTER_LEFT;
import static org.dillon.fx.theme.ThemeManager.DEFAULT_ZOOM;
import static org.dillon.fx.theme.ThemeManager.SUPPORTED_ZOOM;
import static org.kordamp.ikonli.material2.Material2AL.ARROW_BACK;
import static org.kordamp.ikonli.material2.Material2AL.ARROW_FORWARD;

// This should really be refactored to more generic control someday.
// - the whole component to PopoverMenu, that reuses JavaFX MenuItem API
// - font size switcher to flat SpinnerMenuItem
public class QuickConfigMenu extends StackPane {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final String EXIT_ID = "Exit";

    private MainMenu mainMenu;
    private ThemeSelectionMenu themeSelectionMenu;
    private Runnable exitHandler;

    private final Consumer<String> navHandler = s -> {
        Menu menu = null;
        switch (s) {
            case MainMenu.ID -> menu = getOrCreateMainMenu();
            case ThemeSelectionMenu.ID -> menu = getOrCreateThemeSelectionMenu();
            default -> {
                if (exitHandler != null) {
                    exitHandler.run();
                    return;
                }
            }
        }

        Objects.requireNonNull(menu);
        menu.update();
        getChildren().setAll(menu.getRoot());
    };

    public void setExitHandler(Runnable exitHandler) {
        this.exitHandler = exitHandler;
    }

    public QuickConfigMenu() {
        super();

        getChildren().setAll(getOrCreateMainMenu());
        setId("quick-config-menu");
    }

    private MainMenu getOrCreateMainMenu() {
        if (mainMenu == null) { mainMenu = new MainMenu(navHandler); }
        return mainMenu;
    }

    private ThemeSelectionMenu getOrCreateThemeSelectionMenu() {
        if (themeSelectionMenu == null) { themeSelectionMenu = new ThemeSelectionMenu(navHandler); }
        return themeSelectionMenu;
    }

    private static Pane menu(String title, HorizontalDirection direction) {
        var label = new Label(title);
        label.getStyleClass().add(TEXT_CAPTION);

        var root = new HBox();
        root.setAlignment(CENTER_LEFT);
        root.getStyleClass().addAll("row", "action");

        switch (direction) {
            case LEFT -> root.getChildren().setAll(new FontIcon(ARROW_BACK), new Spacer(), label, new Spacer());
            case RIGHT -> root.getChildren().setAll(label, new Spacer(), new FontIcon(ARROW_FORWARD));
        }

        return root;
    }

    public void update() {
        getOrCreateMainMenu().update();
    }

    ///////////////////////////////////////////////////////////////////////////

    private interface Menu {

        void update();

        Pane getRoot();
    }

    private static class MainMenu extends VBox implements Menu {

        private static final String ID = "MainMenu";

        private final IntegerProperty zoom = new SimpleIntegerProperty(DEFAULT_ZOOM);
        private final BooleanBinding canZoomIn = Bindings.createBooleanBinding(
                () -> SUPPORTED_ZOOM.indexOf(zoom.get()) < SUPPORTED_ZOOM.size() - 1, zoom
        );
        private final BooleanBinding canZoomOut = Bindings.createBooleanBinding(
                () -> SUPPORTED_ZOOM.indexOf(zoom.get()) >= 1, zoom
        );

        public MainMenu(Consumer<String> navHandler) {
            super();

            Objects.requireNonNull(navHandler);

            var themeSelectionMenu = menu("主题", HorizontalDirection.RIGHT);
            themeSelectionMenu.setOnMouseClicked(e -> navHandler.accept(ThemeSelectionMenu.ID));

            var accentSelector = new AccentColorSelector();
            accentSelector.setAlignment(Pos.CENTER);

            // ~

            var zoomInBtn = new Button("", new FontIcon(Feather.ZOOM_IN));
            zoomInBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_ICON, FLAT);
            zoomInBtn.setOnAction(e -> {
                if (canZoomIn.get()) {
                    zoom.set(SUPPORTED_ZOOM.get(SUPPORTED_ZOOM.indexOf(zoom.get()) + 1));
                }
            });
            zoomInBtn.disableProperty().bind(canZoomIn.not());

            var zoomOutBtn = new Button("", new FontIcon(Feather.ZOOM_OUT));
            zoomOutBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_ICON, FLAT);
            zoomOutBtn.setOnAction(e -> {
                if (canZoomOut.get()) {
                    zoom.set(SUPPORTED_ZOOM.get(SUPPORTED_ZOOM.indexOf(zoom.get()) - 1));
                }
            });
            zoomOutBtn.disableProperty().bind(canZoomOut.not());

            var zoomLabel = new Label();
            zoomLabel.textProperty().bind(Bindings.createStringBinding(() -> zoom.get() + "%", zoom));

            var zoomBox = new HBox(zoomOutBtn, new Spacer(), zoomLabel, new Spacer(), zoomInBtn);
            zoomBox.setAlignment(CENTER_LEFT);
            zoomBox.getStyleClass().addAll("row");

            final var tm = ThemeManager.getInstance();
            zoom.addListener((obs, old, val) -> {
                if (val != null && tm.getZoom() != val.intValue()) {
                    tm.setZoom(val.intValue());
                }
            });

            // ~

            getChildren().setAll(
                    themeSelectionMenu,
                    new Separator(),
                    accentSelector,
                    new Separator(),
                    zoomBox
            );
        }

        @Override
        public void update() {
            zoom.set(ThemeManager.getInstance().getZoom());
        }

        @Override
        public Pane getRoot() {
            return this;
        }
    }

    private static class ThemeSelectionMenu extends VBox implements Menu {

        public static final String ID = "ThemeSelectionMenu";

        private final List<Node> items = new ArrayList<>();

        public ThemeSelectionMenu(Consumer<String> navHandler) {
            super();

            Objects.requireNonNull(navHandler);
            var tm = ThemeManager.getInstance();

            var mainMenu = menu("主题", HorizontalDirection.LEFT);
            mainMenu.setOnMouseClicked(e -> navHandler.accept(MainMenu.ID));

            getChildren().setAll(mainMenu, new Separator());

            tm.getRepository().getAll().forEach(theme -> {
                var icon = new FontIcon(Material2AL.CHECK);


                String themName = theme.getName();
                if (StrUtil.equals(theme.getName(), "Primer Light")) {
                    themName = "明亮1";
                } else if (StrUtil.equals(theme.getName(), "Primer Dark")) {
                    themName = "黑暗1";
                } else if (StrUtil.equals(theme.getName(), "Nord Light")) {
                    themName = "明亮2";
                } else if (StrUtil.equals(theme.getName(), "Nord Dark")) {
                    themName = "黑暗2";
                }
                var item = new HBox(20, icon, new Label(themName));
                item.getStyleClass().addAll("row", "action", "radio");
                item.setUserData(themName);
                item.setOnMouseClicked(e -> {
                    tm.setTheme(theme);
                    navHandler.accept(MainMenu.ID);
                    navHandler.accept(QuickConfigMenu.EXIT_ID);
                });

                items.add(item);
                getChildren().add(item);
            });
        }

        @Override
        public void update() {
            items.forEach(item -> item.pseudoClassStateChanged(
                    SELECTED,
                    Objects.equals(item.getUserData(), ThemeManager.getInstance().getTheme().getName())
            ));
        }

        @Override
        public Pane getRoot() {
            return this;
        }
    }
}
