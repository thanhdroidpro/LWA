package com.kinglloy.album.engine.video;

import android.content.Context;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.yalin.style.engine.GDXWallpaperServiceProxy;

import org.jetbrains.annotations.NotNull;

/**
 * @author jinyalin
 * @since 2017/11/8.
 */

public class VideoWallpaper extends GDXWallpaperServiceProxy {
    private String filePath;

    public VideoWallpaper(@NotNull Context host, String filePath) {
        super(host);
        this.filePath = filePath;
    }

    @Override
    public void onCreateApplication() {
        super.onCreateApplication();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useGLSurfaceView20API18 = true;
        config.useAccelerometer = false;
        initialize(new VideoRenderer(this, filePath), config);
    }
}
