/* SPDX-License-Identifier: MIT */
package org.dillon.fx.view.control;

import atlantafx.base.controls.Spacer;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dillon.fx.utils.Containers;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.Objects;

import static atlantafx.base.theme.Styles.*;

public abstract class OverlayDialog<T extends Node> extends VBox {

    protected static final int CONTENT_CHILD_INDEX = 1;

    protected Label titleLabel;
    protected Button topCloseBtn;
    protected HBox headerBox;

    protected Button bottomCloseBtn;
    protected HBox footerBox;

    protected Runnable onCloseCallback;

    public OverlayDialog() {
        createView();
    }

    public static final String CSS = """ 
            .mfx-dialog {
              	-fx-background-color:  -color-bg-default;
              	-fx-background-radius: 10;
                -fx-border-width: 1px;
                -fx-border-color: -color-border-default;
              	-fx-border-radius: 10;
              	-fx-padding: 10;
              }
              
              .mfx-dialog #alwaysOnTop .mfx-font-icon{
              	-mfx-color: -color-fg-default;
              }
              .mfx-dialog #alwaysOnTop:hover {
              	-fx-background-color: red;
              }
              
              .mfx-dialog #alwaysOnTop:hover .mfx-font-icon {
              	-mfx-color: derive(-mfx-purple, 30%);
              }
              
              .mfx-dialog:always-on-top #alwaysOnTop {
              	-fx-background-color: derive(-mfx-purple, 140%);
              }
              
              .mfx-dialog:always-on-top #alwaysOnTop .mfx-font-icon {
              	-mfx-color: derive(-mfx-purple, 30%);
              }
              .mfx-dialog #minimize .mfx-font-icon{
              	-mfx-color: -color-fg-default;
              }
              .mfx-dialog #minimize:hover {
              	-fx-background-color: derive(-mfx-blue, 105%);
              }
              
              .mfx-dialog #minimize:hover .mfx-font-icon {
              	-mfx-color: -mfx-blue;
              }
              
              .mfx-dialog #close .mfx-font-icon{
              	-mfx-color: -color-fg-default;
              }
              .mfx-dialog #close:hover {
              	-fx-background-color: derive(-mfx-red, 90%);
              }
              
              .mfx-dialog #close:hover .mfx-font-icon {
              	-mfx-color: -mfx-red;
              }
              
              .mfx-dialog .header-label {
              	-fx-font-family: "Open Sans Bold";
              	-fx-font-size: 14;
              	-fx-text-fill: -color-fg-muted;
              	-fx-graphic-text-gap: 10;
              }
              
              .mfx-dialog .header-label .text {
              	-fx-font-smoothing-type: lcd;
              }
              
              .mfx-dialog .content-container {
              	-fx-padding: 10 0 10 0;
              	-fx-background-color:  -color-bg-default;
              }
              
              .mfx-dialog .content-container .scroll-bar:vertical {
              	-fx-pref-width: 15;
              }
              
              .mfx-dialog .content {
              	-fx-font-family: "Open Sans Regular";
              	-fx-text-fill: -color-fg-muted;
              }
              
              .mfx-dialog .content .text {
              	-fx-font-smoothing-type: lcd;
              }
                        
              /********************
              Info
              ********************/
              .mfx-info-dialog .header-label .mfx-font-icon {
              	-mfx-color: derive(-mfx-blue, 60%);
              }
              
              .mfx-info-dialog .actions-pane .mfx-button {
              	-fx-text-fill: derive(-mfx-blue, -20%);
              }
              
              .mfx-info-dialog .actions-pane .mfx-button .mfx-ripple-generator {
              	-mfx-ripple-color: derive(-mfx-blue, 80%);
              }
              
              .mfx-info-dialog .actions-pane .mfx-button:hover {
              	-fx-background-color: derive(-mfx-blue, 110%);
              }
              
              /********************
              Warn
              ********************/
              .mfx-warn-dialog .header-label .mfx-font-icon {
              	-mfx-color: derive(-mfx-orange, 35%);
              }
              
              .mfx-warn-dialog .actions-pane .mfx-button {
              	-fx-text-fill: derive(-mfx-orange, -5%);
              }
              
              .mfx-warn-dialog .actions-pane .mfx-button .mfx-ripple-generator {
              	-mfx-ripple-color: derive(-mfx-orange, 60%);
              }
              
              .mfx-warn-dialog .actions-pane .mfx-button:hover {
              	-fx-background-color: derive(-mfx-orange, 90%);
              }
              
              /********************
              Error
              ********************/
              .mfx-error-dialog .header-label .mfx-font-icon {
              	-mfx-color: derive(-mfx-red, 20%);
              }
              
              .mfx-error-dialog .actions-pane .mfx-button {
              	-fx-text-fill: derive(-mfx-red, -5%);
              }
              
              .mfx-error-dialog .actions-pane .mfx-button .mfx-ripple-generator {
              	-mfx-ripple-color: derive(-mfx-red, 65%);
              }
              
              .mfx-error-dialog .actions-pane .mfx-button:hover {
              	-fx-background-color: derive(-mfx-red, 95%);
              }
            """;

    protected void createView() {
        titleLabel = new Label();
        titleLabel.getStyleClass().addAll(TITLE_4, "title");

        topCloseBtn = new Button("", new FontIcon(Material2AL.CLOSE));
        topCloseBtn.getStyleClass().addAll(BUTTON_ICON, BUTTON_CIRCLE, FLAT, "close-button");
        topCloseBtn.setOnAction(e -> close());

        headerBox = new HBox(10);
        headerBox.getStyleClass().add("header");
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().setAll(
                titleLabel,
                new Spacer(),
                topCloseBtn
        );
        VBox.setVgrow(headerBox, Priority.NEVER);

        bottomCloseBtn = new Button("取消");
        bottomCloseBtn.getStyleClass().add("form-action");
        bottomCloseBtn.setOnAction(e -> close());
        bottomCloseBtn.setCancelButton(true);

        footerBox = new HBox(10);
        footerBox.getStyleClass().add("footer");
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.getChildren().setAll(
                new Spacer(),
                bottomCloseBtn
        );
        VBox.setVgrow(footerBox, Priority.NEVER);

        // IMPORTANT: this guarantees client will use correct width and height
        Containers.usePrefWidth(this);
        Containers.usePrefHeight(this);

        // if you're updating this line, update setContent() method as well
        getChildren().setAll(headerBox, footerBox);

        getStyleClass().add("overlay-dialog");
    }

    protected void setContent(T content) {
        Objects.requireNonNull(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        // content have to be placed exactly between header and footer
        if (getChildren().size() == 2) {
            // add new content
            getChildren().add(CONTENT_CHILD_INDEX, content);
        } else if (getChildren().size() == 3) {
            // overwrite existing content
            getChildren().set(CONTENT_CHILD_INDEX, content);
        } else {
            throw new UnsupportedOperationException("Content cannot be placed because of unexpected children size. " +
                    "You should override 'OverlayDialog#setContent()' and place it manually.");
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }

    public Runnable getOnCloseRequest() {
        return onCloseCallback;
    }

    public void setOnCloseRequest(Runnable handler) {
        this.onCloseCallback = handler;
    }
}
