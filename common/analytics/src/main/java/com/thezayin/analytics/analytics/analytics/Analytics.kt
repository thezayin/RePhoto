package com.thezayin.analytics.analytics

import com.thezayin.events.AnalyticsEvent

interface Analytics {
    fun logEvent(event: AnalyticsEvent)
}