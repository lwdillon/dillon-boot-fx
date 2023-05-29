import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MultiLevelSideMenuDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 创建一级菜单列表
        TreeView<String> firstLevelMenu = new TreeView<>();
        TreeItem<String> root = new TreeItem<>("菜单");
        root.setExpanded(true);
        firstLevelMenu.setRoot(root);

        // 创建二级菜单列表
        TreeItem<String> menuItem1 = new TreeItem<>("菜单项1");
        TreeItem<String> menuItem2 = new TreeItem<>("菜单项2");
        TreeItem<String> menuItem3 = new TreeItem<>("菜单项3");
        TreeItem<String> menuItem4 = new TreeItem<>("菜单项4");
        root.getChildren().addAll(menuItem1, menuItem2, menuItem3, menuItem4);

        // 创建三级菜单列表
        TreeItem<String> subMenuItem1 = new TreeItem<>("子菜单项1");
        TreeItem<String> subMenuItem2 = new TreeItem<>("子菜单项2");
        menuItem2.getChildren().addAll(subMenuItem1, subMenuItem2);

        // 创建内容区域
        Label content = new Label("这里是内容区域");

        // 创建 BorderPane 布局，并设置左侧的 SideMenu 和中心的内容区域
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(firstLevelMenu);
        borderPane.setCenter(content);

        // 创建 VBox 布局，并将 BorderPane 放入其中
        VBox vbox = new VBox();
        vbox.getChildren().addAll(borderPane);

        // 创建场景和舞台，并设置场景的根节点为 VBox 布局
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Multi-Level SideMenu 示例");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
