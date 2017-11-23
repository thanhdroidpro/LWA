package com.kinglloy.album.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import com.kinglloy.album.BuildConfig
import com.kinglloy.album.R
import kotlinx.android.synthetic.main.activity_ad.*
import kotlinx.android.synthetic.main.layout_include_about_content.*

/**
 * @author jinyalin
 * @since 2017/11/6.
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBar.setNavigationOnClickListener {
            onBackPressed()
        }

        appVersion.text = Html.fromHtml(
                getString(R.string.about_version_template, BuildConfig.VERSION_NAME))

        aboutBody.text = Html.fromHtml(getString(R.string.about_body))
        aboutBody.movementMethod = LinkMovementMethod()
    }
}