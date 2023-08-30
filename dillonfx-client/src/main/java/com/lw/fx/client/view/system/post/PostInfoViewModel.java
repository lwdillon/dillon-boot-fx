package com.lw.fx.client.view.system.post;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysPost;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysPostFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class PostInfoViewModel implements ViewModel {

    private ModelWrapper<SysPost> sysPostWrapper = new ModelWrapper<>();


    public PostInfoViewModel() {
        setSysPost(new SysPost());

    }

    /**
     * 设置系统角色
     *
     * @param sysPost 系统作用
     */
    public void setSysPost(SysPost sysPost) {
        this.sysPostWrapper.set(sysPost);
        this.sysPostWrapper.reload();
    }


    public IntegerProperty postSortProperty() {
        return sysPostWrapper.field("postSort", SysPost::getPostSort, SysPost::setPostSort, 0);
    }

    public StringProperty postCodeProperty() {
        return sysPostWrapper.field("postCode", SysPost::getPostCode, SysPost::setPostCode, "");
    }

    public StringProperty statusProperty() {
        return sysPostWrapper.field("status", SysPost::getStatus, SysPost::setStatus, "0");
    }

    public StringProperty postNameProperty() {
        return sysPostWrapper.field("postName", SysPost::getPostName, SysPost::setPostName, "");
    }

    public StringProperty remarkProperty() {
        return sysPostWrapper.field("remark", SysPost::getRemark, SysPost::setRemark, "");
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        sysPostWrapper.commit();
        SysPost sysPost = sysPostWrapper.get();
        List<Long> menuIs = new ArrayList<>();
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysPostFeign.class).edit(sysPost);

        } else {
            result = Request.connector(SysPostFeign.class).add(sysPost);
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }


    public void updateSysPostInfo(Long sysPostId) {

        //add
        if (ObjectUtil.isNull(sysPostId)) {
            setSysPost(new SysPost());
        } else {//edit
            ProcessChain.create().addSupplierInExecutor(() -> getSysPost(sysPostId))
                    .addConsumerInPlatformThread(r -> setSysPost(r))
                    .onException(e -> e.printStackTrace()).run();
        }
    }

    private SysPost getSysPost(Long postId) {
        JsonObject jsonObject = Request.connector(SysPostFeign.class).getInfo(postId);
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        SysPost sysPost = objects.get(AjaxResult.DATA_TAG, SysPost.class);
        return sysPost;
    }


}
