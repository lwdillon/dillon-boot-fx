package com.lw.fx.client.view.system.dict.type;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysDictType;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDictTypeFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.StringProperty;

public class DictTypeInfoViewModel implements ViewModel {

    private ModelWrapper<SysDictType> sysDictTypeWrapper = new ModelWrapper<>();

    public DictTypeInfoViewModel() {
        setSysDictType(new SysDictType());

    }

    /**
     * 设置系统角色
     *
     * @param sysDictType 系统作用
     */
    public void setSysDictType(SysDictType sysDictType) {
        this.sysDictTypeWrapper.set(sysDictType);
        this.sysDictTypeWrapper.reload();
    }

    public StringProperty dictTypeProperty() {
        return sysDictTypeWrapper.field("dictType", SysDictType::getDictType, SysDictType::setDictType, "0");
    }

    public StringProperty dictNameProperty() {
        return sysDictTypeWrapper.field("dictName", SysDictType::getDictName, SysDictType::setDictName, "0");
    }


    public StringProperty statusProperty() {
        return sysDictTypeWrapper.field("status", SysDictType::getStatus, SysDictType::setStatus, "0");
    }


    public StringProperty remarkProperty() {
        return sysDictTypeWrapper.field("remark", SysDictType::getRemark, SysDictType::setRemark, "");
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        sysDictTypeWrapper.commit();
        SysDictType sysDictType = sysDictTypeWrapper.get();
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysDictTypeFeign.class).edit(sysDictType);

        } else {
            result = Request.connector(SysDictTypeFeign.class).add(sysDictType);
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }


    public void updateSysDictTypeInfo(Long sysDictTypeId) {

        //add
        if (ObjectUtil.isNull(sysDictTypeId)) {
            setSysDictType(new SysDictType());
        } else {//edit
            ProcessChain.create().addSupplierInExecutor(() -> getSysDictType(sysDictTypeId))
                    .addConsumerInPlatformThread(r -> setSysDictType(r)).
                    onException(e -> e.printStackTrace()).run();
        }
    }

    private SysDictType getSysDictType(Long dictId) {
        JsonObject jsonObject = Request.connector(SysDictTypeFeign.class).getInfo(dictId);
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        SysDictType sysDictType = objects.get(AjaxResult.DATA_TAG, SysDictType.class);
        return sysDictType;
    }


}
