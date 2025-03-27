package cz.cvut.fit.chlumant.mon3tize

sealed interface AdMob {
    data object Disabled : AdMob

    data class Enabled(
        val key: String
    ) : AdMob
}

data class Mon3tizeConfiguration(
    val enableFreemium: Boolean = true,
    val adMob: AdMob = AdMob.Disabled
)