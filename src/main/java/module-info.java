open module org.dillon.fx {

    requires java.sql;
    requires archaius.core;
    requires feign.core;
    requires feign.gson;
    requires feign.okhttp;
    requires okhttp3;
    requires com.google.gson;
    requires lombok;
    requires okio;
    requires annotations;
    requires datafx.core;
    requires org.kordamp.ikonli.core;
    requires javafx.controls;
    requires javafx.fxml;
    requires de.saxsys.mvvmfx;
    requires de.saxsys.mvvmfx.validation;
    requires MaterialFX;
    requires FX.BorderlessScene;
    requires fr.brouillard.oss.cssfx;

    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.material2;
    requires org.controlsfx.controls;
    requires com.kitfox.svg;
    requires AnimateFX;
    requires atlantafx.base;
    requires java.prefs;
    requires hutool.all;


    requires org.kordamp.ikonli.javafx;
    requires static org.kordamp.jipsy.annotations;

    provides org.kordamp.ikonli.IkonHandler
            with org.dillon.fx.icon.WIconIkonHandler;
    provides org.kordamp.ikonli.IkonProvider
            with org.dillon.fx.icon.WIconIkonProvider;

//    opens org.dillon.fx to javafx.fxml, de.saxsys.mvvmfx;
//    exports org.dillon.fx;

}
