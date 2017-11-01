package com.kinglloy.album.domain;

/**
 * @author jinyalin
 * @since 2017/10/31.
 */

public enum WallpaperType {
    VIDEO(0),
    LIVE(1),
    STYLE(2);

    private int typeInt;

    WallpaperType(int typeInt) {
        this.typeInt = typeInt;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public static WallpaperType fromTypeInt(int type) {
        switch (type) {
            case 0:
                return VIDEO;
            case 1:
                return LIVE;
            case 2:
                return STYLE;
            default:
                return LIVE;
        }
    }
}
