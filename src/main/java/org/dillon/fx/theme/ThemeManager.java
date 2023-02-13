/* SPDX-License-Identifier: MIT */
package org.dillon.fx.theme;

import atlantafx.base.theme.*;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.dillon.fx.Resources;
import org.dillon.fx.event.DefaultEventBus;
import org.dillon.fx.event.EventBus;
import org.dillon.fx.event.ThemeEvent;
import org.dillon.fx.utils.JColor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.dillon.fx.Resources.getResource;

public final class ThemeManager {

    static final String DUMMY_STYLESHEET = getResource("styles/empty.css").toString();
    static final String[] APP_STYLESHEETS = new String[] {
            Resources.resolve("styles/index.css")
    };
    static final Set<Class<? extends Theme>> PROJECT_THEMES = Set.of(
            PrimerLight.class, PrimerDark.class, NordLight.class, NordDark.class
    );

    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass USER_CUSTOM = PseudoClass.getPseudoClass("user-custom");
    private static final EventBus EVENT_BUS = DefaultEventBus.getInstance();

    public static final String DEFAULT_FONT_FAMILY_NAME = "Inter";
    public static final int DEFAULT_FONT_SIZE = 14;
    public static final int DEFAULT_ZOOM = 100;
    public static final AccentColor DEFAULT_ACCENT_COLOR = null;
    public static final List<Integer> SUPPORTED_FONT_SIZE = IntStream.range(8, 29).boxed().collect(Collectors.toList());
    public static final List<Integer> SUPPORTED_ZOOM = List.of(50, 75, 80, 90, 100, 110, 125, 150, 175, 200);

    private final Map<String, String> customCSSDeclarations = new LinkedHashMap<>(); // -fx-property | value;
    private final Map<String, String> customCSSRules = new LinkedHashMap<>(); // .foo | -fx-property: value;

    private final ThemeRepository repository = new ThemeRepository();

    private Scene scene;

    private SamplerTheme currentTheme = null;
    private String fontFamily = DEFAULT_FONT_FAMILY_NAME;
    private int fontSize = DEFAULT_FONT_SIZE;
    private int zoom = DEFAULT_ZOOM;
    private AccentColor accentColor = DEFAULT_ACCENT_COLOR;

    public ThemeRepository getRepository() {
        return repository;
    }

    public Scene getScene() {
        return scene;
    }

    // MUST BE SET ON STARTUP
    // (this is supposed to be a constructor arg, but since app don't use DI..., sorry)
    public void setScene(Scene scene) {
        this.scene = Objects.requireNonNull(scene);
    }

    public SamplerTheme getTheme() {
        return currentTheme;
    }

    public SamplerTheme getDefaultTheme() {
        return getRepository().getAll().get(3);
    }

    /** @see SamplerTheme */
    public void setTheme(SamplerTheme theme) {
        Objects.requireNonNull(theme);

        Application.setUserAgentStylesheet(Objects.requireNonNull(theme.getUserAgentStylesheet()));
        getScene().getStylesheets().setAll(theme.getAllStylesheets());
        getScene().getRoot().pseudoClassStateChanged(DARK, theme.isDarkMode());

        // remove user CSS customizations and reset accent on theme change
        resetAccentColor();
        resetCustomCSS();

        currentTheme = theme;
        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.THEME_CHANGE));
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        Objects.requireNonNull(fontFamily);
        setCustomDeclaration("-fx-font-family", "\"" + fontFamily + "\"");

        this.fontFamily = fontFamily;

        reloadCustomCSS();
        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.FONT_CHANGE));
    }

    public boolean isDefaultFontFamily() {
        return Objects.equals(DEFAULT_FONT_FAMILY_NAME, getFontFamily());
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int size) {
        if (!SUPPORTED_FONT_SIZE.contains(size)) {
            throw new IllegalArgumentException(
                    String.format("Font size must in the range %d-%dpx. Actual value is %d.",
                                  SUPPORTED_FONT_SIZE.get(0),
                                  SUPPORTED_FONT_SIZE.get(SUPPORTED_FONT_SIZE.size() - 1),
                                  size
                    ));
        }

        setCustomDeclaration("-fx-font-size", size + "px");
        setCustomRule(".ikonli-font-icon", String.format("-fx-icon-size: %dpx;", size + 2));

        this.fontSize = size;

        var rawZoom = (int) Math.ceil((size * 1.0 / DEFAULT_FONT_SIZE) * 100);
        this.zoom = SUPPORTED_ZOOM.stream()
                .min(Comparator.comparingInt(i -> Math.abs(i - rawZoom)))
                .orElseThrow(NoSuchElementException::new);

        reloadCustomCSS();
        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.FONT_CHANGE));
    }

    public boolean isDefaultSize() {
        return DEFAULT_FONT_SIZE == fontSize;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        if (!SUPPORTED_ZOOM.contains(zoom)) {
            throw new IllegalArgumentException(
                    String.format("Zoom value must one of %s. Actual value is %d.", SUPPORTED_ZOOM, zoom)
            );
        }

        setFontSize((int) Math.ceil(zoom != 100 ? (DEFAULT_FONT_SIZE * zoom) / 100.0f : DEFAULT_FONT_SIZE));
        this.zoom = zoom;
    }

    public AccentColor getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(AccentColor color) {
        Objects.requireNonNull(color);

        if (accentColor != null) {
            getScene().getRoot().pseudoClassStateChanged(accentColor.pseudoClass(), false);
        }

        getScene().getRoot().pseudoClassStateChanged(color.pseudoClass(), true);
        this.accentColor = color;

        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.COLOR_CHANGE));
    }

    public void resetAccentColor() {
        if (accentColor != null) {
            getScene().getRoot().pseudoClassStateChanged(accentColor.pseudoClass(), false);
            accentColor = null;
        }

        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.COLOR_CHANGE));
    }

    public void setNamedColors(Map<String, Color> colors) {
        Objects.requireNonNull(colors).forEach(this::setOrRemoveColor);
        reloadCustomCSS();
        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.COLOR_CHANGE));
    }

    public void unsetNamedColors(String... colors) {
        for (String c : colors) {
            setOrRemoveColor(c, null);
        }
        reloadCustomCSS();
        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.COLOR_CHANGE));
    }

    public void resetAllChanges() {
        resetCustomCSS();
        EVENT_BUS.publish(new ThemeEvent(ThemeEvent.EventType.THEME_CHANGE));
    }

    public HighlightJSTheme getMatchingSourceCodeHighlightTheme(Theme theme) {
        Objects.requireNonNull(theme);
        if ("Nord Light".equals(theme.getName())) { return HighlightJSTheme.nordLight(); }
        if ("Nord Dark".equals(theme.getName())) { return HighlightJSTheme.nordDark(); }
        return theme.isDarkMode() ? HighlightJSTheme.githubDark() : HighlightJSTheme.githubLight();
    }

    ///////////////////////////////////////////////////////////////////////////

    private void setCustomDeclaration(String property, String value) {
        customCSSDeclarations.put(property, value);
    }

    private void removeCustomDeclaration(String property) {
        customCSSDeclarations.remove(property);
    }

    private void setCustomRule(String selector, String rule) {
        customCSSRules.put(selector, rule);
    }

    private void removeCustomRule(String selector) {
        customCSSRules.remove(selector);
    }

    private void setOrRemoveColor(String colorName, Color color) {
        Objects.requireNonNull(colorName);
        if (color != null) {
            setCustomDeclaration(colorName, JColor.color(
                    (float) color.getRed(),
                    (float) color.getGreen(),
                    (float) color.getBlue(),
                    (float) color.getOpacity()).getColorHexWithAlpha()
            );
        } else {
            removeCustomDeclaration(colorName);
        }
    }

    private void reloadCustomCSS() {
        Objects.requireNonNull(scene);
        StringBuilder css = new StringBuilder();

        css.append(".root:");
        css.append(USER_CUSTOM.getPseudoClassName());
        css.append(" {\n");
        customCSSDeclarations.forEach((k, v) -> {
            css.append("\t");
            css.append(k);
            css.append(":\s");
            css.append(v);
            css.append(";\n");
        });
        css.append("}\n");

        customCSSRules.forEach((k, v) -> {
            // custom CSS is applied to the body,
            // thus it has a preference over accent color
            css.append(".body:");
            css.append(USER_CUSTOM.getPseudoClassName());
            css.append(" ");
            css.append(k);
            css.append(" {");
            css.append(v);
            css.append("}\n");
        });

        getScene().getRoot().getStylesheets().removeIf(uri -> uri.startsWith("data:text/css"));
        getScene().getRoot().getStylesheets().add(
                "data:text/css;base64," + Base64.getEncoder().encodeToString(css.toString().getBytes(UTF_8))
        );
        getScene().getRoot().pseudoClassStateChanged(USER_CUSTOM, true);
    }

    public void resetCustomCSS() {
        customCSSDeclarations.clear();
        customCSSRules.clear();
        getScene().getRoot().pseudoClassStateChanged(USER_CUSTOM, false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Singleton                                                             //
    ///////////////////////////////////////////////////////////////////////////

    private ThemeManager() { }

    private static class InstanceHolder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }

    public static ThemeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
