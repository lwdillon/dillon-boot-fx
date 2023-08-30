package com.lw.fx.client.view.system.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.*;
import com.lw.fx.client.domain.vo.TreeSelect;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysUserFeign;
import com.lw.fx.client.view.control.Tag;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.ContentDisplay;

import java.util.List;
import java.util.stream.Collectors;

public class UserInfoViewModel implements ViewModel {

    private Command infoCommand;

    private Long userId = null;
    private ObservableList<SysRole> roles = FXCollections.observableArrayList();
    private ObservableList<SysPost> posts = FXCollections.observableArrayList();

    private ObservableList<Tag> selPosts = FXCollections.observableArrayList();
    private ObservableList<Tag> selRoles = FXCollections.observableArrayList();

    private SimpleObjectProperty<TreeSelect> selDept = new SimpleObjectProperty<>(new TreeSelect());

    private ObservableMap<Long, SysPost> selPostMap = FXCollections.observableHashMap();
    private ObservableMap<Long, SysRole> selRoleMap = FXCollections.observableHashMap();
    private List<Long> postIds = CollUtil.newArrayList();
    private List<Long> roleIds = CollUtil.newArrayList();

    private ObservableList<TreeSelect> deptTreeList = FXCollections.observableArrayList();
    /**
     * 包装器
     */
    private ModelWrapper<SysUser> wrapper = new ModelWrapper<>();

    public void initialize() {
        infoCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                getUserInfo();
            }
        }, true); //Async
        setSysUser(new SysUser());


        selPostMap.addListener(new MapChangeListener<Long, SysPost>() {
            @Override
            public void onChanged(Change<? extends Long, ? extends SysPost> change) {
                selPosts.setAll(selPostMap.values().stream().map(posts -> {
                    Tag tag = new Tag(posts.getPostName());
                    tag.setContentDisplay(ContentDisplay.RIGHT);
                    return tag;
                }).collect(Collectors.toList()));
            }
        });
        selRoleMap.addListener(new MapChangeListener<Long, SysRole>() {
            @Override
            public void onChanged(Change<? extends Long, ? extends SysRole> change) {
                selRoles.setAll(selRoleMap.values().stream().map(role -> {
                    Tag tag = new Tag(role.getRoleName());
                    tag.setContentDisplay(ContentDisplay.RIGHT);
                    return tag;
                }).collect(Collectors.toList()));
            }
        });

    }

    public ObservableList<TreeSelect> getDeptTreeList() {
        return deptTreeList;
    }


    public TreeSelect getSelDept() {
        return selDept.get();
    }

    public SimpleObjectProperty<TreeSelect> selDeptProperty() {
        return selDept;
    }

    public void setSelDept(TreeSelect selDept) {
        this.selDept.set(selDept);
    }

    public ObservableList<Tag> getSelPosts() {
        return selPosts;
    }

    public ObservableList<Tag> getSelRoles() {
        return selRoles;
    }

    public List<Long> getPostIds() {
        return postIds;
    }

    public void setSysUser(SysUser sysUser) {
        wrapper.set(sysUser);
        wrapper.reload();
    }

    public StringProperty userNameProperty() {
        return wrapper.field("userName", SysUser::getUserName, SysUser::setUserName, "");
    }


    public StringProperty nickNameProperty() {
        return wrapper.field("nickName", SysUser::getNickName, SysUser::setNickName, "");
    }

    public StringProperty emailProperty() {
        return wrapper.field("email", SysUser::getEmail, SysUser::setEmail, "");
    }

    public StringProperty phonenumberProperty() {
        return wrapper.field("phonenumber", SysUser::getPhonenumber, SysUser::setPhonenumber, "");
    }

    public StringProperty passwordProperty() {
        return wrapper.field("password", SysUser::getPassword, SysUser::setPassword, "");
    }

    public StringProperty remarkProperty() {
        return wrapper.field("remark", SysUser::getRemark, SysUser::setRemark, "");
    }

    public StringProperty sexProperty() {
        return wrapper.field("sex", SysUser::getSex, SysUser::setSex, "");
    }

    public StringProperty statusProperty() {
        return wrapper.field("status", SysUser::getStatus, SysUser::setStatus, "");
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }


    public void updateInfo(Long userId) {
        this.userId = userId;
        infoCommand.execute();
    }

    public ObservableMap<Long, SysRole> getSelRoleMap() {
        return selRoleMap;
    }

    public ObservableMap<Long, SysPost> getSelPostMap() {
        return selPostMap;
    }

    public ObservableList<SysRole> getRoles() {
        return roles;
    }

    public ObservableList<SysPost> getPosts() {
        return posts;
    }

    private void getUserInfo() {
        JsonObject jsonObject = Request.connector(SysUserFeign.class).getInfo(userId);

        Platform.runLater(() -> {
            roles.clear();
            posts.clear();
        });
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        List<SysRole> rs = JSONUtil.toList(objects.getJSONArray("roles"), SysRole.class);
        List<SysPost> ps = JSONUtil.toList(objects.getJSONArray("posts"), SysPost.class);

        Platform.runLater(() -> {
            posts.addAll(ps);
            roles.addAll(rs);

        });


        if (ObjectUtil.isNotNull(userId)) {
            Platform.runLater(() -> {
                SysUser sysUser = objects.get(AjaxResult.DATA_TAG, SysUser.class);
                setSysUser(sysUser);
                postIds = JSONUtil.toList(objects.getJSONArray("postIds"), Long.class);
                roleIds = JSONUtil.toList(objects.getJSONArray("roleIds"), Long.class);
                posts.forEach(post -> {
                    if (postIds.contains(post.getPostId())) {
                        post.setSelect(true);
                        selPostMap.put(post.getPostId(), post);
                    }
                });
                roles.forEach(role -> {
                    if (roleIds.contains(role.getRoleId())) {
                        role.setSelect(true);
                        selRoleMap.put(role.getRoleId(), role);
                    }
                });

                setSelDept(new TreeSelect(ObjectUtil.defaultIfNull(sysUser.getDept(), new SysDept())));

            });
        }
    }


    public boolean save(boolean isEdit) {

        wrapper.get().setDeptId(selDept.getValue().getId());
        wrapper.get().setRoleIds(ArrayUtil.toArray(selRoleMap.keySet(), Long.class));
        wrapper.get().setPostIds(ArrayUtil.toArray(selPostMap.keySet(), Long.class));
        wrapper.commit();
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysUserFeign.class).edit(wrapper.get());
        } else {
            result = Request.connector(SysUserFeign.class).add(wrapper.get());
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }
}
