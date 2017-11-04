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
import android.support.v7.widget.Toolbar
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.R
import com.kinglloy.album.data.utils.STYLE_SETTINGS_MAX_BLUR
import com.kinglloy.album.data.utils.STYLE_SETTINGS_MAX_DIM
import com.kinglloy.album.data.utils.STYLE_SETTINGS_MAX_GREY
import com.kinglloy.album.model.StyleSettingsItem
import com.kinglloy.album.presenter.SettingsPresenter
import com.kinglloy.album.view.SettingsView
import com.kinglloy.album.view.component.MySecondarySwitchDrawerItem
import com.kinglloy.album.view.component.OnProgressChangedListener
import com.kinglloy.album.view.component.SecondarySeekDrawerItem
import com.kinglloy.album.view.fragment.LiveWallpapersFragment
import com.kinglloy.album.view.fragment.StyleWallpapersFragment
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class WallpaperListActivityV2 : AppCompatActivity(), SettingsView {

    companion object {
        val titleArray: IntArray = intArrayOf(
//                R.string.video_type_title,
                R.string.live_type_title,
                R.string.style_type_title
        )

        val SP_NAME = "WallpaperListActivity"
        val SELECT_INDEX_KEY = "select_index"

        val ID_SWITCH = 1000L
        val ID_BLUR = 1001L
        val ID_DIM = 1002L
        val ID_GREY = 1003L
    }

    @Inject
    lateinit internal var presenter: SettingsPresenter

    private lateinit var viewPager: ViewPager
    private lateinit var drawer: Drawer


    private val onCheckedChangeListener = OnCheckedChangeListener { _, _, isChecked ->
        presenter.enableStyleEffect(isChecked)
        styleSettingsSeekBars.forEach {
            it.withSeekEnable(isChecked)
            drawer.updateItem(it)
        }
    }

    private val onProgressChangedListener = OnProgressChangedListener { item, _, progress ->
        when (item.identifier) {
            ID_BLUR ->
                presenter.setStyleBlur(progress)
            ID_DIM ->
                presenter.setStyleDim(progress)
            ID_GREY ->
                presenter.setStylegrey(progress)
        }
    }

    private val styleSettingsSwitch = MySecondarySwitchDrawerItem().withName("Apply Style Effect")
            .withLevel(2)
            .withChecked(true)
            .withSelectable(false)
            .withOnCheckedChangeListener(onCheckedChangeListener)

    private val styleSettingsSeekBars =
            arrayOf(
                    SecondarySeekDrawerItem().withName("Blur")
                            .withLevel(3)
                            .withMax(STYLE_SETTINGS_MAX_BLUR)
                            .withSelectable(false)
                            .withOnSeekBarChangeListener(onProgressChangedListener),
                    SecondarySeekDrawerItem().withName("Dim")
                            .withLevel(3)
                            .withMax(STYLE_SETTINGS_MAX_DIM)
                            .withSelectable(false)
                            .withOnSeekBarChangeListener(onProgressChangedListener),
                    SecondarySeekDrawerItem().withName("Grey")
                            .withLevel(3)
                            .withMax(STYLE_SETTINGS_MAX_GREY)
                            .withSelectable(false)
                            .withOnSeekBarChangeListener(onProgressChangedListener))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_list2)
        AlbumApplication.instance.applicationComponent.inject(this)

        presenter.setView(this)

        initDrawer(savedInstanceState)

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

        presenter.initialize()
    }

    override fun onDestroy() {
        super.onDestroy()
        getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putInt(SELECT_INDEX_KEY, viewPager.currentItem).apply()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        val newOutState = drawer.saveInstanceState(outState)
        newOutState.putInt(SELECT_INDEX_KEY, viewPager.currentItem)
        super.onSaveInstanceState(newOutState, outPersistentState)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    private fun initDrawer(savedInstanceState: Bundle?) {
        val toolBar = findViewById<Toolbar>(R.id.appBar)
        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolBar)
                .withHasStableIds(true)
                .withHeader(R.layout.layout_drawer_header)
                .withSelectedItem(-1)
                .addDrawerItems(
                        ExpandableBadgeDrawerItem().withName("Style Settings")
                                .withIcon(R.drawable.icon_retry).withIdentifier(1)
                                .withSelectable(false).withSubItems(
                                styleSettingsSwitch.withIdentifier(ID_SWITCH),
                                styleSettingsSeekBars[0].withIdentifier(ID_BLUR),
                                styleSettingsSeekBars[1].withIdentifier(ID_DIM),
                                styleSettingsSeekBars[2].withIdentifier(ID_GREY)
                        ))
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build()


    }


    override fun renderStyleSettings(styleSettingsItem: StyleSettingsItem) {
        styleSettingsSwitch.withChecked(styleSettingsItem.enableEffect)
        drawer.updateItem(styleSettingsSwitch)
        styleSettingsSeekBars[0].withProgress(styleSettingsItem.blur)
                .withSeekEnable(styleSettingsItem.enableEffect)
        styleSettingsSeekBars[1].withProgress(styleSettingsItem.dim)
                .withSeekEnable(styleSettingsItem.enableEffect)
        styleSettingsSeekBars[2].withProgress(styleSettingsItem.grey)
                .withSeekEnable(styleSettingsItem.enableEffect)
        styleSettingsSeekBars.forEach {
            drawer.updateItem(it)
        }
    }


    private inner class WallpaperTypesAdapter(fragmentManager: FragmentManager)
        : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment = when (position) {
//            0 -> VideoWallpapersFragment()
            0 -> LiveWallpapersFragment()
            else -> StyleWallpapersFragment()
        }

        override fun getCount() = 2

        override fun getPageTitle(position: Int): CharSequence = getString(titleArray[position])
    }
}