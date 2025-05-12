package cz.cvut.fit.chlumant.mon3tize

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

public data class Mon3tizeConfiguration(
    val freemium: Freemium = Freemium.Disabled,
    val logLevel: LogLevel = LogLevel.NONE,
    val clientId: String? = null
) {

    public enum class LogLevel {
        NONE,
        ERROR,
        ALL,
    }

    public sealed interface Freemium {

        public data object Disabled : Freemium

        public data class Enabled(val freemiumDuration: Duration = 7.days) : Freemium
    }
}