package com.lw.fx.client.icon;

import org.kordamp.ikonli.AbstractIkonHandler;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.IkonHandler;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import java.io.InputStream;
import java.net.URL;

@ServiceProviderFor(IkonHandler.class)
public class WIconIkonHandler extends AbstractIkonHandler {
    private static final String FONT_RESOURCE = "/com/lw/fx/client/assets/fonts/wiconfont.ttf";

    @Override
    public boolean supports(String description) {
        return description != null && description.startsWith("lw-");
    }

    @Override
    public Ikon resolve(String description) {
        return WIcon.findByDescription(description);
    }

    @Override
    public URL getFontResource() {
        return getClass().getResource(FONT_RESOURCE);
    }

    @Override
    public InputStream getFontResourceAsStream() {
        return getClass().getResourceAsStream(FONT_RESOURCE);
    }

    @Override
    public String getFontFamily() {
        return "wiconfont";
    }
}