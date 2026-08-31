package com.repolenspro.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics
) {

    // સર્ચ ઇવેન્ટ ટ્રેક કરવા માટે
    fun logSearchEvent(query: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    // ક્રેશ થયા વગરની કોઈ ગંભીર એરર Crashlytics પર મોકલવા માટે
    fun recordNonFatalException(throwable: Throwable, customMessage: String? = null) {
        customMessage?.let { crashlytics.log(it) }
        crashlytics.recordException(throwable)
    }

}