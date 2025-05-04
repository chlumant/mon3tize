package cz.cvut.fit.chlumant.mon3tize.freemium

data class FreemiumInfo(
    val isActive: Boolean = false,
    val activatedAt: Long = 0L,
    val expiresAt: Long = 0L,
    val trialUsed: Boolean = false
)