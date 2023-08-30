package com.lw.fx.client.view.window;

import animatefx.animation.AnimateFXInterpolator;
import animatefx.animation.AnimationFX;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.util.ParallelAnimationFX;
import atlantafx.base.theme.Tweaks;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lw.fx.client.icon.WIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.FLAT;

public class SideMenu extends StackPane {

    private TreeView<JSONObject> treeView;
    private VBox menuBar;

    private MainViewModel mainViewModel;

    public SideMenu(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
        mainViewModel.routersUpdateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                treeView.setRoot(createTreeItem());
            }
        });
        createView();
    }

    private void createView() {
        this.minWidthProperty().bind(this.prefWidthProperty());
        this.maxWidthProperty().bind(this.prefWidthProperty());
        menuBar = new VBox();
        menuBar.setId("side-menu-bar");
        menuBar.setMaxWidth(Double.MAX_VALUE);
        menuBar.setAlignment(Pos.TOP_CENTER);

        treeView = new TreeView();
        treeView.setShowRoot(false);
        treeView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        treeView.setId("side-tree");
        treeView.setCellFactory(new Callback<TreeView<JSONObject>, TreeCell<JSONObject>>() {
            @Override
            public TreeCell call(TreeView param) {
                TreeCell treeCell = new TreeCell<JSONObject>() {
                    @Override
                    protected void updateItem(JSONObject item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Label label = new Label(item.getJSONObject("meta").getStr("title"));
                            String iconStr = item.getJSONObject("meta").getStr("icon");
                            label.setMaxWidth(Double.MAX_VALUE);
                            FontIcon icon = FontIcon.of(Feather.CHEVRON_DOWN);
                            label.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + iconStr), 24, Color.CYAN));
                            label.setGraphicTextGap(10);
                            HBox box = new HBox(label, icon);
                            HBox.setHgrow(label, Priority.ALWAYS);


                            icon.setVisible(!getTreeItem().isLeaf());
                            if (isSelected()) {
                                label.setId("side-menu-cell-selected");
                            }
                            box.setPadding(new Insets(7, 7, 7, 0));
                            setGraphic(box);
                        }

                    }
                };
                treeCell.setOnMouseClicked(event -> {
                    if (!treeCell.isEmpty()) {
                        treeCell.getTreeItem().setExpanded(!treeCell.getTreeItem().isExpanded());
                    }
                });
                return treeCell;
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<JSONObject> currentSelectItem = (TreeItem<JSONObject>) newValue;
            if (currentSelectItem != null && currentSelectItem.isLeaf()) {
                mainViewModel.addTab(currentSelectItem.getValue());
            }
        });
        setId("side-menu");
        getChildren().addAll(menuBar, treeView);
    }

    private TreeItem<JSONObject> createTreeItem() {
        var root = new TreeItem<JSONObject>();
        root.setExpanded(true);

        mainViewModel.getRouterList().forEach(obj -> {
            var child = new TreeItem<JSONObject>(obj);
            String iconStr = obj.getJSONObject("meta").getStr("icon");
            MenuButton menuButton = new MenuButton();
            menuButton.setPopupSide(Side.RIGHT);
            menuButton.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + iconStr), 32));
            menuButton.getStyleClass().addAll(FLAT, Tweaks.NO_ARROW);
            menuButton.setId("side-menu-button");

            var childObj = obj.getJSONArray("children");
            if (childObj != null) {
                generateTree(child, childObj);
                generateMenu(menuButton, childObj);
            } else {
                menuButton.setOnMouseClicked(event -> mainViewModel.addTab(obj));
            }

            Platform.runLater(() -> {
                root.getChildren().add(child);
                menuBar.getChildren().add(menuButton);
            });

        });

        return root;
    }

    private void generateTree(TreeItem<JSONObject> parent, JSONArray jsonArray) {
        jsonArray.forEach(obj -> {

            if (obj instanceof JSONObject) {

                var child = new TreeItem<JSONObject>((JSONObject) obj);
                var childObj = ((JSONObject) obj).getJSONArray("children");
                if (childObj != null) {
                    generateTree(child, childObj);
                }

                Platform.runLater(() -> {
                    parent.getChildren().add(child);

                });
            }

        });
    }

    private void generateMenu(MenuButton parent, JSONArray jsonArray) {
        jsonArray.forEach(obj -> {

            if (obj instanceof JSONObject) {
                var childObj = ((JSONObject) obj).getJSONArray("children");
                var text = ((JSONObject) obj).getJSONObject("meta").getStr("title");
                String iconStr = ((JSONObject) obj).getJSONObject("meta").getStr("icon");
                if (childObj != null) {
                    var child = new Menu(text);
                    child.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + iconStr), 24));
                    generateMenu2(child, childObj);

                    Platform.runLater(() -> {
                        parent.getItems().add(child);

                    });
                } else {
                    var child = new MenuItem(text);
                    child.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + iconStr), 32));
                    child.setOnAction(event -> mainViewModel.addTab((JSONObject) obj));
                    Platform.runLater(() -> {
                        parent.getItems().add(child);

                    });
                }
            }

        });
    }

    private void generateMenu2(Menu parent, JSONArray jsonArray) {
        jsonArray.forEach(obj -> {

            if (obj instanceof JSONObject) {
                var text = ((JSONObject) obj).getJSONObject("meta").getStr("title");
                String iconStr = ((JSONObject) obj).getJSONObject("meta").getStr("icon");
                var childObj = ((JSONObject) obj).getJSONArray("children");
                if (childObj != null) {
                    var child = new Menu(text);
                    child.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + iconStr), 24));
                    generateMenu2(child, childObj);

                    Platform.runLater(() -> {
                        parent.getItems().add(child);

                    });
                } else {
                    var child = new MenuItem(text);
                    child.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + iconStr)));
                    child.setOnAction(event -> mainViewModel.addTab((JSONObject) obj));
                    Platform.runLater(() -> {
                        parent.getItems().add(child);

                    });
                }
            }

        });
    }

    public void expansion(boolean expansion) {


        if (expansion) {
            menuBar.setVisible(false);
            treeView.setVisible(true);
            AnimationFX animationFX = new FadeOut(this);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0),
                    new KeyValue(this.prefWidthProperty(), 50D, AnimateFXInterpolator.EASE)),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(this.prefWidthProperty(), 300D, AnimateFXInterpolator.EASE)));

            animationFX.setTimeline(timeline);
            ParallelAnimationFX parallelAnimationFX
                    = new ParallelAnimationFX(new FadeIn(treeView), animationFX);
            parallelAnimationFX.play();
        } else {
            treeView.setVisible(false);
            menuBar.setVisible(true);
            AnimationFX animationFX = new FadeOut(this);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0),
                    new KeyValue(this.prefWidthProperty(), 300D, AnimateFXInterpolator.EASE)),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(this.prefWidthProperty(), 50D, AnimateFXInterpolator.EASE)));

            animationFX.setTimeline(timeline);
            ParallelAnimationFX parallelAnimationFX
                    = new ParallelAnimationFX(new FadeIn(menuBar), animationFX);
            parallelAnimationFX.play();
        }
    }
}
