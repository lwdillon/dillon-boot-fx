package org.dillon.fx.view.loginregister;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.validation.*;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import org.dillon.fx.domain.AjaxResult;
import org.dillon.fx.domain.R;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.LoginFeign;
import org.dillon.fx.store.AppStore;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录注册视图模型
 *
 * @author liwen
 * @date 2022/09/25
 */
public class LoginRegisterViewModel implements ViewModel, SceneLifecycle {


    public final static String ON_VIEW_ADDEDA = "onViewAdded";
    private Command codeCommand;
    private Command loginCommand;

    private SimpleStringProperty userName = new SimpleStringProperty("admin");
    private SimpleStringProperty passWord = new SimpleStringProperty("admin123");
    private SimpleStringProperty uuId = new SimpleStringProperty();
    private SimpleStringProperty vefCode = new SimpleStringProperty("ss");
    private SimpleBooleanProperty success = new SimpleBooleanProperty(false);

    private SimpleObjectProperty<Image> codeImage = new SimpleObjectProperty<Image>();

    private FunctionBasedValidator userNameValidator;
    private ObservableRuleBasedValidator passwordValidator;
    private ObservableRuleBasedValidator vefCodeValidator;
    private CompositeValidator formValidator;
    private SimpleStringProperty msg = new SimpleStringProperty();

    public void initialize() {

        codeCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                getCode();
            }
        }, true); //Async
        loginCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                login();
            }
        }, true); //Async

        userNameValidator = new FunctionBasedValidator(
                userNameProperty(),
                input -> input != null && !input.toString().trim().isEmpty(),
                ValidationMessage.error("请输入用户名"));

        passwordValidator = new ObservableRuleBasedValidator();
        passwordValidator.addRule(passWordProperty().isNotEmpty(), ValidationMessage.error("请输入用户密码"));

        vefCodeValidator = new ObservableRuleBasedValidator();
        vefCodeValidator.addRule(vefCodeProperty().isNotEmpty(), ValidationMessage.error("请输入验证码"));

        formValidator = new CompositeValidator();
        formValidator.addValidators(userNameValidator, passwordValidator, vefCodeValidator, new ObservableRuleBasedValidator(loginCommand.runningProperty().not(), ValidationMessage.error("正在登录...")));

    }

    @Override
    public void onViewAdded() {

        publish(ON_VIEW_ADDEDA);
    }

    @Override
    public void onViewRemoved() {
        System.err.println("--------");
    }


    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getPassWord() {
        return passWord.get();
    }

    public SimpleStringProperty passWordProperty() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord.set(passWord);
    }

    public String getUuId() {
        return uuId.get();
    }

    public SimpleStringProperty uuIdProperty() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId.set(uuId);
    }

    public String getVefCode() {
        return vefCode.get();
    }

    public SimpleStringProperty vefCodeProperty() {
        return vefCode;
    }

    public void setVefCode(String vefCode) {
        this.vefCode.set(vefCode);
    }

    public String getMsg() {
        return msg.get();
    }

    public SimpleStringProperty msgProperty() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg.set(msg);
    }

    public boolean isSuccess() {
        return success.get();
    }

    public SimpleBooleanProperty successProperty() {
        return success;
    }

    public ValidationStatus getUserNameValidator() {
        return userNameValidator.getValidationStatus();
    }

    public ValidationStatus getPasswordValidator() {
        return passwordValidator.getValidationStatus();
    }

    public ValidationStatus getVefCodeValidator() {
        return vefCodeValidator.getValidationStatus();
    }

    public ValidationStatus getFormValidator() {
        return formValidator.getValidationStatus();
    }

    public Image getCodeImage() {
        return codeImage.get();
    }

    public SimpleObjectProperty<Image> codeImageProperty() {
        return codeImage;
    }

    public void setCodeImage(Image codeImage) {
        this.codeImage.set(codeImage);
    }

    public Command getCodeCommand() {
        return codeCommand;
    }

    public Command getLoginCommand() {
        return loginCommand;
    }

    private void getCode() {


        LoginFeign loginFeign = Request.connector(LoginFeign.class);
        JsonObject ajaxResult = loginFeign.getCode();
        String img = ajaxResult.get("img").getAsString();
        uuId.setValue(ajaxResult.get("uuid").getAsString());
        setCodeImage(SwingFXUtils.toFXImage(ImgUtil.toImage(img), null));
    }


    /**
     * 登录
     */
    private void login() {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("username", userName.getValue());
        queryMap.put("password", passWord.getValue());
        queryMap.put("code", vefCode.getValue());
        queryMap.put("uuid", uuId.getValue());

        LoginFeign loginFeign = Request.connector(LoginFeign.class);

        JsonObject result = loginFeign.login(userName.getValue(), passWord.getValue(), vefCode.getValue(), uuId.getValue());
        JSONObject objects = JSONUtil.parseObj(result.toString());

        Platform.runLater(() -> {
            msg.setValue(objects.getStr(AjaxResult.MSG_TAG));
        });

        if (objects.getInt(AjaxResult.CODE_TAG) == 200) {
            AppStore.setToken(objects.getStr("token"));

            success.setValue(true);

        } else {
            getCode();
        }
    }

}
