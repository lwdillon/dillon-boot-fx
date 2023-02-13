package org.dillon.fx.view.main;

import cn.hutool.json.JSONBeanParser;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ruoyi.common.core.web.domain.AjaxResult;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.SysMenuFeign;
import org.dillon.fx.store.AppStore;
import org.dillon.fx.theme.SamplerTheme;
import org.dillon.fx.theme.ThemeManager;

import java.util.List;
import java.util.Objects;


public class MainViewModel implements ViewModel, SceneLifecycle {
    private Command getRoutersCommand;

    public final static String SWITCH_THEME = "switchTheme";

    private final ReadOnlyObjectWrapper<JSONObject> addTab = new ReadOnlyObjectWrapper<>();

    private ObservableList<JSONObject> routerList;
    private SimpleBooleanProperty maximized = new SimpleBooleanProperty(false);
    private SimpleObjectProperty theme = new SimpleObjectProperty();
    private SimpleBooleanProperty showNavigationBar = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty routersUpdate = new SimpleBooleanProperty(false);

    private StringProperty title = new SimpleStringProperty(System.getProperty("app.name"));


    public void initialize() {

        getRoutersCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                getRouters();
            }
        }, true); //Async
        getRoutersCommand.execute();
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }


    /**
     * 在视图中添加
     */
    @Override
    public void onViewAdded() {

        setTheme(ThemeManager.getInstance().getDefaultTheme());
    }

    /**
     * 在视图中删除
     */
    @Override
    public void onViewRemoved() {
        System.err.println("------remove");
    }


    public boolean isMaximized() {
        return maximized.get();
    }

    public SimpleBooleanProperty maximizedProperty() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized.set(maximized);
    }


    public boolean isShowNavigationBar() {
        return showNavigationBar.get();
    }

    public SimpleBooleanProperty showNavigationBarProperty() {
        return showNavigationBar;
    }

    public void setShowNavigationBar(boolean showNavigationBar) {
        this.showNavigationBar.set(showNavigationBar);
    }

    public Object getTheme() {
        return theme.get();
    }

    public void setTheme(SamplerTheme theme) {
        this.theme.set(theme);
        publish(SWITCH_THEME, theme);
    }


    public ObservableList<JSONObject> getRouterList() {
        return routerList;
    }

    public SimpleBooleanProperty routersUpdateProperty() {
        return routersUpdate;
    }

    public ReadOnlyObjectProperty<JSONObject> addTabProperty() {
        return addTab.getReadOnlyProperty();
    }

    public void getRouters() {
        JsonObject routers = Request.connector(SysMenuFeign.class).getRouters();

        JsonArray array = routers.getAsJsonArray(AjaxResult.DATA_TAG);

        List<JSONObject> ru = JSONUtil.toList(array.toString(), JSONObject.class);

        routerList = FXCollections.observableArrayList(ru);
        Platform.runLater(() -> routersUpdate.setValue(true));

    }

    public void addTab(JSONObject obj) {
        addTab.set(Objects.requireNonNull(obj));
    }
}
