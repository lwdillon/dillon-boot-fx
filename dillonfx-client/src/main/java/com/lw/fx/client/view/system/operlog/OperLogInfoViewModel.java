package com.lw.fx.client.view.system.operlog;

import com.lw.fx.client.domain.SysOperLog;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class OperLogInfoViewModel implements ViewModel {

    private ModelWrapper<SysOperLog> sysDictWrapper = new ModelWrapper<>();

    public OperLogInfoViewModel() {

    }

    /**
     * 设置系统角色
     *
     * @param sysDict 系统作用
     */
    public void setSysOperLog(SysOperLog sysDict) {
        this.sysDictWrapper.set(sysDict);
        this.sysDictWrapper.reload();
    }

    public StringProperty titleProperty() {
        return sysDictWrapper.field("title", SysOperLog::getTitle, SysOperLog::setTitle);
    }

    public StringProperty operNameProperty() {
        return sysDictWrapper.field("operName", SysOperLog::getOperName, SysOperLog::setOperName);
    }

    public StringProperty operIpProperty() {
        return sysDictWrapper.field("operIp", SysOperLog::getOperIp, SysOperLog::setOperIp);
    }

    public StringProperty operUrlProperty() {
        return sysDictWrapper.field("operUrl", SysOperLog::getOperUrl, SysOperLog::setOperUrl);
    }

    public StringProperty requestMethodProperty() {
        return sysDictWrapper.field("requestMethod", SysOperLog::getRequestMethod, SysOperLog::setRequestMethod);
    }

    public StringProperty methodProperty() {
        return sysDictWrapper.field("method", SysOperLog::getMethod, SysOperLog::setMethod);
    }

    public StringProperty operParamProperty() {
        return sysDictWrapper.field("operParam", SysOperLog::getOperParam, SysOperLog::setOperParam);
    }

    public StringProperty jsonResultProperty() {
        return sysDictWrapper.field("jsonResult", SysOperLog::getJsonResult, SysOperLog::setJsonResult);
    }

    public StringProperty errorMsgProperty() {
        return sysDictWrapper.field("errorMsg", SysOperLog::getErrorMsg, SysOperLog::setErrorMsg);
    }

    public ObjectProperty<Date> operTimeProperty() {
        return sysDictWrapper.field("operTime", SysOperLog::getOperTime, SysOperLog::setOperTime);
    }

    public LongProperty costTimeProperty() {
        return sysDictWrapper.field("costTime", SysOperLog::getCostTime, SysOperLog::setCostTime);
    }

    public IntegerProperty statusProperty() {
        return sysDictWrapper.field("status", SysOperLog::getStatus, SysOperLog::setStatus);
    }


}
