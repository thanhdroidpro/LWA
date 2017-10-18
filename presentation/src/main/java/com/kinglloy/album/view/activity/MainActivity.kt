package com.kinglloy.album.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kinglloy.album.R
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*



/**
 * @author jinyalin
 * @since 2017/10/11.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        val showSelectKey = "show_select"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!shouldShowSelect()) {
            startActivity(Intent(this@MainActivity, WallpaperListActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        watchAD.setOnClickListener {
            Analytics.logEvent(this, Event.OPEN_AD_ACTIVITY)
            startActivity(Intent(this@MainActivity, ADActivity::class.java))
        }
        wallpaperList.setOnClickListener {
            maybeUpdatePreference()
            startActivity(Intent(this@MainActivity, WallpaperListActivity::class.java))
            if (checkBox.isChecked) {
                Analytics.logEvent(this, Event.TODAY_NOT_SHOW)
                finish()
            }
        }

        attachAD()
    }

    private fun shouldShowSelect(): Boolean {
        val lastShowSelect = defaultSharedPreferences.getInt(showSelectKey, 0)
        return getFormatDate() != lastShowSelect
    }

    private fun maybeUpdatePreference() {
        if (checkBox.isChecked) {
            val currentDate = getFormatDate()
            defaultSharedPreferences.edit().putInt(showSelectKey, currentDate).apply()
        }
    }

    private fun getFormatDate(): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_YEAR)
        return year * 1000 + day
    }

    private fun attachAD() {
        MobileAds.initialize(applicationContext,
                getString(R.string.app_ad_id))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}