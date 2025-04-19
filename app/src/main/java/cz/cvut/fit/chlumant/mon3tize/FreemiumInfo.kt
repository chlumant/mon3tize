package cz.cvut.fit.chlumant.mon3tize

data class FreemiumInfo(
    val isActive: Boolean = false,
    val activatedAt: Long = 0L,
    val expiresAt: Long = 0L,
    val isFirst: Boolean = true
)