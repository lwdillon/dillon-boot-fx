package com.lw.fx.client.view.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class WFXGenericDialog extends ModalDialog {
    private final ObjectProperty<Orientation> actionsOrientation = new SimpleObjectProperty<>(Orientation.HORIZONTAL);

    protected Pane actions;

    public WFXGenericDialog() {
        this(null, null, null);
    }

    public WFXGenericDialog(String title) {
        this(null, title, null);
    }

    public WFXGenericDialog(String title, Node body) {
        this(null, title, body);
    }

    public WFXGenericDialog(Node icon, String title, Node body) {
        super();
        actionsOrientationProperty().addListener(invalidated -> buildActionsPane());
        buildActionsPane();
        setId("wfx-generic-dialog");
        header.setTitle(title);
        header.setGraphic(icon);
        content.setBody(body);
        content.setFooter(actions);
//        content.setPrefSize(600, 500);

    }

    public void setHeaderIcon(Node icon) {
        header.setGraphic(icon);
    }

    public void setHeaderText(String text) {
        header.setTitle(text);
    }

    public void setContent(Node body) {
        content.setBody(body);
    }

    protected void buildActionsPane() {
        ObservableList<Node> children = FXCollections.observableArrayList();
        if (actions != null) {
            getChildren().remove(actions);
            children.addAll(actions.getChildren());
        }

        if (getActionsOrientation() == Orientation.HORIZONTAL) {
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_RIGHT);
            this.actions = actions;
        } else {
            VBox actions = new VBox(10);
            actions.setAlignment(Pos.TOP_RIGHT);
            this.actions = actions;
        }
        actions.getChildren().setAll(children);
        actions.getStyleClass().add("actions-pane");
    }

    /**
     * Adds the specified nodes to the actions pane.
     */
    public void addActions(Node... actions) {
        this.actions.getChildren().addAll(actions);
    }

    /**
     * Each entry has a {@link Node} that will trigger the given action on {@link MouseEvent#MOUSE_CLICKED}.
     * <p>
     * For each entry, adds the given {@link EventHandler} to the given {@link Node}, than
     * adds it to the actions pane.
     */
    @SafeVarargs
    public final void addActions(Map.Entry<Node, EventHandler<MouseEvent>>... actions) {
        for (Map.Entry<Node, EventHandler<MouseEvent>> action : actions) {
            action.getKey().addEventHandler(MouseEvent.MOUSE_CLICKED, action.getValue());
            this.actions.getChildren().add(action.getKey());
        }
    }

    /**
     * Removes all the nodes from the actions pane.
     */
    public void clearActions() {
        actions.getChildren().clear();
    }


    public Orientation getActionsOrientation() {
        return actionsOrientation.get();
    }

    public ObjectProperty<Orientation> actionsOrientationProperty() {
        return actionsOrientation;
    }

    public void setActionsOrientation(Orientation actionsOrientation) {
        this.actionsOrientation.set(actionsOrientation);
    }
}
