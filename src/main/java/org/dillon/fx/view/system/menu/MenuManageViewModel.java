package org.dillon.fx.view.system.menu;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.SysMenuFeign;
import org.dillon.fx.vo.SysMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuManageViewModel implements ViewModel, SceneLifecycle {
    public final static String OPEN_ALERT = "OPEN_ALERT";

    private ObservableList<SysMenu> allData = FXCollections.observableArrayList();
    private StringProperty text = new SimpleStringProperty();

    private Command listCommand;


    public void initialize() {

        listCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                list();
            }
        }, true); //Async
    }

    public void someAction() {
        publish(OPEN_ALERT, "Some Error has happend");
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    @Override
    public void onViewAdded() {
        System.err.println("------add");
    }

    public Command getListCommand() {
        return listCommand;
    }

    public ObservableList<SysMenu> getAllData() {
        return allData;
    }

    @Override
    public void onViewRemoved() {
        unsubscribe(OPEN_ALERT, (s, objects) -> {
        });
    }

    /**
     * 获取菜单列表
     */

    private void list() {
        allData.clear();
        JsonObject routers = Request.connector(SysMenuFeign.class).list(new HashMap<>());

        JsonArray array = routers.getAsJsonArray(AjaxResult.DATA_TAG);

        JSONArray objects =JSONUtil.parseArray(array.toString());
        List<SysMenu> mapList = JSONUtil.toList(objects, SysMenu.class);
        allData.addAll(mapList);

    }


}
