import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class CustomTableViewSkinExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<String> tableView = new TableView<>();
        TableColumn<String, String> column1 = new TableColumn<>("Column 1");
        TableColumn<String, String> column2 = new TableColumn<>("Column 2");
        TableColumn<String, String> column3 = new TableColumn<>("Column 3");
        tableView.getColumns().addAll(column1, column2, column3);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getItems().addAll("Data 1", "Data 2", "Data 3");
        CustomTableViewPane customTableViewPane = new CustomTableViewPane(tableView);
        HBox root = new HBox(customTableViewPane);
        HBox.setHgrow(customTableViewPane, Priority.ALWAYS);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
