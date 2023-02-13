/* SPDX-License-Identifier: MIT */
package org.dillon.fx.theme;

import javafx.scene.layout.Region;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public final class CSSFragment {

    private static final String DATA_URI_PREFIX = "data:base64,";

    private final String css;

    public CSSFragment(String css) {
        this.css = Objects.requireNonNull(css);
    }

    public void addTo(Region region) {
        Objects.requireNonNull(region);
        region.getStylesheets().add(toDataURI());
    }

    public void removeFrom(Region region) {
        Objects.requireNonNull(region);
        region.getStylesheets().remove(toDataURI());
    }

    public boolean existsIn(Region region) {
        Objects.requireNonNull(region);
        return region.getStylesheets().contains(toDataURI());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSSFragment cssFragment = (CSSFragment) o;
        return css.equals(cssFragment.css);
    }

    @Override
    public int hashCode() {
        return Objects.hash(css);
    }

    @Override
    public String toString() {
        return css;
    }

    public String toDataURI() {
        return DATA_URI_PREFIX + new String(Base64.getEncoder().encode(css.getBytes()), StandardCharsets.UTF_8);
    }
}
