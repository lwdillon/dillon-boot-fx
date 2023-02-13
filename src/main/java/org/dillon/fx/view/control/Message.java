/* SPDX-License-Identifier: MIT */
package org.dillon.fx.view.control;

import animatefx.animation.BounceInRight;
import animatefx.animation.BounceOutRight;
import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import atlantafx.base.theme.Styles;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.Objects;
import java.util.function.Consumer;

public class Message extends StackPane {

    private static final int ANIMATION_DURATION = 500;

    public enum Type {
        INFO, SUCCESS, WARNING, DANGER
    }

    public static final String CSS = """
            .message {
              -color-message-bg: -color-bg-default;
              -color-message-fg: -color-fg-default;
              -fx-background-color: -color-message-bg;
              -fx-border-color:     -color-message-fg;
              -fx-border-width: 0 0 0 5px;
              -fx-pref-width: 600px;
              -fx-alignment: TOP_LEFT;
            }
            .message > .header {
              -fx-font-weight: bold;
            }
            .message Text {
              -fx-fill: -color-message-fg;
            }
            .message > .button {
              -color-button-fg: -color-message-fg;
            }
            .message.info {
              -color-message-bg: -color-accent-subtle;
              -color-message-fg: -color-accent-fg;
            }
            .message.success {
              -color-message-bg: -color-success-subtle;
              -color-message-fg: -color-success-fg;
            }
            .message.warning {
              -color-message-bg: -color-warning-subtle;
              -color-message-fg: -color-warning-fg;
            }
            .message.danger {
              -color-message-bg: -color-danger-subtle;
              -color-message-fg: -color-danger-fg;
            }
            """;

    private final Type type;
    private final String header;
    private final String text;
    private Consumer<Message> closeHandler;

    public Message(Type type, String header, String text) {
        this.type = Objects.requireNonNull(type);
        this.header = header;
        this.text = Objects.requireNonNull(text);
        createView();

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                handleClose();
            }
        }));
//        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

    }

    private void createView() {
        if (header != null) {
            var headerText = new Text(header);
            headerText.getStyleClass().addAll("header");
            StackPane.setMargin(headerText, new Insets(10, 10, 0, 15));
            getChildren().add(headerText);
        }

        var messageText = new TextFlow(new Text(text));
        var icon = FontIcon.of(Feather.BOOK);
        icon.getStyleClass().setAll("msg-icon", type.name().toLowerCase());
        if (header != null) {
            StackPane.setMargin(icon, new Insets(40, 10, 10, 15));
            StackPane.setMargin(messageText, new Insets(40, 10, 10, 40));
        } else {
            StackPane.setMargin(icon, new Insets(10, 10, 10, 15));
            StackPane.setMargin(messageText, new Insets(10, 10, 10, 40));
        }
        messageText.maxWidthProperty().bind(widthProperty().subtract(50));
        getChildren().addAll(icon, messageText);

        var closeBtn = new Button("", new FontIcon(Material2AL.CLOSE));
        closeBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.FLAT);
        closeBtn.setOnAction(e -> handleClose());
        StackPane.setMargin(closeBtn, new Insets(2));
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        getChildren().add(closeBtn);

        parentProperty().addListener((obs, old, val) -> {
            if (val != null) {
//                handleOpen();
            }
        });

        getStyleClass().setAll("message", type.name().toLowerCase());
    }

    public Type getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public void setCloseHandler(Consumer<Message> closeHandler) {
        this.closeHandler = closeHandler;
    }

    public void handleOpen() {
//        var transition = new FadeTransition(new Duration(500), this);
//        transition.setFromValue(0);
//        transition.setToValue(1);
//        transition.play();
        new BounceInRight(this).play();
    }

    private void handleClose() {
        var transition = new BounceOutRight(this);
        transition.setOnFinished(e -> {
            if (getParent() != null && getParent() instanceof Pane pane) {
                pane.getChildren().remove(this);
            }
            if (closeHandler != null) {
                closeHandler.accept(this);
            }
        });
        transition.play();
    }
}
