package cz.cvut.fit.chlumant.mon3tize

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class Mon3tizeConfiguration(
    val enableFreemium: Boolean = false,
    val adMobEnabled: Boolean = false,
    val freemiumDuration: Duration = 0.days
)