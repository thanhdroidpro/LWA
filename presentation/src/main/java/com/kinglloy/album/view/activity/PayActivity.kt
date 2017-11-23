package com.kinglloy.album.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.kinglloy.album.R


/**
 * @author jinyalin
 * @since 2017/11/6.
 */
class PayActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private val PAY_MODE_GOOGLE = 0
        private val PAY_MODE_ALIPAY = 1

        private val PAY_MODE_KEY = "pay_mode"

        private val LOAD_PAYMENT_DATA_REQUEST_CODE = 100

        private val SDK_PAY_FLAG = 1

        fun payWithGoogle(context: Activity) {
            payWith(context, PAY_MODE_GOOGLE)
        }

        fun payWithAlipay(context: Activity) {
            payWith(context, PAY_MODE_ALIPAY)
        }

        private fun payWith(context: Activity, mode: Int) {
            val intent = Intent(context, PayActivity::class.java)
            intent.putExtra(PAY_MODE_KEY, mode)
            context.startActivity(intent)
        }
    }

    private var payMode = PAY_MODE_GOOGLE

    private var googleAvailable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payMode = intent.getIntExtra(PAY_MODE_KEY, PAY_MODE_GOOGLE)
        setContentView(R.layout.activity_pay)
        findViewById<View>(R.id.pay).setOnClickListener(this)

        googleAvailable = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.pay ->
                if (googleAvailable && payMode == PAY_MODE_GOOGLE) {

                } else {
                    alipay(0.1f)
                }
        }
    }

    private fun alipay(amount: Float) {

    }

}