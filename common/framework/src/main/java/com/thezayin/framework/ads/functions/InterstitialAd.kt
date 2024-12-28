package com.thezayin.framework.ads.functions

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.thezayin.analytics.analytics.Analytics
import com.thezayin.framework.ads.loader.GoogleInterstitialAdLoader

fun Activity.interstitialAd(
    analytics: Analytics,
    showAd: Boolean,
    adUnitId: String,
    showLoading: () -> Unit,
    hideLoading: () -> Unit,
    callback: () -> Unit

) = runCatching {
    if (!showAd) return@runCatching callback()
    showLoading()
    GoogleInterstitialAdLoader.loadAd(
        context = this@interstitialAd,
        analytics = analytics,
        adUnitId = adUnitId,
        onAdLoading = showLoading,
        onAdLoaded = { interstitialAd ->
            hideLoading()
            interstitialAd.fullScreenContentCallback = AdmobInterListener(callback)
            interstitialAd.show(this@interstitialAd)
        },
        onAdFailed = {
            hideLoading()
            callback()
        }
    )
}.onFailure {
    hideLoading()
    callback()
}

internal class AdmobInterListener(
    private val callback: () -> Unit,
) : FullScreenContentCallback() {
    private var clicks = 0

    override fun onAdClicked() {
        super.onAdClicked()
        clicks++
    }

    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        callback.invoke()
    }

    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        super.onAdFailedToShowFullScreenContent(adError)
        callback.invoke()
    }
}