package com.lw.fx.client.icon;

import org.kordamp.ikonli.IkonProvider;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

@ServiceProviderFor(IkonProvider.class)
public class WIconIkonProvider implements IkonProvider<WIcon> {
    @Override
    public Class<WIcon> getIkon() {
        return WIcon.class;
    }
}