/* SPDX-License-Identifier: MIT */
package org.dillon.fx.view.system.menu;

import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.utils.notifications.WeakNotificationObserver;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.vo.SysMenu;

import static org.dillon.fx.view.system.menu.MenuDialogViewModel.ON_CLOSE;

public class MenuManagerDialog extends OverlayDialog {


    public MenuManagerDialog(ViewTuple<MenuDialogView, MenuDialogViewModel> viewTuple, SysMenu sysMenu, boolean isEdit) {
        MenuDialogViewModel menuDialogViewModel = viewTuple.getViewModel();
        menuDialogViewModel.setSysMenu(isEdit?sysMenu:new SysMenu());
        menuDialogViewModel.setSelectSysMenu(sysMenu);
        menuDialogViewModel.subscribe(ON_CLOSE, new WeakNotificationObserver((key, payload) -> {
            close();
        }));
        menuDialogViewModel.getMenuListCommand().execute();
        setId("theme-repo-manager-dialog");
        setContent(viewTuple.getView());

        var saveBtn = new Button("保存");
        var addBtn = new Button("添加");
        addBtn.setVisible(!isEdit);
        addBtn.managedProperty().bind(addBtn.visibleProperty());
        saveBtn.visibleProperty().bind(addBtn.visibleProperty().not());
        saveBtn.managedProperty().bind(saveBtn.visibleProperty());
        addBtn.setOnAction(event -> menuDialogViewModel.getAddCommand().execute());
        saveBtn.setOnAction(event -> menuDialogViewModel.getEdtCommand().execute());


        footerBox.getChildren().addAll(addBtn, saveBtn);
        footerBox.setAlignment(Pos.CENTER_RIGHT);
    }

}
