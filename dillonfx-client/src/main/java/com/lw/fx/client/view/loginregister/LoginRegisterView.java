package com.lw.fx.client.view.loginregister;

import animatefx.animation.*;
import animatefx.util.ParallelAnimationFX;
import atlantafx.base.theme.Styles;
import com.kitfox.svg.app.beans.SVGIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static com.lw.fx.client.view.loginregister.LoginRegisterViewModel.ON_VIEW_ADDEDA;

public class LoginRegisterView implements FxmlView<LoginRegisterViewModel>, Initializable {

    @InjectViewModel
    private LoginRegisterViewModel loginRegisterViewModel;

    @FXML
    private StackPane rootPane;
    @FXML
    private HBox loginPane;
    @FXML
    private HBox registerPane;

    @FXML
    private Hyperlink loginLink;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private ImageView logoImageView;
    @FXML
    private ImageView registerImageView;
    @FXML
    private Button loginBut;

    @FXML
    private TextField usrNameTextField;

    @FXML
    private TextField vefCodeTextField;

    @FXML
    private PasswordField pwdTextField;
    @FXML
    private Button closeBut;

    @FXML
    private ImageView codeImeageView;

    @FXML
    private ProgressBar bar;

    @FXML
    private Label msg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeBut.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.DANGER);
        closeBut.setOnAction(event -> MvvmFX.getNotificationCenter().publish("exit"));
        SVGIcon svgIcon = new SVGIcon();
        SVGIcon svgIcon1 = new SVGIcon();

        try {
            svgIcon.setSvgURI(LoginRegisterView.class.getResource("/images/dd.svg").toURI());
            svgIcon1.setSvgURI(LoginRegisterView.class.getResource("/images/bb.svg").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        msg.pseudoClassStateChanged(STATE_DANGER, true);
        loginRegisterViewModel.getCodeCommand().execute();
        codeImeageView.imageProperty().bind(loginRegisterViewModel.codeImageProperty());
        logoImageView.setImage(SwingFXUtils.toFXImage((BufferedImage) svgIcon.getImage(), null));
        registerImageView.setImage(SwingFXUtils.toFXImage((BufferedImage) svgIcon1.getImage(), null));
        codeImeageView.setOnMouseClicked(event -> loginRegisterViewModel.getCodeCommand().execute());
        loginBut.setOnAction(event -> {
            loginRegisterViewModel.getLoginCommand().execute();
//
        });
        loginRegisterViewModel.successProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                BounceOutUp bounceOutDown = new BounceOutUp(loginPane);
                bounceOutDown.setOnFinished(event1 -> MvvmFX.getNotificationCenter().publish("showMainView", "显示主界面"));
                new ParallelAnimationFX(bounceOutDown, new BounceOutUp(registerPane)).play();
            }
        });
        bar.visibleProperty().bind(loginRegisterViewModel.getLoginCommand().runningProperty());
        bar.managedProperty().bind(bar.visibleProperty());
        bar.progressProperty().bind(Bindings.createDoubleBinding(
                () -> loginRegisterViewModel.getLoginCommand().isRunning() ? -1d : 0d, loginRegisterViewModel.getLoginCommand().runningProperty())
        );
        usrNameTextField.textProperty().bindBidirectional(loginRegisterViewModel.userNameProperty());
        pwdTextField.textProperty().bindBidirectional(loginRegisterViewModel.passWordProperty());
        vefCodeTextField.textProperty().bindBidirectional(loginRegisterViewModel.vefCodeProperty());
        loginBut.disableProperty().bind(loginRegisterViewModel.getFormValidator().validProperty().not());
        loginBut.textProperty().bind(Bindings.createStringBinding(
                () -> loginRegisterViewModel.getLoginCommand().isRunning() ? "正在登录..." : "登 录", loginRegisterViewModel.getLoginCommand().runningProperty())
        );
        msg.visibleProperty().bind(loginRegisterViewModel.msgProperty().isNotEmpty());
        msg.managedProperty().bind(msg.visibleProperty());

        msg.textProperty().bind(loginRegisterViewModel.msgProperty());

        Rectangle innerBounds = new Rectangle();
        innerBounds.widthProperty().bind(Bindings.createObjectBinding(() -> rootPane.getLayoutBounds().getWidth(), rootPane.layoutBoundsProperty()));
        innerBounds.heightProperty().bind(Bindings.createObjectBinding(() -> rootPane.getLayoutBounds().getHeight(), rootPane.layoutBoundsProperty()));
        innerBounds.setArcWidth(20);
        innerBounds.setArcHeight(20);
        rootPane.setClip(innerBounds);


        loginRegisterViewModel.subscribe(ON_VIEW_ADDEDA, (s, objects) -> {
            new ParallelAnimationFX(new BounceInUp(loginPane), new BounceInUp(registerPane)).play();
        });
        registerLink.setOnAction(event -> {

            RotateOutDownRight rotateOutDownRight = new RotateOutDownRight(loginPane);
            rotateOutDownRight.setResetOnFinished(true);
            RotateInDownRight rotateInDownRight = new RotateInDownRight(registerPane);
            rotateInDownRight.setResetOnFinished(true);
            rotateInDownRight.setOnFinished(event1 -> loginPane.setVisible(false));
            ParallelAnimationFX parallelAnimationFX
                    = new ParallelAnimationFX(rotateOutDownRight, rotateInDownRight);
            registerPane.setVisible(true);
            parallelAnimationFX.play();

        });

        loginLink.setOnAction(event -> {

            RotateOutUpRight rotateOutDownRight = new RotateOutUpRight(registerPane);
            rotateOutDownRight.setResetOnFinished(true);
            RotateInUpRight rotateInDownRight = new RotateInUpRight(loginPane);
            rotateInDownRight.setResetOnFinished(true);
            rotateInDownRight.setOnFinished(event1 -> registerPane.setVisible(false));
            ParallelAnimationFX parallelAnimationFX
                    = new ParallelAnimationFX(rotateOutDownRight, rotateInDownRight);
            loginPane.setVisible(true);
            parallelAnimationFX.play();
        });


    }
}
