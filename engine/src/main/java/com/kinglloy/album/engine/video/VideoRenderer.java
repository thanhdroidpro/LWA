package com.kinglloy.album.engine.video;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaper;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.io.FileNotFoundException;

public class VideoRenderer extends AndroidLiveWallpaper implements ApplicationListener {

    private VideoPlayerAndroid videoPlayerAndroid;
    private String videoPath;

    VideoRenderer(AndroidLiveWallpaperService service, String videoPath) {
        super(service);
        this.videoPath = videoPath;
    }

    @Override
    public void create() {
        ShaderProgram.pedantic = false;
        videoPlayerAndroid = new VideoPlayerAndroid();
        try {
            videoPlayerAndroid.play(Gdx.files.absolute(videoPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void resize(int w, int h) {
        videoPlayerAndroid.resize(w, h);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        videoPlayerAndroid.resume();
        videoPlayerAndroid.render();
    }

    @Override
    public void pause() {
        videoPlayerAndroid.pause();
    }

    @Override
    public void resume() {
        videoPlayerAndroid.resume();
    }

    @Override
    public void dispose() {
        videoPlayerAndroid.dispose();
    }


}
