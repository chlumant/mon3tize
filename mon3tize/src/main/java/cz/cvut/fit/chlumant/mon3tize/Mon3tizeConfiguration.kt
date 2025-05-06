package cz.cvut.fit.chlumant.mon3tize

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class Mon3tizeConfiguration(
    val freemium: Freemium = Freemium.Disabled,
    val logLevel: LogLevel = LogLevel.NONE,
) {

    enum class LogLevel {
        NONE,
        ERROR,
        ALL,
    }

    sealed interface Freemium {

        data object Disabled : Freemium

        data class Enabled(val freemiumDuration: Duration = 7.days) : Freemium
    }
}