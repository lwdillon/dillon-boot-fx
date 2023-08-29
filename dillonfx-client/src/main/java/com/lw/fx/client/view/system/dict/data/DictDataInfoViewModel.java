package com.lw.fx.client.view.system.dict.data;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysDictData;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDictDataFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public class DictDataInfoViewModel implements ViewModel {

    private ModelWrapper<SysDictData> sysDictWrapper = new ModelWrapper<>();

    public DictDataInfoViewModel() {
        setSysDictData(new SysDictData());

    }

    /**
     * 设置系统角色
     *
     * @param sysDict 系统作用
     */
    public void setSysDictData(SysDictData sysDict) {
        this.sysDictWrapper.set(sysDict);
        this.sysDictWrapper.reload();
    }

    public StringProperty dictTypeProperty() {
        return sysDictWrapper.field("dictType", SysDictData::getDictType, SysDictData::setDictType, "0");
    }

    public StringProperty dictValueProperty() {
        return sysDictWrapper.field("dictValue", SysDictData::getDictValue, SysDictData::setDictValue, "0");
    }

    public StringProperty listClassProperty() {
        return sysDictWrapper.field("listClass", SysDictData::getListClass, SysDictData::setListClass, "default");
    }

    public StringProperty cssClassProperty() {
        return sysDictWrapper.field("cssClass", SysDictData::getCssClass, SysDictData::setCssClass, "0");
    }


    public LongProperty dictSortProperty() {
        return sysDictWrapper.field("dictSort", SysDictData::getDictSort, SysDictData::setDictSort, 0);
    }

    public StringProperty statusProperty() {
        return sysDictWrapper.field("status", SysDictData::getStatus, SysDictData::setStatus, "0");
    }

    public StringProperty dictLabelProperty() {
        return sysDictWrapper.field("dictLabel", SysDictData::getDictLabel, SysDictData::setDictLabel, "");
    }

    public StringProperty remarkProperty() {
        return sysDictWrapper.field("remark", SysDictData::getRemark, SysDictData::setRemark, "");
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        sysDictWrapper.commit();
        SysDictData sysDict = sysDictWrapper.get();
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysDictDataFeign.class).edit(sysDict);

        } else {
            result = Request.connector(SysDictDataFeign.class).add(sysDict);
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }


    public void updateSysDictDataInfo(Long sysDictId, String dictType) {

        //add
        if (ObjectUtil.isNull(sysDictId)) {
            SysDictData sysDictData = new SysDictData();
            sysDictData.setDictType(dictType);
            setSysDictData(sysDictData);
        } else {//edit
            ProcessChain.create().addSupplierInExecutor(() -> getSysDictData(sysDictId))
                    .addConsumerInPlatformThread(r -> setSysDictData(r)).
                    onException(e -> e.printStackTrace()).run();
        }
    }

    private SysDictData getSysDictData(Long dictId) {
        JsonObject jsonObject = Request.connector(SysDictDataFeign.class).getInfo(dictId);
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        SysDictData sysDict = objects.get(AjaxResult.DATA_TAG, SysDictData.class);
        return sysDict;
    }


}
