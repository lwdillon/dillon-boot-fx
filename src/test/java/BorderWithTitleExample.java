import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BorderWithTitleExample extends Application {

    private TableView tableView = new TableView();
    private  Label label = new Label("这是在边框上方的文字");

    @Override
    public void start(Stage primaryStage) {
        tableView.getColumns().addAll(new TableColumn<>("aa"),new TableColumn<>("bb"));
        tableView.setTableMenuButtonVisible(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        tableView.setTableMenuButtonVisible(true);

        // 创建一个StackPane作为容器
        StackPane stackPane = new StackPane();

        // 创建一个矩形作为边框
        Rectangle border = new Rectangle(200, 100);
        border.setStroke(Color.BLACK);
        border.setFill(Color.TRANSPARENT);

        // 创建一个Label节点作为文字
        label.setFont(Font.font("Arial", 16));
        label.setTextFill(Color.BLACK);
        label.setMinWidth(200);
        label.setPrefWidth(200);
        label.setMaxWidth(600);

        // 将矩形和文字添加到StackPane中
        stackPane.getChildren().addAll(border, label,tableView);

        root.setCenter(stackPane);

        Scene scene = new Scene(root, 400, 300);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Text on Top of Border Example");
        primaryStage.show();



        Platform.runLater(() -> {
            // 在 JavaFX 主线程上执行查找操作
            if (tableView.lookup(".show-hide-columns-button") != null) {
                Node button = tableView.lookup(".show-hide-columns-button");
                if (button != null) {
                    button.setStyle("-fx-pref-width: 100px;");
                }
                // 设置表格菜单按钮的宽度
            } else {
                System.out.println("show-hide-columns-button 未找到");
            }
        });
    }

    private Node getMenuButton() {
        final TableHeaderRow tableHeaderRow = getTableHeaderRow();
        if (tableHeaderRow == null) {
            return null;
        }
        // child identified as cornerRegion in TableHeaderRow.java
        return tableHeaderRow.getChildren().stream().filter(child -> child
                        .getStyleClass().contains("show-hide-columns-button")).findAny()
                .get();
    }

    private TableHeaderRow getTableHeaderRow() {
        final TableViewSkin<?> tableSkin = (TableViewSkin<?>) tableView
                .getSkin();
        if (tableSkin == null) {
            return null;
        }
        // find the TableHeaderRow child
        return (TableHeaderRow) tableSkin.getChildren().stream()
                .filter(child -> child instanceof TableHeaderRow).findAny()
                .get();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
