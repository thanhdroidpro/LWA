package com.kinglloy.album.view.activity

import android.os.Bundle
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

    val titleArray: IntArray = intArrayOf(
            R.string.video_type_title,
            R.string.live_type_title,
            R.string.style_type_title
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_list2)

        val tabLayout = findViewById<TabLayout>(R.id.wallpaper_types_tab)
        val viewPager = findViewById<ViewPager>(R.id.wallpaper_types)

        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = WallpaperTypesAdapter(supportFragmentManager)
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