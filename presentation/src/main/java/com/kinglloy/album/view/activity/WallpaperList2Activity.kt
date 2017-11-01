package com.kinglloy.album.view.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.kinglloy.album.R
import com.kinglloy.album.view.fragment.LiveWallpapersFragment
import com.kinglloy.album.view.fragment.StyleWallpapersFragment
import com.kinglloy.album.view.fragment.VideoWallpapersFragment

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class WallpaperList2Activity : AppCompatActivity() {

    companion object {
        val titleArray: IntArray = intArrayOf(
                R.string.video_type_title,
                R.string.live_type_title,
                R.string.style_type_title
        )

        val SP_NAME = "WallpaperListActivity"
        val SELECT_INDEX_KEY = "select_index"

    }

    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_list2)

        val tabLayout = findViewById<TabLayout>(R.id.wallpaper_types_tab)
        viewPager = findViewById(R.id.wallpaper_types)

        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = WallpaperTypesAdapter(supportFragmentManager)
        if (savedInstanceState == null) {
            viewPager.currentItem = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                    .getInt(SELECT_INDEX_KEY, 0)
        } else {
            viewPager.currentItem = savedInstanceState.getInt(SELECT_INDEX_KEY, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putInt(SELECT_INDEX_KEY, viewPager.currentItem).apply()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        outState.putInt(SELECT_INDEX_KEY, viewPager.currentItem)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private inner class WallpaperTypesAdapter(fragmentManager: FragmentManager)
        : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> VideoWallpapersFragment()
            1 -> LiveWallpapersFragment()
            else -> StyleWallpapersFragment()
        }

        override fun getCount() = 3

        override fun getPageTitle(position: Int): CharSequence = getString(titleArray[position])
    }
}