package com.kinglloy.album.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.R
import com.kinglloy.album.view.activity.WallpaperListActivity

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class VideoWallpapersFragment : BaseWallpapersFragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_video_wallpapers_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AlbumApplication.instance.applicationComponent.inject(this)

        presenter.setView(this)

        if (savedInstanceState != null) {
            loadState = savedInstanceState.getInt(WallpaperListActivity.LOAD_STATE)
            presenter.onRestoreInstanceState(savedInstanceState)
        }

        handleState()
    }
}