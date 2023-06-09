package org.dillon.fx.view.window;

import animatefx.animation.BounceInRight;
import atlantafx.base.controls.Message;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dillon.fx.constant.HttpStatus;
import org.dillon.fx.view.loginregister.LoginRegisterView;
import org.dillon.fx.view.loginregister.LoginRegisterViewModel;
import org.dillon.fx.view.main.MainView;
import org.dillon.fx.view.main.MainViewModel;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowView implements FxmlView<WindowViewModel>, Initializable {

    @InjectViewModel
    private WindowViewModel windowViewModel;

    @FXML
    private VBox messagePane;

    @FXML
    private StackPane rootPane;
    @FXML
    private StackPane contentPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        messagePane.visibleProperty().bindBidirectional(windowViewModel.mainViewVisbleProperty());
        MvvmFX.getNotificationCenter().subscribe("showMainView", (key, payload) -> {
            // trigger some actions
            Platform.runLater(() -> {
                showMainView();

            });

        });
        MvvmFX.getNotificationCenter().subscribe("showLoginRegister", (key, payload) -> {
            // trigger some actions
            Platform.runLater(() -> {
                showLoginView();

            });
        });

        MvvmFX.getNotificationCenter().subscribe("message", (key, payload) -> {
            // trigger some actions

            Platform.runLater(() -> {
                showMessage((Integer) payload[0], (String) payload[1]);

            });

        });
        MvvmFX.getNotificationCenter().subscribe("exit", (key, payload) -> {
            // trigger some actions

            Platform.runLater(() -> {
                Stage stage = (Stage) rootPane.getScene().getWindow();

                stage.close();

            });

        });

        showLoginView();

    }

    private void showMainView() {

        contentPane.getChildren().clear();
        ViewTuple<MainView, MainViewModel> load = FluentViewLoader.fxmlView(MainView.class).load();
        contentPane.getChildren().add(load.getView());
        windowViewModel.mainViewVisbleProperty().setValue(true);

    }

    private void showLoginView() {
        contentPane.getChildren().clear();
        ViewTuple<LoginRegisterView, LoginRegisterViewModel> viewTuple = FluentViewLoader.fxmlView(LoginRegisterView.class).load();
        contentPane.getChildren().add(viewTuple.getView());
        windowViewModel.mainViewVisbleProperty().setValue(false);

    }

    private void showMessage(int code, String msg) {

        if (windowViewModel.isMainViewVisble()) {
            Platform.runLater(() -> {


                Message message = null;
                if (code == HttpStatus.SUCCESS) {
                    message = new Message("消息提示", msg, new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
                    message.getStyleClass().addAll(Styles.SUCCESS);
                } else {
                    message = new Message("消息提示", msg, new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));
                    message.getStyleClass().addAll(Styles.DANGER);
                }
                message.setPrefHeight(Region.USE_PREF_SIZE);
                message.setMaxHeight(Region.USE_PREF_SIZE);
                StackPane.setAlignment(message, Pos.TOP_RIGHT);
                StackPane.setMargin(message, new Insets(10, 10, 0, 0));
                Message finalMessage = message;
                message.setOnClose(e -> {
                    var out = Animations.slideOutUp(finalMessage, Duration.millis(250));
                    out.setOnFinished(f -> messagePane.getChildren().remove(finalMessage));
                    out.playFromStart();
                });
//                var in = Animations.slideInDown(message, Duration.millis(250));
                if (!messagePane.getChildren().contains(message)) {
                    messagePane.getChildren().add(message);
                }
                new BounceInRight(message).play();

                Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        var out = Animations.slideOutUp(finalMessage, Duration.millis(250));
                        out.setOnFinished(f -> messagePane.getChildren().remove(finalMessage));
                        out.playFromStart();
                    }
                }));
                fiveSecondsWonder.play();
            });
        }


    }
}
