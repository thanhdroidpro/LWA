package com.kinglloy.album.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kinglloy.album.data.repository.datasource.provider.settings.SettingsContract;
import com.kinglloy.album.data.repository.datasource.provider.settings.SettingsContract.StyleWallpaperSettingsColumns;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kinglloy.album.data.test", appContext.getPackageName());
    }

    @Test
    public void testGetStyleWallpaperSettings() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Uri uri = SettingsContract.StyleWallpaperSettings.Companion.getCONTENT_URI();
        Cursor cursor =
                appContext.getContentResolver().query(uri, null,
                        null, null, null);

        assert cursor != null;
        try {
            assert cursor.getColumnCount() == 4;
            assert cursor.getColumnIndex(StyleWallpaperSettingsColumns
                    .Companion.getCOLUMN_NAME_ENABLE_EFFECT()) == 0;
            assert cursor.getColumnIndex(StyleWallpaperSettingsColumns
                    .Companion.getCOLUMN_NAME_BLUR()) == 1;
            assert cursor.getColumnIndex(StyleWallpaperSettingsColumns
                    .Companion.getCOLUMN_NAME_DIM()) == 2;
            assert cursor.getColumnIndex(StyleWallpaperSettingsColumns
                    .Companion.getCOLUMN_NAME_GREY()) == 3;
        } finally {
            cursor.close();
        }
    }
}
