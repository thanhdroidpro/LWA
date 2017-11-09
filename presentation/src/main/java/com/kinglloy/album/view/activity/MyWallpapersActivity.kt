package com.kinglloy.album.view.activity

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.R
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.presenter.MyWallpapersPresenter
import com.kinglloy.album.view.MyWallpapersView
import kotlinx.android.synthetic.main.activity_my_wallpapers.*
import java.util.ArrayList
import javax.inject.Inject

/**
 * YaLin
 * On 2017/11/8.
 */
class MyWallpapersActivity : AppCompatActivity(), MyWallpapersView {

    private lateinit var wallpaperList: RecyclerView
    private lateinit var loadingView: View
    private lateinit var emptyView: View

    private lateinit var proBackground: Drawable
    private lateinit var normalBackground: Drawable

    @Inject
    lateinit internal var presenter: MyWallpapersPresenter

    private var placeHolderDrawable: ColorDrawable? = null
    private var mItemSize = 10

    val wallpapers = ArrayList<WallpaperItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wallpapers)
        AlbumApplication.instance.applicationComponent.inject(this)
        presenter.setView(this)

        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBar.setNavigationOnClickListener {
            onBackPressed()
        }

        setupMultiSelect()

        initView()
        presenter.initialize()
    }

    private fun initView() {
        wallpaperList = findViewById(R.id.wallpaperList)
        loadingView = findViewById(R.id.loading)
        emptyView = findViewById(android.R.id.empty)

        proBackground = ContextCompat.getDrawable(this, R.drawable.pro_wallpaper_gradient)!!
        normalBackground = ColorDrawable(ContextCompat.getColor(this, R.color.color_name_bg))

        val itemAnimator = DefaultItemAnimator()
        itemAnimator.supportsChangeAnimations = false
        wallpaperList.itemAnimator = itemAnimator

        val gridLayoutManager = GridLayoutManager(this, 1)
        wallpaperList.layoutManager = gridLayoutManager

        wallpaperList.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val width = wallpaperList.width -
                                wallpaperList.paddingStart - wallpaperList.paddingEnd
                        if (width <= 0) {
                            return
                        }

                        // Compute number of columns
                        val maxItemWidth = resources.getDimensionPixelSize(
                                R.dimen.advance_grid_max_item_size)
                        var numColumns = 1
                        while (true) {
                            if (width / numColumns > maxItemWidth) {
                                ++numColumns
                            } else {
                                break
                            }
                        }

                        val spacing = resources.getDimensionPixelSize(
                                R.dimen.gallery_chosen_photo_grid_spacing)
                        mItemSize = (width - spacing * (numColumns - 1)) / numColumns

                        // Complete setup
                        gridLayoutManager.spanCount = numColumns
                        wallpaperList.adapter = wallpapersAdapter

                        wallpaperList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })

        ViewCompat.setOnApplyWindowInsetsListener(wallpaperList) { v, insets ->
            val gridSpacing = resources
                    .getDimensionPixelSize(R.dimen.gallery_chosen_photo_grid_spacing)
            ViewCompat.onApplyWindowInsets(v, insets.replaceSystemWindowInsets(
                    insets.systemWindowInsetLeft + gridSpacing,
                    gridSpacing,
                    insets.systemWindowInsetRight + gridSpacing,
                    insets.systemWindowInsetBottom + insets.systemWindowInsetTop + gridSpacing))

            insets
        }

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

    private var selectionVisible = false
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

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun renderWallpapers(wallpapers: List<WallpaperItem>) {
        this.wallpapers.clear()
        this.wallpapers.addAll(wallpapers)

        if (wallpaperList.visibility == View.VISIBLE) {
            return
        }
        wallpaperList.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.GONE
        wallpapersAdapter.notifyDataSetChanged()
    }

    override fun selectWallpaper(wallpaper: WallpaperItem) {
        var oldSelectedIndex = -1
        var newSelectedIndex = -1
        for ((index, value) in wallpapers.withIndex()) {
            if (value.isSelected) {
                value.isSelected = false
                oldSelectedIndex = index
            }
            if (value.wallpaperType == wallpaper.wallpaperType
                    && TextUtils.equals(value.wallpaperId, wallpaper.wallpaperId)) {
                value.isSelected = true
                newSelectedIndex = index
            }
        }

        if (oldSelectedIndex == newSelectedIndex) {
            return
        }
        if (oldSelectedIndex >= 0 && newSelectedIndex >= 0) {
            wallpapersAdapter.notifyItemChanged(oldSelectedIndex)
            wallpapersAdapter.notifyItemChanged(newSelectedIndex)
        } else if (oldSelectedIndex >= 0) {
            wallpapersAdapter.notifyItemChanged(oldSelectedIndex)
        } else {
            wallpapersAdapter.notifyItemChanged(newSelectedIndex)
        }
    }

    override fun showLoading() {
        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
    }

    override fun showEmpty() {
        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    override fun complete() {
        finish()
    }

    override fun hideLoading() {

    }

    override fun deleteWallpapers(wallpapers: List<WallpaperItem>) {

    }

    override fun showRetry() {

    }

    override fun hideRetry() {

    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun context() = applicationContext!!

    class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkedOverlayView: View = itemView.findViewById(R.id.checked_overlay)
        private var thumbnailView: View = itemView.findViewById(R.id.thumbnail)
        var thumbnail: ImageView = thumbnailView as ImageView
        private var nameView: View = itemView.findViewById(R.id.tvName)
        var tvName: TextView = nameView as TextView

        var icPro: View = itemView.findViewById(R.id.icon_pro)
    }

    private val wallpapersAdapter = object : RecyclerView.Adapter<WallpaperViewHolder>() {
        override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
            val item = wallpapers[position]
            holder.thumbnail.layoutParams.width = mItemSize
            holder.thumbnail.layoutParams.height = mItemSize
            Glide.with(this@MyWallpapersActivity)
                    .load(item.iconUrl)
                    .override(mItemSize, mItemSize)
                    .placeholder(placeHolderDrawable)
                    .into(holder.thumbnail)

            if (item.isSelected) {
                holder.checkedOverlayView.visibility = View.VISIBLE
            } else {
                holder.checkedOverlayView.visibility = View.GONE
            }

            holder.tvName.background = if (item.pro) proBackground else normalBackground
            holder.icPro.visibility = if (item.pro) View.VISIBLE else View.GONE
            holder.tvName.text = item.name
        }

        override fun getItemCount(): Int = wallpapers.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WallpaperViewHolder {
            val view = LayoutInflater.from(this@MyWallpapersActivity)
                    .inflate(R.layout.advance_chosen_wallpaper_item, parent, false)

            val vh = WallpaperViewHolder(view)
            view.setOnClickListener {
                val item = wallpapers[vh.adapterPosition]
                presenter.previewWallpaper(item)
            }

            return vh
        }

        override fun getItemId(position: Int): Long = wallpapers[position].id
    }
}