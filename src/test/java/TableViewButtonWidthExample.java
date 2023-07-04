import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TableViewButtonWidthExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<String> tableView = new TableView<>();
        TableColumn<String, String> column1 = new TableColumn<>("Column 1");
        TableColumn<String, String> column2 = new TableColumn<>("Column 2");
        tableView.getColumns().addAll(column1, column2);
        tableView.getItems().addAll("Data 1", "Data 2", "Data 3");
        tableView.setTableMenuButtonVisible(true);
        HBox root = new HBox(tableView);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 设置 show-hide-columns-button 宽度
        tableView.applyCss();
        tableView.layout();
        Region showHideButton = (Region) tableView.lookup(".show-hide-columns-button");
        if (showHideButton != null) {
            showHideButton.setMinWidth(100);
            showHideButton.setMaxWidth(100);
            showHideButton.setPrefWidth(100);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
