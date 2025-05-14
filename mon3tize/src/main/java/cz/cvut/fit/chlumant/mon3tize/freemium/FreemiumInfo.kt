package cz.cvut.fit.chlumant.mon3tize.freemium

internal data class FreemiumInfo(
    val active: Boolean = false,
    val activatedAt: Long = 0L,
    val expiresAt: Long = 0L,
    val trialUsed: Boolean = false
)