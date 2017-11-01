package com.kinglloy.album.domain;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class Wallpaper {
    public long id;
    public String wallpaperId;
    public String link;
    public String name;
    public String author;
    public String iconUrl;
    public String downloadUrl;

    public boolean lazyDownload;

    public String providerName;

    public String storePath;

    public boolean isDefault;
    public boolean isSelected;

    public WallpaperType wallpaperType;
}
