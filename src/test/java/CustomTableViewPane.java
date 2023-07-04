import javafx.scene.AccessibleAction;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class CustomTableViewPane extends AnchorPane {

    private StackPane tablePane;
    private MenuButton menuButton;

    private TableView tableView;

    public CustomTableViewPane(TableView tableView) {

        this.tableView = tableView;
        initViews();
    }

    private void initViews() {
        tablePane = new StackPane(tableView);
        menuButton = new MenuButton("你怎么写都可以");
        AnchorPane.setTopAnchor(menuButton, 0.0);
        AnchorPane.setRightAnchor(menuButton, 0.0);

        AnchorPane.setTopAnchor(tablePane,0.0);
        AnchorPane.setRightAnchor(tablePane,0.0);
        AnchorPane.setBottomAnchor(tablePane,0.0);
        AnchorPane.setLeftAnchor(tablePane,0.0);
        this.getChildren().addAll(tablePane, menuButton);

        if (tableView != null) {

            for (int i = 0; i < tableView.getColumns().size(); i++) {
                TableColumn column = (TableColumn) tableView.getColumns().get(i);
                CheckMenuItem item = new CheckMenuItem(column.getText());
                item.setSelected(true);
                item.setOnAction(actionEvent -> {
                    column.setVisible(item.isSelected());
                });
                menuButton.getItems().add(item);
            }

            TableColumn lastCol = (TableColumn) tableView.getColumns().get(tableView.getColumns().size()-1);

            if (lastCol != null) {
                lastCol.setMinWidth(lastCol.getWidth()+menuButton.getWidth());
            }

        }
    }
}
