package cz.cvut.fit.chlumant.mon3tize.components.banners

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(
    adUnitId: String,
    bannerType: BannerType,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(bannerType.toAdSize())
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

private fun BannerType.toAdSize(): AdSize {
    return when (this) {
        BannerType.Banner -> AdSize.BANNER
        BannerType.LargeBanner -> AdSize.LARGE_BANNER
        BannerType.MediumRectangle -> AdSize.MEDIUM_RECTANGLE
        BannerType.FullBanner -> AdSize.FULL_BANNER
        BannerType.Leaderboard -> AdSize.LEADERBOARD
        BannerType.WideSkyscraper -> AdSize.WIDE_SKYSCRAPER
    }
}
