package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdUnits(
    @SerialName("appOpenAd") val appOpenAd: String = "",
    @SerialName("interstitialAdOnSplash") val interstitialAdOnSplash: String = "",
    @SerialName("rewardedAdOnDelete") val rewardedAdOnDelete: String = "",
    @SerialName("interstitialAdOnDelete") val interstitialAdOnDelete: String = "",
    @SerialName("interstitialAdOnUpdate") val interstitialAdOnUpdate: String = "",
    @SerialName("rewardedOnBirthdaySave") val rewardedOnBirthdaySave: String = "",
    @SerialName("interstitialAdOnHomeFeatures") val interstitialAdOnHomeFeatures: String = "",
    @SerialName("interstitialAdOnBack") val interstitialAdOnBack: String = "",
    @SerialName("rewardedAdOnGenerateIdea") val rewardedAdOnGenerateIdea: String = "",
    @SerialName("interstitialAdOnSettingClick") val interstitialAdOnSettingClick: String = "",
    @SerialName("rewardedAdOnCalculateClick") val rewardedAdOnCalculateClick: String = "",
    @SerialName("rewardedAdOnHistoryClick") val rewardedAdOnHistoryClick: String = ""
)

val defaultAdUnits = """
   {
   "appOpenAd": "ca-app-pub-3940256099942544/3419835294",
   "interstitialAdOnSplash": "ca-app-pub-3940256099942544/1033173712",
   "rewardedAdOnDelete": "ca-app-pub-3940256099942544/5224354917",
   "interstitialAdOnDelete": "ca-app-pub-3940256099942544/1033173712",
   "interstitialAdOnUpdate": "ca-app-pub-3940256099942544/1033173712",
   "rewardedOnBirthdaySave": "ca-app-pub-3940256099942544/5224354917",
   "rewardedAdOnGenerateIdea": "ca-app-pub-3940256099942544/5224354917",
   "interstitialAdOnBack": "ca-app-pub-3940256099942544/1033173712",
   "rewardedAdOnHistoryClick": "ca-app-pub-3940256099942544/5224354917",
    "interstitialAdOnHomeFeatures": "ca-app-pub-3940256099942544/1033173712",
    "interstitialAdOnSettingClick": "ca-app-pub-3940256099942544/1033173712",
    "rewardedAdOnCalculateClick": "ca-app-pub-3940256099942544/5224354917"
}
""".trimIndent()