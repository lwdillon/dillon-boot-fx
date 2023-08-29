package com.lw.fx.client.view.system.notice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysDictData;
import com.lw.fx.client.domain.SysNotice;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysNoticeFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NoticeInfoViewModel implements ViewModel {

    private ModelWrapper<SysNotice> sysDictTypeWrapper = new ModelWrapper<>();

    private ObjectProperty<SysDictData> noticeType = new SimpleObjectProperty<>();

    private ObservableList<SysDictData> noticeDataObservableList = FXCollections.observableArrayList();


    public NoticeInfoViewModel() {
        setSysNotice(new SysNotice());

    }

    /**
     * 设置系统角色
     *
     * @param sysDictType 系统作用
     */
    public void setSysNotice(SysNotice sysDictType) {
        this.sysDictTypeWrapper.set(sysDictType);
        this.sysDictTypeWrapper.reload();
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

    public StringProperty noticeTitleProperty() {
        return sysDictTypeWrapper.field("noticeTitle", SysNotice::getNoticeTitle, SysNotice::setNoticeTitle);
    }

    public StringProperty statusProperty() {
        return sysDictTypeWrapper.field("status", SysNotice::getStatus, SysNotice::setStatus, "0");
    }


    public StringProperty noticeContentProperty() {
        return sysDictTypeWrapper.field("noticeContent", SysNotice::getNoticeContent, SysNotice::setNoticeContent);
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        sysDictTypeWrapper.commit();
        SysNotice sysDictType = sysDictTypeWrapper.get();
        if (ObjectUtil.isAllNotEmpty(noticeType, noticeType.getValue())) {
            sysDictType.setNoticeType(noticeType.getValue().getDictValue());
        }
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysNoticeFeign.class).edit(sysDictType);

        } else {
            result = Request.connector(SysNoticeFeign.class).add(sysDictType);
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }


    public void updateSysNoticeInfo(Long sysDictTypeId, SysDictData sysDictData) {

        setNoticeType(sysDictData);

        //add
        if (ObjectUtil.isNull(sysDictTypeId)) {
            setSysNotice(new SysNotice());
        } else {//edit
            ProcessChain.create().addSupplierInExecutor(() -> getSysNotice(sysDictTypeId))
                    .addConsumerInPlatformThread(r -> {
                        setSysNotice(r);

                    }).
                    onException(e -> e.printStackTrace()).run();
        }
    }

    private SysNotice getSysNotice(Long noticeId) {
        JsonObject jsonObject = Request.connector(SysNoticeFeign.class).getInfo(noticeId);
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        SysNotice sysDictType = objects.get(AjaxResult.DATA_TAG, SysNotice.class);
        return sysDictType;
    }


    public ObservableList<SysDictData> getNoticeDataObservableList() {
        return noticeDataObservableList;
    }

    public void setNoticeDataObservableList(ObservableList<SysDictData> noticeDataObservableList) {
        this.noticeDataObservableList.clear();
        this.noticeDataObservableList.setAll(noticeDataObservableList);
    }

    public void commitHtmlText() {
        publish("commitHtmlText");
    }
}
