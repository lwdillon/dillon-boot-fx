package com.lw.fx.client.view.system.post;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.lw.fx.client.domain.SysPost;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysPostFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostViewModel implements ViewModel {

    private ObservableList<SysPost> sysPosts = FXCollections.observableArrayList();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty postName = new SimpleStringProperty();
    private StringProperty postCode = new SimpleStringProperty();

    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    public PostViewModel() {
        queryPostList();
    }

    public void queryPostList() {


        Map<String, Object> querMap = new HashMap<>();
        querMap.put("postName", postName.getValue());
        querMap.put("postCode", postCode.getValue());
        querMap.put("status", status.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysPosts.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysPostFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysPost> posts = BeanUtil.copyToList(r.getRows(), SysPost.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysPosts.addAll(posts);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void reset() {
        postCode.setValue("");
        postName.setValue("");
        status.setValue("");
        endDate.setValue(null);
        startDate.setValue(null);
    }

    public ObservableList<SysPost> getSysPosts() {
        return sysPosts;
    }


    public void setSysPosts(ObservableList<SysPost> sysPosts) {
        this.sysPosts = sysPosts;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getPostName() {
        return postName.get();
    }

    public StringProperty postNameProperty() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName.set(postName);
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

    public void del(String postIds) {
        Request.connector(SysPostFeign.class).remove(postIds);
    }

    public String getPostCode() {
        return postCode.get();
    }

    public StringProperty postCodeProperty() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode.set(postCode);
    }
}
