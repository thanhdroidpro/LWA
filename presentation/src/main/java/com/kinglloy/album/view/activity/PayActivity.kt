package com.kinglloy.album.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.kinglloy.album.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest


/**
 * @author jinyalin
 * @since 2017/11/6.
 */
class PayActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private val PAY_MODE_GOOGLE = 0
        private val PAY_MODE_ALIPAY = 1

        private val PAY_MODE_KEY = "pay_mode"

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

    private lateinit var mPaymentsClient: PaymentsClient
    private var payMode = PAY_MODE_GOOGLE

    private var googleAvailable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payMode = intent.getIntExtra(PAY_MODE_KEY, PAY_MODE_GOOGLE)
        setContentView(R.layout.activity_pay)
        findViewById<View>(R.id.pay).setOnClickListener(this)

        googleAvailable = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS

        if (googleAvailable && payMode == PAY_MODE_GOOGLE) {
            mPaymentsClient = Wallet.getPaymentsClient(this,
                    Wallet.WalletOptions.Builder()
                            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                            .build())
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.pay ->
                isReadyToPay()
        }
    }

    private fun isReadyToPay() {
        val request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build()
        val task = mPaymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { t ->
            try {
                val result = t.getResult(ApiException::class.java)
                if (result == true) {
                    // Show Google as payment option.
                } else {
                    // Hide Google as payment option.
                }
            } catch (exception: ApiException) {
            }
        }
    }
}