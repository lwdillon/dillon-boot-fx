package com.lw.fx.client.icon;

import org.kordamp.ikonli.Ikon;

public enum WIcon implements Ikon {
    TOOL("lw-tool", '\ue911'),
    MONITOR("lw-monitor", '\ue906'),
    SYSTEM_COPY("lw-system", '\ue908'),

    MESSAGE("lw-message", '\ue90e'),

    LOG("lw-log", '\ue909'),

    EDIT("lw-edit", '\ue90c'),

    DICT("lw-dict", '\ue90b'),

    POST("lw-post", '\ue902'),

    TREE("lw-tree", '\ue90f'),

    PEOPLES("lw-peoples", '\ue90d'),

    USER("lw-user", '\ue907'),

    TREE_TABLE("lw-tree-table", '\ue900'),
    YUANDIAN("lw-form", '\ue903'),
    HOME("lw-home", '\ue916');

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
