package org.dillon.fx.view.window;

import animatefx.animation.BounceInRight;
import cn.hutool.json.JSONObject;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.web.domain.AjaxResult;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.notifications.WeakNotificationObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.Message;
import org.dillon.fx.view.loginregister.LoginRegisterView;
import org.dillon.fx.view.loginregister.LoginRegisterViewModel;
import org.dillon.fx.view.main.MainView;
import org.dillon.fx.view.main.MainViewModel;
import org.dillon.fx.view.main.Overlay;
import org.dillon.fx.view.system.DeptManageView;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowView implements FxmlView<WindowViewModel>, Initializable {

    @InjectViewModel
    private WindowViewModel windowViewModel;

    @FXML
    private VBox messagePane;

    @FXML
    private StackPane rootPane;
    @FXML
    private StackPane contentPane;

    private Overlay overlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        rootPane.getChildren().add(0, overlay = new Overlay());
        MvvmFX.getNotificationCenter().subscribe("showMainView", (key, payload) -> {
            // trigger some actions
            showMainView();

        });
        MvvmFX.getNotificationCenter().subscribe("showLoginRegister", (key, payload) -> {
            // trigger some actions
            showLoginView();

        });

        MvvmFX.getNotificationCenter().subscribe("message", (key, payload) -> {
            // trigger some actions
            showMessage((Integer) payload[0], (String) payload[1]);

        });

        showLoginView();

    }

    private void showMainView() {

        contentPane.getChildren().clear();
        ViewTuple<MainView, MainViewModel> load = FluentViewLoader.fxmlView(MainView.class).load();
        contentPane.getChildren().add(load.getView());
        windowViewModel.mainViewVisbleProperty().setValue(true);

    }

    private void showLoginView() {
        contentPane.getChildren().clear();
        ViewTuple<LoginRegisterView, LoginRegisterViewModel> viewTuple = FluentViewLoader.fxmlView(LoginRegisterView.class).load();
        contentPane.getChildren().add(viewTuple.getView());
        windowViewModel.mainViewVisbleProperty().setValue(false);

    }

    private void showMessage(int code, String msg) {

        if (windowViewModel.isMainViewVisble()) {
            Platform.runLater(() -> {
                Message message=null;
                if (code == HttpStatus.SUCCESS) {
                    message = new Message(Message.Type.SUCCESS, null, msg);

                }else {
                    message= new Message(Message.Type.DANGER, null, msg);

                }
                messagePane.getChildren().add(message);
                message.handleOpen();
            });
        }


    }
}
