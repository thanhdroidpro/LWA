package com.kinglloy.album.view.component;

import android.widget.SeekBar;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * @author jinyalin
 * @since 2017/11/4.
 */

public interface OnProgressChangedListener {
    void onProgressChanged(IDrawerItem drawerItem, SeekBar seekBar, int progress);
}
