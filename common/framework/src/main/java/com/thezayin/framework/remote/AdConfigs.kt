package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdConfigs(
    @SerialName("appOpenAd") val appOpenAd: Boolean = false,
    @SerialName("interstitialAdOnSplash") val interstitialAdOnSplash: Boolean = false,
    @SerialName("rewardedAdOnDelete") val rewardedAdOnDelete: Boolean = false,
    @SerialName("interstitialAdOnUpdate") val interstitialAdOnUpdate: Boolean = false,
    @SerialName("interstitialAdOnDelete") val interstitialAdOnDelete: Boolean = false,
    @SerialName("rewardedOnBirthdaySave") val rewardedOnBirthdaySave: Boolean = false,
    @SerialName("rewardedAdOnHistoryClick") val rewardedAdOnHistoryClick: Boolean = false,
    @SerialName("adOnLoading") val adOnLoading: Boolean = false,
    @SerialName("rewardedAdOnCalculateClick") val rewardedAdOnCalculateClick: Boolean = false,
    @SerialName("interstitialAdOnSettingClick") val interstitialAdOnSettingClick: Boolean = false,
    @SerialName("interstitialAdOnHomeFeatures") val interstitialAdOnHomeFeatures: Boolean = false,
    @SerialName("interstitialAdOnBack") val interstitialAdOnBack: Boolean = false,
    @SerialName("rewardedAdOnGenerateIdea") val rewardedAdOnGenerateIdea: Boolean = false,
)

val defaultAdConfigs = """
   {
   "appOpenAd": false,
   "rewardedAdOnGenerateIdea": false,
   "interstitialAdOnBack": false,
   "interstitialAdOnHomeFeatures": false,
   "interstitialAdOnSettingClick": false,
   "rewardedAdOnCalculateClick": false,
   "adOnLoading": false,
   "rewardedAdOnHistoryClick": false,
   "rewardedOnBirthdaySave": false,
   "interstitialAdOnDelete": false,
   "interstitialAdOnUpdate": false,
   "rewardedAdOnDelete": false,
   "interstitialAdOnSplash": false
}
""".trimIndent()