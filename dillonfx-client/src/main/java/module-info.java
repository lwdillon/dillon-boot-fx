open module com.lw.fx.client {
    requires atlantafx.base;
    requires javafx.base;
    requires java.desktop;
    requires java.prefs;
    requires javafx.swing;
    requires javafx.media;
    requires javafx.web;
    requires javafx.fxml;
    requires jdk.zipfs;
    requires com.google.gson;
    requires feign.core;
    requires feign.gson;
    requires feign.okhttp;
    requires feign.slf4j;
    requires okhttp3;
    requires datafx.core;
    requires lombok;
    requires java.sql;
    requires datafaker;
    requires org.slf4j;
    requires logback.core;
    requires logback.classic;

    requires eu.hansolo.tilesfx;
    requires eu.hansolo.fx.heatmap;
    requires eu.hansolo.toolboxfx;
    requires eu.hansolo.toolbox;
    requires eu.hansolo.fx.countries ;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.material2;
    requires org.jetbrains.annotations;

    requires org.kordamp.ikonli.core;
    requires static org.kordamp.jipsy.annotations;
    exports com.lw.fx.client.icon;

    provides org.kordamp.ikonli.IkonHandler
            with com.lw.fx.client.icon.WIconIkonHandler;
    provides org.kordamp.ikonli.IkonProvider
            with com.lw.fx.client.icon.WIconIkonProvider;

    requires fr.brouillard.oss.cssfx;
    requires org.controlsfx.controls;
    requires de.saxsys.mvvmfx;
    requires de.saxsys.mvvmfx.validation;
    requires FX.BorderlessScene;
    requires AnimateFX;
    requires com.kitfox.svg;
    requires cn.hutool;
    exports com.lw.fx.client;
}