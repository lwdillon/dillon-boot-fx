package com.lw.fx.client.view.system.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysDept;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDeptFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 部门管理视图模型
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptManageViewModel implements ViewModel, SceneLifecycle {
    public final static String OPEN_ALERT = "OPEN_ALERT";

    private SimpleStringProperty searchText = new SimpleStringProperty("");
    private SimpleStringProperty statusText = new SimpleStringProperty("全部");


    private ObservableList<SysDept> allData = FXCollections.observableArrayList();

    private List<SysDept> deptList = CollUtil.newArrayList();

    public String getSearchText() {
        return searchText.get();
    }

    public SimpleStringProperty searchTextProperty() {
        return searchText;
    }

    public String getStatusText() {
        return statusText.get();
    }

    public SimpleStringProperty statusTextProperty() {
        return statusText;
    }

    public void initialize() {

    }

    public void someAction() {
        publish(OPEN_ALERT, "Some Error has happend");
    }


    @Override
    public void onViewAdded() {
        System.err.println("------add");
    }


    public ObservableList<SysDept> getAllData() {
        return allData;
    }


    public List<SysDept> getDeptList() {
        return deptList;
    }

    @Override
    public void onViewRemoved() {
        unsubscribe(OPEN_ALERT, (s, objects) -> {
        });
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (Iterator<SysDept> iterator = depts.iterator(); iterator.hasNext(); ) {
            SysDept dept = (SysDept) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = (SysDept) it.next();
            if (n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }


    /**
     * 获取部门列表
     */

    public void query() {
        deptList.clear();
        String status = statusText.getValue();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("deptName", searchText.getValue());
        queryMap.put("status", StrUtil.equals("全部", status) ? "" : (StrUtil.equals("正常", status) ? "0" : "1"));
        JsonObject routers = Request.connector(SysDeptFeign.class).list(queryMap);

        JsonArray array = routers.getAsJsonArray(AjaxResult.DATA_TAG);
        List<SysDept> SysDeptList = buildDeptTree(JSONUtil.toList(array.toString(), SysDept.class));

        deptList.addAll(SysDeptList);
    }

    public void remove(Long deptId) {
        Request.connector(SysDeptFeign.class).remove(deptId);

    }

    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
    }


}
