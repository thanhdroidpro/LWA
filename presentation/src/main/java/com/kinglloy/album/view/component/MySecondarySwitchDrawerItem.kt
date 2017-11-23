package com.kinglloy.album.view.component

import android.content.Context
import android.support.annotation.LayoutRes
import com.mikepenz.materialdrawer.R
import com.mikepenz.materialdrawer.holder.ColorHolder

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
class MySecondarySwitchDrawerItem : MyAbstractSwitchDrawerItem<MySecondarySwitchDrawerItem>() {

    override fun getType(): Int {
        return R.id.material_drawer_item_secondary_switch
    }

    @LayoutRes
    override fun getLayoutRes(): Int {
        return R.layout.material_drawer_item_secondary_switch
    }

    /**
     * helper method to decide for the correct color
     * OVERWRITE to get the correct secondary color
     *
     * @param ctx
     * @return
     */
    override fun getColor(ctx: Context): Int {
        return if (isEnabled) {
            ColorHolder.color(getTextColor(), ctx, R.attr.material_drawer_secondary_text, R.color.material_drawer_secondary_text)
        } else {
            ColorHolder.color(getDisabledTextColor(), ctx, R.attr.material_drawer_hint_text, R.color.material_drawer_hint_text)
        }
    }
}