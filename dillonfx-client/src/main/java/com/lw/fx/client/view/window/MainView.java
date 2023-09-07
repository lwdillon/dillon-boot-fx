package com.lw.fx.client.view.window;

import atlantafx.base.controls.Popover;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.lw.fx.client.icon.WIcon;
import com.lw.fx.client.util.Lazy;
import com.lw.fx.client.view.config.UserInfoView;
import com.lw.fx.client.view.dashboard.CountryDashboardView;
import com.lw.fx.client.view.general.ThemePage;
import com.lw.fx.client.view.home.DashboardView;
import com.lw.fx.client.view.home.HomeView;
import com.lw.fx.client.view.monitor.MonitorView;
import com.lw.fx.client.view.system.config.ConfigView;
import com.lw.fx.client.view.system.dept.DeptManageView;
import com.lw.fx.client.view.system.dict.type.DictTypeView;
import com.lw.fx.client.view.system.logininfor.LoginInforView;
import com.lw.fx.client.view.system.menu.MenuManageView;
import com.lw.fx.client.view.system.notice.NoticeView;
import com.lw.fx.client.view.system.operlog.OperLogView;
import com.lw.fx.client.view.system.post.PostView;
import com.lw.fx.client.view.system.role.RoleView;
import com.lw.fx.client.view.system.tool.ToolView;
import com.lw.fx.client.view.system.tool.ToolViewModel;
import com.lw.fx.client.view.system.user.UserView;
import de.saxsys.mvvmfx.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.controls.Popover.ArrowLocation.TOP_RIGHT;
import static atlantafx.base.theme.Styles.*;

public class MainView implements FxmlView<MainViewModel>, Initializable {


    @FXML
    private BorderPane rootPane;

    @FXML
    private HBox centerPane;

    @FXML
    private Button themeBut;
    @FXML
    private Button userBut;


    @FXML
    private Button minimizeBut;

    @FXML
    private Button maximizeBut;
    @FXML
    private Button closeBut;
    @FXML
    private Button logoBut;
    @FXML
    private ToggleButton menuButton;

    private TabPane tabPane;
    private SideMenu sideMenu;
    private Lazy<ThemeDialog> themeDialog;

    @InjectViewModel
    private MainViewModel mainViewModel;

    public MainView() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.getStyleClass().add("main-view");
        sideMenu = new SideMenu(mainViewModel);
        tabPane = new TabPane();

        HBox.setHgrow(tabPane, Priority.ALWAYS);

        centerPane.getChildren().addAll(sideMenu, tabPane);
        centerPane.getStyleClass().add("bordered-top-bottom");
        menuButton.selectedProperty().addListener((observableValue, aBoolean, newValue) -> {
            sideMenu.expansion(newValue);
        });


        themeDialog = new Lazy<>(() -> {
            var dialog = new ThemeDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });

        Region theme = new Region();
        theme.getStyleClass().add("theme-icon");

        Region minus = new Region();
        minus.getStyleClass().add("minus-icon");

        Region resizeMax = new Region();
        resizeMax.getStyleClass().add("resize-max-icon");

        Region resizeMin = new Region();
        resizeMin.getStyleClass().add("resize-min-icon");

        Region close = new Region();
        close.getStyleClass().add("close-icon");

        mainViewModel.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            maximizeBut.setTooltip(new Tooltip(newVal ? "还原" : "最大化"));
            maximizeBut.setGraphic(newVal ? resizeMin : resizeMax);

        });


//        logoBut.setGraphic(new FontIcon(Feather.STAR));
        logoBut.textProperty().bindBidirectional(mainViewModel.titleProperty());
        logoBut.getStyleClass().addAll(BUTTON_OUTLINED, FLAT, "title-1");


        themeBut.setGraphic(theme);
        themeBut.setOnAction(e -> openThemeDialog());
        userBut.setOnAction(event -> showPersonalInformation());

        minimizeBut.setOnAction(actionEvent -> {

            if (minimizeBut.getScene() instanceof BorderlessScene) {
                ((BorderlessScene) maximizeBut.getScene()).minimizeStage();
            }
        });
        minimizeBut.setGraphic(minus);

        maximizeBut.setGraphic(resizeMax);
        maximizeBut.setOnAction(actionEvent -> {

            if (maximizeBut.getScene() instanceof BorderlessScene) {

                ((BorderlessScene) maximizeBut.getScene()).maximizeStage();
            }
        });

        closeBut.setGraphic(close);
        closeBut.setOnAction(actionEvent -> {
            Stage stage = (Stage) closeBut.getScene().getWindow();

            stage.close();
        });


        menuButton.setSelected(true);
        menuButton.setGraphic(new FontIcon(Feather.MENU));
        menuButton.getStyleClass().addAll(FLAT, BUTTON_CIRCLE);
        initListeners();

        loddTab("主页", "home", FluentViewLoader.fxmlView(DashboardView.class).load().getView());


        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();


        MvvmFX.getNotificationCenter().subscribe("showThemePage", (key, payload) -> {
            // trigger some actions
            Platform.runLater(() -> {
                loddTab("个性化设置", "home", new ThemePage());

            });
        });

    }


    private void initListeners() {
        mainViewModel.addTabProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loddTab(newValue);
            }
        });
        MvvmFX.getNotificationCenter().subscribe("addTab", (key, payload) -> {

            // trigger some actions
            Platform.runLater(() -> {
                loddTab((String) payload[0], (String) payload[1], (Parent) payload[2]);

            });

        });


    }


    private void loddTab(JSONObject obj) {
        var title = obj.getJSONObject("meta").getStr("title");
        String component = ((JSONObject) obj).getStr("component");
        String iconStr = ((JSONObject) obj).getJSONObject("meta").getStr("icon");
        Tab tab = null;
        String finalTitle = title;
        var tabOptional = tabPane.getTabs().stream()
                .filter(t -> StrUtil.equals(t.getText(), finalTitle))
                .findFirst();

        if (tabOptional.isPresent()) {
            tab = tabOptional.get();
        } else {

            Class clazz = null;

            if (StrUtil.equals("菜单管理", title)) {
                clazz = MenuManageView.class;
            } else if (StrUtil.equals("用户管理", title)) {
                clazz = UserView.class;
            } else if (StrUtil.equals("角色管理", title)) {
                clazz = RoleView.class;
            } else if (StrUtil.equals("部门管理", title)) {
                clazz = DeptManageView.class;
            } else if (StrUtil.equals("岗位管理", title)) {
                clazz = PostView.class;
            } else if (StrUtil.equals("字典管理", title)) {
                clazz = DictTypeView.class;
            } else if (StrUtil.equals("参数设置", title)) {
                clazz = ConfigView.class;
            } else if (StrUtil.equals("通知公告", title)) {
                clazz = NoticeView.class;
            } else if (StrUtil.equals("操作日志", title)) {
                clazz = OperLogView.class;
            } else if (StrUtil.equals("登录日志", title)) {
                clazz = LoginInforView.class;
            } else if (StrUtil.equals("服务监控", title)) {
                clazz = MonitorView.class;
            } else if (StrUtil.equals("若依官网", title)) {
                title = "主页2";
                iconStr = "home";
                clazz = CountryDashboardView.class;
            } else {

                if (component.contains("/")) {
                    clazz = ToolView.class;
                } else {

                    try {
                        clazz = Class.forName(component);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


            Node node = null;

            if (clazz == ToolView.class) {
                ViewTuple<ToolView, ToolViewModel> viewTuple = FluentViewLoader.fxmlView(ToolView.class).load();
                viewTuple.getViewModel().setUrl("http://vue.ruoyi.vip/" + ((JSONObject) obj).getStr("component").replace("/index", "").replace("/list", "List"));
                MvvmFX.getNotificationCenter().publish("addTab", "若依演示", "", viewTuple.getView());
                return;
            } else {
                node = FluentViewLoader.fxmlView(clazz).load().getView();
            }
            tab = new Tab(title);
            tab.setId("main-tab");
            tab.setGraphic(new FontIcon(WIcon.findByDescription("lw-" + iconStr)));
            tab.setContent(node);
            tabPane.getTabs().add(tab);

        }

        tabPane.getSelectionModel().select(tab);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), tab.getContent());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void loddTab(String title, String icon, Parent node) {
        Tab tab = null;
        var tabOptional = tabPane.getTabs().stream()
                .filter(t -> StrUtil.equals(t.getText(), title))
                .findFirst();

        if (tabOptional.isPresent()) {
            tab = tabOptional.get();
            tabPane.getTabs().remove(tab);
        }
        tab = new Tab(title);
        tab.setId("main-tab");
        FontIcon fontIcon = new FontIcon(WIcon.HOME);
        fontIcon.setIconSize(24);
        tab.setGraphic(fontIcon);
        tabPane.getTabs().add(tab);
        tab.setContent(node);
        tabPane.getSelectionModel().select(tab);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), tab.getContent());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void showPersonalInformation() {
        Popover popover = new Popover(FluentViewLoader.fxmlView(UserInfoView.class).load().getView());
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(TOP_RIGHT);
        Bounds bounds = userBut.localToScreen(userBut.getBoundsInLocal());
        popover.show(userBut, bounds.getMinX(), bounds.getMinY() + bounds.getHeight());
    }


    private void openThemeDialog() {
        var dialog = themeDialog.get();
        dialog.show(rootPane.getScene());
        Platform.runLater(dialog::requestFocus);
    }

}
