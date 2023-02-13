import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListViewSample extends Application {

    ListView<TestBean> list = new ListView<>();
    static ObservableList<TestBean> data = FXCollections.observableArrayList();
    ;

    @Override
    public void start(Stage stage) {
        VBox box = new VBox();
        Scene scene = new Scene(box, 200, 200);
        stage.setScene(scene);
        stage.setTitle("ListViewSample");
        box.getChildren().addAll(list);
        VBox.setVgrow(list, Priority.ALWAYS);

        list.setItems(data);

        list.setCellFactory((ListView<TestBean> l) -> new ColorRectCell());
        lodingData();
        stage.show();
    }

    static void lodingData() {
        data.clear();
        for (int i = 0; i < 7000; i++) {
            data.add(new TestBean("已经取消" + i, "220101MD8HTDXK" + i, "3a0125a3-7216-3a49-d13f-6fa5d4d056b3" + i, "220101-063590972952628" + i));
        }
    }

    ;

    static class ColorRectCell extends ListCell<TestBean> {
        @Override
        public void updateItem(TestBean item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                HBox box = new HBox();
                box.setAlignment(Pos.CENTER_LEFT);
                Label label = new Label(item.getOrderNo());
                label.setMaxWidth(Double.MAX_VALUE);
                Button button = new Button(item.getRemark());
                button.setOnAction(event -> {
                    System.err.println(item.getOrderNo());
                });
                box.getChildren().addAll(label, button);
                HBox.setHgrow(label, Priority.ALWAYS);
                setGraphic(box);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}