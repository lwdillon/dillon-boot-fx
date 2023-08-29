package org.dillon.fx.view.main;

import atlantafx.base.controls.Popover;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import de.saxsys.mvvmfx.*;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dillon.fx.icon.WIcon;
import org.dillon.fx.theme.SamplerTheme;
import org.dillon.fx.theme.ThemeManager;
import org.dillon.fx.view.config.UserInfoView;
import org.dillon.fx.view.home.DashboardView;
import org.dillon.fx.view.home.HomeView;
import org.dillon.fx.view.monitor.MonitorView;
import org.dillon.fx.view.system.config.ConfigView;
import org.dillon.fx.view.system.dept.DeptManageView;
import org.dillon.fx.view.system.dict.data.DictDataView;
import org.dillon.fx.view.system.dict.data.DictDataViewModel;
import org.dillon.fx.view.system.dict.type.DictTypeView;
import org.dillon.fx.view.system.logininfor.LoginInforView;
import org.dillon.fx.view.system.menu.MenuManageView;
import org.dillon.fx.view.system.notice.NoticeView;
import org.dillon.fx.view.system.operlog.OperLogView;
import org.dillon.fx.view.system.post.PostView;
import org.dillon.fx.view.system.role.RoleView;
import org.dillon.fx.view.system.tool.ToolView;
import org.dillon.fx.view.system.tool.ToolViewModel;
import org.dillon.fx.view.system.user.UserView;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.controls.Popover.ArrowLocation.TOP_CENTER;
import static atlantafx.base.controls.Popover.ArrowLocation.TOP_RIGHT;
import static atlantafx.base.theme.Styles.BUTTON_OUTLINED;
import static atlantafx.base.theme.Styles.FLAT;
import static org.dillon.fx.view.main.MainViewModel.SWITCH_THEME;

public class MainView implements FxmlView<MainViewModel>, Initializable {


    @FXML
    private BorderPane rootPane;

    @FXML
    private HBox centerPane;

    @FXML
    private MFXButton themeBut;
    @FXML
    private MFXButton userBut;


    @FXML
    private MFXButton minimizeBut;

    @FXML
    private MFXButton maximizeBut;
    @FXML
    private MFXButton closeBut;
    @FXML
    private Button logoBut;
    @FXML
    private ToggleButton menuButton;

    private Popover quickConfigPopover;
    private TabPane tabPane;
    private SideMenu sideMenu;

    @InjectViewModel
    private MainViewModel mainViewModel;

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


        mainViewModel.subscribe(SWITCH_THEME, (key, payload) -> {
            SamplerTheme samplerTheme = (SamplerTheme) payload[0];
            ThemeManager.getInstance().setTheme(samplerTheme);
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


        logoBut.setGraphic(new FontIcon(Feather.STAR));
        logoBut.textProperty().bindBidirectional(mainViewModel.titleProperty());
        logoBut.getStyleClass().addAll(BUTTON_OUTLINED, FLAT);


        themeBut.setGraphic(theme);
        themeBut.setOnAction(e -> showThemeConfigPopover(themeBut));
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

        initListeners();

        loddTab("主页", "home", FluentViewLoader.fxmlView(DashboardView.class).load().getView());

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();


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
                clazz = DashboardView.class;
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
        tab.setGraphic(new FontIcon(WIcon.findByDescription("lw-" + icon)));
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


    private void showThemeConfigPopover(Node source) {
        if (quickConfigPopover == null) {
            var content = new QuickConfigMenu();
            content.setExitHandler(() -> quickConfigPopover.hide());

            quickConfigPopover = new Popover(content);
            quickConfigPopover.setHeaderAlwaysVisible(false);
            quickConfigPopover.setDetachable(false);
            quickConfigPopover.setArrowLocation(TOP_CENTER);
            quickConfigPopover.setOnShowing(e -> content.update());
        }
        Bounds bounds = source.localToScreen(source.getBoundsInLocal());
        quickConfigPopover.show(source, bounds.getMinX(), bounds.getMinY() + bounds.getHeight());
    }

}
