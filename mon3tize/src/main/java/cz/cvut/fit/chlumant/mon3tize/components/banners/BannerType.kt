package cz.cvut.fit.chlumant.mon3tize.components.banners

/**
 * Enum reprezentující typy bannerových reklam dostupné přes Google Mobile Ads SDK.>
 * Používej v kombinaci s funkcí AdBanner() pro jednodušší a bezpečný výběr velikosti reklamy.
 */
public enum class BannerType() {
    Banner(),                     // 320x50 px
    LargeBanner(),          // 320x100 px
    MediumRectangle(),  // 300x250 px
    FullBanner(),            // 468x60 px
    Leaderboard(),           // 728x90 px
    WideSkyscraper()     // 160x600 px
}