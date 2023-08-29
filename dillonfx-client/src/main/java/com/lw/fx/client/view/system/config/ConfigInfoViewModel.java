package com.lw.fx.client.view.system.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysConfig;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysConfigFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public class ConfigInfoViewModel implements ViewModel {

    private ModelWrapper<SysConfig> sysConfigTypeWrapper = new ModelWrapper<>();

    public ConfigInfoViewModel() {
        setSysConfig(new SysConfig());

    }

    /**
     * 设置系统角色
     *
     * @param sysConfigType 系统作用
     */
    public void setSysConfig(SysConfig sysConfigType) {
        this.sysConfigTypeWrapper.set(sysConfigType);
        this.sysConfigTypeWrapper.reload();
    }

    public StringProperty configTypeProperty() {
        return sysConfigTypeWrapper.field("configType", SysConfig::getConfigType, SysConfig::setConfigType, "0");
    }

    public StringProperty configNameProperty() {
        return sysConfigTypeWrapper.field("configName", SysConfig::getConfigName, SysConfig::setConfigName, "0");
    }

    public StringProperty configKeyProperty() {
        return sysConfigTypeWrapper.field("configKey", SysConfig::getConfigKey, SysConfig::setConfigKey, "0");
    }

    public StringProperty cconfigValueProperty() {
        return sysConfigTypeWrapper.field("configValue", SysConfig::getConfigValue, SysConfig::setConfigValue, "0");
    }


    public LongProperty configIdProperty() {
        return sysConfigTypeWrapper.field("configId", SysConfig::getConfigId, SysConfig::setConfigId);
    }


    public StringProperty remarkProperty() {
        return sysConfigTypeWrapper.field("remark", SysConfig::getRemark, SysConfig::setRemark, "");
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        sysConfigTypeWrapper.commit();
        SysConfig sysConfigType = sysConfigTypeWrapper.get();
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysConfigFeign.class).edit(sysConfigType);

        } else {
            result = Request.connector(SysConfigFeign.class).add(sysConfigType);
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }


    public void updateSysConfigInfo(Long sysConfigTypeId) {

        //add
        if (ObjectUtil.isNull(sysConfigTypeId)) {
            setSysConfig(new SysConfig());
        } else {//edit
            ProcessChain.create().addSupplierInExecutor(() -> getSysConfig(sysConfigTypeId))
                    .addConsumerInPlatformThread(r -> setSysConfig(r)).
                    onException(e -> e.printStackTrace()).run();
        }
    }

    private SysConfig getSysConfig(Long configId) {
        JsonObject jsonObject = Request.connector(SysConfigFeign.class).getInfo(configId);
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        SysConfig sysConfigType = objects.get(AjaxResult.DATA_TAG, SysConfig.class);
        return sysConfigType;
    }


}
