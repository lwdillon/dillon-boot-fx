package org.dillon.fx.view.config;

import cn.hutool.core.date.DateUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;

/**
 * 快速配置菜单视图
 *
 * @author liwen
 * @date 2022/09/16
 */
public class UserInfoView implements FxmlView<UserInfoViewModel>, Initializable {

    @InjectViewModel
    private UserInfoViewModel userInfoViewModel;

    @FXML
    private VBox root;
    @FXML
    private Button iconBut;

    @FXML
    private Button loginOutBut;

    @FXML
    private Label userName;

    @FXML
    private Label phonenumber;
    @FXML
    private Label email;
    @FXML
    private Label dept;
    @FXML
    private Label roles;

    @FXML
    private Label createDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        userName.textProperty().bind(userInfoViewModel.userNameProperty());
        phonenumber.textProperty().bind(userInfoViewModel.phonenumberProperty());
        email.textProperty().bind(userInfoViewModel.emailProperty());
        roles.textProperty().bind(userInfoViewModel.rolesProperty());
        dept.textProperty().bind(Bindings.createStringBinding(
                () -> userInfoViewModel.deptProperty().get().getLeader()+"/"+userInfoViewModel.deptProperty().get().getDeptName(), userInfoViewModel.deptProperty())
        );
        createDate.textProperty().bind(Bindings.createStringBinding(
                () -> DateUtil.format((Date) userInfoViewModel.createTimeProperty().getValue(),"yyyy-MM-dd HH:mm:ss"), userInfoViewModel.createTimeProperty())
        );

        iconBut.getStylesheets().addAll(BUTTON_CIRCLE, ACCENT);
        iconBut.setId("userBut");
        iconBut.setShape(new Circle(120));
        loginOutBut.setOnAction(event -> {

            MvvmFX.getNotificationCenter().publish("showLoginRegister", "显示登录界面");
        });

    }
}
