package com.kinglloy.album.view.component

import android.support.annotation.LayoutRes
import android.view.View
import android.widget.SeekBar
import com.kinglloy.album.R
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem
import com.mikepenz.materialdrawer.model.BaseViewHolder

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
abstract class AbstractSeekDrawerItem<Item : AbstractSeekDrawerItem<Item>>
    : BaseDescribeableDrawerItem<Item, AbstractSeekDrawerItem.ViewHolder>(),
        SeekBar.OnSeekBarChangeListener {

    private var seekEnable: Boolean = true
    private var maxProgress: Int = 100
    private var progressChangedListener: OnProgressChangedListener? = null

    private var currentProgress: Int = 0

    fun withProgress(progress: Int): Item {
        currentProgress = progress
        return this as Item
    }

    fun withSeekEnable(enable: Boolean): Item {
        seekEnable = enable
        return this as Item
    }

    fun withMax(max: Int): Item {
        maxProgress = max
        return this as Item
    }

    fun withOnSeekBarChangeListener(progressChangedListener: OnProgressChangedListener)
            : Item {
        this.progressChangedListener = progressChangedListener
        return this as Item
    }


    fun getProgress(): Int {
        return currentProgress
    }

    fun isSeekEnable(): Boolean {
        return seekEnable
    }

    override fun getType(): Int {
        return R.id.material_drawer_item_primary_seekbar
    }

    @LayoutRes
    override fun getLayoutRes(): Int {
        return R.layout.material_drawer_item_secondary_seekbar
    }

    override fun bindView(viewHolder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(viewHolder, payloads)
        bindViewHelper(viewHolder)

        viewHolder.seekBar.setOnSeekBarChangeListener(null)
        viewHolder.seekBar.max = maxProgress
        viewHolder.seekBar.progress = currentProgress
        viewHolder.seekBar.isEnabled = seekEnable
        viewHolder.seekBar.setOnSeekBarChangeListener(this)

        withOnDrawerItemClickListener { _, _, _ ->
            true
        }

        onPostBindView(this, viewHolder.itemView)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder constructor(view: View) : BaseViewHolder(view) {
        val seekBar: SeekBar = view.findViewById<View>(R.id.material_drawer_seekbar) as SeekBar

    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        currentProgress = progress
        progressChangedListener?.onProgressChanged(this, seekBar, progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}


    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}