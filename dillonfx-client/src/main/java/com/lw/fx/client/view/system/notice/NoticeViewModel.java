package com.lw.fx.client.view.system.notice;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lw.fx.client.domain.SysDictData;
import com.lw.fx.client.domain.SysNotice;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDictDataFeign;
import com.lw.fx.client.request.feign.client.SysNoticeFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NoticeViewModel implements ViewModel {

    private ObservableList<SysNotice> sysNotices = FXCollections.observableArrayList();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<SysDictData> noticeType = new SimpleObjectProperty<>();
    private StringProperty noticeTitle = new SimpleStringProperty();
    private StringProperty createBy = new SimpleStringProperty();
    private ObservableList<SysDictData> noticeDataObservableList = FXCollections.observableArrayList();
    private Map<String, SysDictData> sysDictDataMap = new HashMap<>();

    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    public NoticeViewModel() {
        initData();
    }

    private void initData() {
        ProcessChain.create().addRunnableInPlatformThread(() -> noticeDataObservableList.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysDictDataFeign.class).dictType("sys_notice_type")
                )
                .addConsumerInPlatformThread(r -> {

                    JSONObject objects = JSONUtil.parseObj(r.toString());
                    List<SysDictData> sysDictDataList = JSONUtil.toList(objects.getJSONArray("data"), SysDictData.class);
                    Map<String, SysDictData> stringSysDictDataMap = sysDictDataList.stream().collect(Collectors.toMap(SysDictData::getDictValue, Function.identity(), (key1, key2) -> key2));
                    setSysDictDataMap(stringSysDictDataMap);
                    noticeDataObservableList.setAll(sysDictDataList);
                })
                .addRunnableInExecutor(() -> updateData())
                .onException(e -> e.printStackTrace()).run();
    }

    public void updateData() {


        Map<String, Object> querMap = new HashMap<>();

        querMap.put("noticeTitle", noticeTitle.getValue());
        if (ObjectUtil.isAllNotEmpty(noticeType, noticeType.getValue())) {
            querMap.put("status", noticeType.getValue().getDictValue());

        }
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysNotices.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysNoticeFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysNotice> notices = BeanUtil.copyToList(r.getRows(), SysNotice.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysNotices.addAll(notices);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void reset() {
        noticeTitle.setValue("");
        createBy.setValue("");
        noticeType.setValue(null);
    }

    public ObservableList<SysNotice> getSysNotices() {
        return sysNotices;
    }


    public void setSysNotices(ObservableList<SysNotice> sysNotices) {
        this.sysNotices = sysNotices;
    }


    public SysDictData getNoticeType() {
        return noticeType.get();
    }

    public ObjectProperty<SysDictData> noticeTypeProperty() {
        return noticeType;
    }

    public void setNoticeType(SysDictData noticeType) {
        this.noticeType.set(noticeType);
    }

    public String getNoticeTitle() {
        return noticeTitle.get();
    }

    public StringProperty noticeTitleProperty() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle.set(noticeTitle);
    }

    public String getCreateBy() {
        return createBy.get();
    }

    public StringProperty createByProperty() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy.set(createBy);
    }

    public int getTotal() {
        return total.get();
    }

    public SimpleIntegerProperty totalProperty() {
        return total;
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public int getPageNum() {
        return pageNum.get();
    }

    public IntegerProperty pageNumProperty() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum.set(pageNum);
    }

    public long getPageSize() {
        return pageSize.get();
    }

    public IntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize.set(pageSize);
    }

    public void del(String noticeIds) {
        Request.connector(SysNoticeFeign.class).remove(noticeIds);
    }

    public ObservableList<SysDictData> getNoticeDataObservableList() {
        return noticeDataObservableList;
    }

    public void setNoticeDataObservableList(ObservableList<SysDictData> noticeDataObservableList) {
        this.noticeDataObservableList = noticeDataObservableList;
    }

    public Map<String, SysDictData> getSysDictDataMap() {
        return sysDictDataMap;
    }

    public void setSysDictDataMap(Map<String, SysDictData> sysDictDataMap) {
        this.sysDictDataMap = sysDictDataMap;
    }
}
