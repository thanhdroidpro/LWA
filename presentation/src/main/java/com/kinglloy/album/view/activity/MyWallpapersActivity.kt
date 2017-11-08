package com.kinglloy.album.view.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.kinglloy.album.R
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.view.MyWallpapersView
import kotlinx.android.synthetic.main.activity_my_wallpapers.*

/**
 * YaLin
 * On 2017/11/8.
 */
class MyWallpapersActivity : AppCompatActivity(), MyWallpapersView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wallpapers)

        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBar.setNavigationOnClickListener {
            onBackPressed()
        }

        setupMultiSelect()
    }

    private fun tryUpdateSelection(animation: Boolean) {
        val duration =
                if (animation)
                    resources.getInteger(android.R.integer.config_shortAnimTime)
                else 0

        if (!selectionVisible) {
            selectionToolbarContainer.visibility = View.VISIBLE
            selectionToolbarContainer.translationY =
                    (-selectionToolbarContainer.height).toFloat()
            selectionToolbarContainer.animate()
                    .translationY(0f)
                    .setDuration(duration.toLong())
                    .withEndAction(null)
        } else {

        }
        selectionVisible = !selectionVisible
    }

    var selectionVisible = false
    private fun setupMultiSelect() {
        // Set up toolbar
        selectionToolbar.setNavigationOnClickListener {
            selectionVisible = true
            tryUpdateSelection(true)
        }
        selectionToolbar.inflateMenu(R.menu.menu_my_wallpapers_edit)
        selectionToolbar.setOnMenuItemClickListener {
            Toast.makeText(this@MyWallpapersActivity, "Delete...", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_wallpapers, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                tryUpdateSelection(true)
            }
        }
        return true
    }

    override fun renderWallpapers(wallpapers: List<WallpaperItem>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun selectWallpaper(wallpaper: WallpaperItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEmpty() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun complete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wallpaperSelected(wallpaperId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteWallpapers(wallpapers: List<WallpaperItem>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRetry() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideRetry() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun context(): Context {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}