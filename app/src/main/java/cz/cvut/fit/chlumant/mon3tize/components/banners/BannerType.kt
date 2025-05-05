package cz.cvut.fit.chlumant.mon3tize.components.banners

import com.google.android.gms.ads.AdSize

/**
 * Enum reprezentující typy bannerových reklam dostupné přes Google Mobile Ads SDK.
 * Používej v kombinaci s funkcí AdBanner() pro jednodušší a bezpečný výběr velikosti reklamy.
 */
enum class BannerType(val adSize: AdSize) {
    Banner(AdSize.BANNER),                     // 320x50
    LargeBanner(AdSize.LARGE_BANNER),          // 320x100
    MediumRectangle(AdSize.MEDIUM_RECTANGLE),  // 300x250
    FullBanner(AdSize.FULL_BANNER),            // 468x60
    Leaderboard(AdSize.LEADERBOARD),           // 728x90
    WideSkyscraper(AdSize.WIDE_SKYSCRAPER)     // 160x600
}