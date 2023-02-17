package org.dillon.fx.icon;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;

public enum WIcon implements Ikon {
    TOOL("lw-tool", '\ue900'),
    MONITOR("lw-monitor", '\ue901'),
    SYSTEM_COPY("lw-system", '\ue902'),

    MESSAGE("lw-message", '\ue903'),

    LOG("lw-log", '\ue904'),

    EDIT("lw-edit", '\ue905'),

    DICT("lw-dict", '\ue906'),

    POST("lw-post", '\ue907'),

    TREE("lw-tree", '\ue908'),

    PEOPLES("lw-peoples", '\ue909'),

    USER("lw-user", '\ue90a'),

    TREE_TABLE("lw-tree-table", '\ue90b'),
    YUANDIAN("lw-yuandian", '\ue90c'),
    HOME("lw-home", '\ue90d');

    public static WIcon findByDescription(String description) {
        for (WIcon font : values()) {
            if (font.getDescription().equals(description)) {
                return font;
            }
        }
        return YUANDIAN;
//        throw new IllegalArgumentException("Icon description '" + description + "' is invalid!");
    }

    private String description;
    private int code;

    WIcon(String description, int code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getCode() {
        return code;
    }
}
