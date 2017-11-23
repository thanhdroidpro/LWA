package com.kinglloy.album.engine.video;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaper;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Android implementation of the VideoPlayer class.
 *
 * @author Rob Bogie <rob.bogie@codepoke.net>
 */
public class VideoPlayerAndroid implements VideoPlayer, OnFrameAvailableListener {

    private static final String ATTRIBUTE_TEXCOORDINATE = ShaderProgram.TEXCOORD_ATTRIBUTE + "0";
    private static final String VARYING_TEXCOORDINATE = "varTexCoordinate";
    private static final String UNIFORM_TEXTURE = "texture";
    private static final String UNIFORM_CAMERATRANSFORM = "camTransform";

    //@formatter:off
    String vertexShaderCode = "attribute highp vec4 a_position; \n" +
            "attribute highp vec2 " + ATTRIBUTE_TEXCOORDINATE + ";" +
            "uniform highp mat4 " + UNIFORM_CAMERATRANSFORM + ";" +
            "varying highp vec2 " + VARYING_TEXCOORDINATE + ";" +
            "void main() \n" +
            "{ \n" +
            " gl_Position = " + UNIFORM_CAMERATRANSFORM + " * a_position; \n" +
            " varTexCoordinate = " + ATTRIBUTE_TEXCOORDINATE + ";\n" +
            "} \n";

    String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n" +
            "uniform samplerExternalOES " + UNIFORM_TEXTURE + ";" +
            "varying highp vec2 " + VARYING_TEXCOORDINATE + ";" +
            "void main()                 \n" +
            "{                           \n" +
            "  gl_FragColor = texture2D(" + UNIFORM_TEXTURE + ", " + VARYING_TEXCOORDINATE + ");    \n" +
            "}";
    //@formatter:on

    private ShaderProgram shader;
    private int[] textures = new int[1];
    private SurfaceTexture videoTexture;

    private MediaPlayer player;
    private boolean prepared = false;
    private boolean frameAvailable = false;
    private boolean done = false;

    private Viewport viewport;
    private Camera cam;
    private Mesh mesh;

    private boolean customMesh = false;

    VideoSizeListener sizeListener;
    CompletionListener completionListener;
    private int primitiveType = GL20.GL_TRIANGLES;

    /**
     * Used for sending mediaplayer tasks to the Main Looper
     */
    private static Handler handler;

    /**
     * Lock used for waiting if the player was not yet created.
     */
    Object lock = new Object();

    public VideoPlayerAndroid () {
        this(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public VideoPlayerAndroid (Viewport viewport) {
        shader = new ShaderProgram(vertexShaderCode, fragmentShaderCode);
        setupRenderTexture();

        this.viewport = viewport;
        cam = viewport.getCamera();
        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        //@formatter:off
        mesh.setVertices(new float[] {0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0});
        //@formatter:on
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

        initializeMediaPlayer();
    }

    public VideoPlayerAndroid (Camera cam, Mesh mesh, int primitiveType) {
        this.cam = cam;
        this.mesh = mesh;
        this.primitiveType = primitiveType;
        customMesh = true;
        setupRenderTexture();

        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        if(handler == null)
            handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    player = new MediaPlayer();
                    lock.notify();
                }
            }
        });
    }

    @Override
    public boolean play (final FileHandle file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("Could not find file: " + file.path());
        }

        //Wait for the player to be created. (If the Looper thread is busy,
        if(player == null) {
            synchronized (lock) {
                while(player == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return false;
                    }
                }
            }
        }

        player.reset();
        done = false;

        player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared (MediaPlayer mp) {
                //视频填充屏幕中 by ferris.xu
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();

                float sceenWidth=Gdx.graphics.getWidth();
                float sceenHeight=Gdx.graphics.getHeight();


                float x = -sceenWidth / 2;
                float y = -sceenHeight / 2;


                float radioW =  sceenWidth /videoWidth;
                float radioH =  sceenHeight / videoHeight;
                float maxRadio = Math.max(radioW, radioH);
                float newW = videoWidth * maxRadio;
                float newH = videoHeight * maxRadio;
                float left= Math.abs(Math.abs((int) ((sceenWidth - newW) / 2)));
                float top= Math.abs(Math.abs((int) ((sceenHeight - newH) / 2)));
                //通过修改uv顶点，来裁剪图像
                float u1=left/newW;
                float v1=top/newH;
                float u2=u1+sceenWidth/newW;
                float v2=v1+sceenHeight/newH;

                //@formatter:off 0  1  2   2  3  0
                mesh.setVertices(
                        new float[] {x, y, 0,                                    u1, v2,
                                      x + sceenWidth, y, 0,                       u2, v2,
                                      x + sceenWidth, y + sceenHeight, 0,         u2, v1,
                                      x, y + sceenHeight, 0,                      u1, v1});
                //@formatter:on

                // set viewport world dimensions according to video dimensions and viewport type
                viewport.setWorldSize(sceenWidth, sceenHeight);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        // force viewport update to let scaling take effect
                        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    }
                });

                prepared = true;
                if (sizeListener != null) {
                    sizeListener.onVideoSize(sceenWidth, sceenHeight);
                }
                mp.start();
            }
        });
        player.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError (MediaPlayer mp, int what, int extra) {
                done = true;
                Log.e("VideoPlayer", String.format("Error occured: %d, %d\n", what, extra));
                return false;
            }
        });

        player.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion (MediaPlayer mp) {
                done = true;
                if (completionListener != null) {
                    completionListener.onCompletionListener(file);
                }
            }
        });

        try {
            if (file.type() == FileType.Classpath || (file.type() == FileType.Internal && !file.file().exists())) {
                AssetManager assets = ((AndroidLiveWallpaper)(Gdx.app)).getContext().getAssets();
                AssetFileDescriptor descriptor = assets.openFd(file.name());
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            } else {
                player.setDataSource(file.file().getAbsolutePath());
            }
            player.setSurface(new Surface(videoTexture));
            player.prepareAsync();
            player.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void resize (int width, int height) {
        if (!customMesh) {

            viewport.update(width, height);
        }
    }

    @Override
    public boolean render () {
        if (done) {
            return false;
        }
        if (!prepared) {
            return true;
        }
        synchronized (this) {
            if (frameAvailable) {
                videoTexture.updateTexImage();
                frameAvailable = false;
            }
        }

        // Draw texture
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        shader.begin();
        shader.setUniformMatrix(UNIFORM_CAMERATRANSFORM, cam.combined);
        mesh.render(shader, primitiveType);
        shader.end();

        return !done;
    }

    /**
     * For android, this will return whether the prepareAsync method of the android MediaPlayer is done with preparing.
     *
     * @return whether the buffer is filled.
     */
    @Override
    public boolean isBuffered () {
        return prepared;
    }

    @Override
    public void stop () {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        prepared = false;
        done = true;
    }

    private void setupRenderTexture () {
        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);

        videoTexture = new SurfaceTexture(textures[0]);
        videoTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onFrameAvailable (SurfaceTexture surfaceTexture) {
        synchronized (this) {
            frameAvailable = true;
        }
    }

    @Override
    public void pause () {
        // If it is running
        if (prepared) {
            if(player.isPlaying())
            player.pause();
        }
    }

    @Override
    public void resume () {
        // If it is running
        if (prepared) {
            if(!player.isPlaying())
            player.start();
        }
    }

    @Override
    public void dispose () {
        stop();
        if(player != null)
            player.release();

        videoTexture.detachFromGLContext();

        GLES20.glDeleteTextures(1, textures, 0);

        if (shader != null) {
            shader.dispose();
        }

        if (!customMesh && mesh != null) {
            mesh.dispose();
        }
    }

    @Override
    public void setOnVideoSizeListener (VideoPlayer.VideoSizeListener listener) {
        sizeListener = listener;
    }

    @Override
    public void setOnCompletionListener (CompletionListener listener) {
        completionListener = listener;
    }

    @Override
    public int getVideoWidth () {
        if (!prepared) {
            throw new IllegalStateException("Can't get width when video is not yet buffered!");
        }
        return player.getVideoWidth();
    }

    @Override
    public int getVideoHeight () {
        if (!prepared) {
            throw new IllegalStateException("Can't get height when video is not yet buffered!");
        }
        return player.getVideoHeight();
    }

    @Override
    public boolean isPlaying () {
        return player.isPlaying();
    }


}
