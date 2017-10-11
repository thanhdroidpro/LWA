package com.kinglloy.album.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.kinglloy.album.R
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import kotlinx.android.synthetic.main.activity_ad.*

/**
 * @author jinyalin
 * @since 2017/10/11.
 */
class ADActivity : AppCompatActivity() {
    var mAd: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        btnRetry.setOnClickListener { attachAD() }
        btnReview.setOnClickListener {
            Analytics.logEvent(this, Event.WATCH_AD_AGAIN)
            attachAD()
        }

        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBar.setNavigationOnClickListener {
            onBackPressed()
        }

        attachAD()
    }


    private fun attachAD() {
        MobileAds.initialize(applicationContext,
                getString(R.string.app_ad_id))

        content.visibility = View.GONE
        loading.visibility = View.VISIBLE
        retry.visibility = View.GONE

        mAd = MobileAds.getRewardedVideoAdInstance(this)
        mAd!!.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
                content.visibility = View.VISIBLE
                loading.visibility = View.GONE
                retry.visibility = View.GONE

                mAd?.destroy(this@ADActivity)
            }

            override fun onRewardedVideoAdLeftApplication() {

            }

            override fun onRewardedVideoAdLoaded() {
                onRewardedVideoAdClosed()
                mAd?.show()
            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewarded(p0: RewardItem?) {
                onRewardedVideoAdClosed()
            }

            override fun onRewardedVideoStarted() {

            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                Analytics.logEvent(this@ADActivity, Event.LOAD_AD_FAILED, p0.toString())

                content.visibility = View.GONE
                loading.visibility = View.GONE
                retry.visibility = View.VISIBLE
            }
        }
        mAd!!.loadAd(getString(R.string.advance_video_ad_unit_id), AdRequest.Builder().build())
    }

    override fun onResume() {
        mAd?.resume(this)
        super.onResume()
    }

    override fun onPause() {
        mAd?.pause(this)
        super.onPause()
    }

    override fun onDestroy() {
        mAd?.destroy(this)
        super.onDestroy()
    }
}