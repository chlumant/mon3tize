package cz.cvut.fit.chlumant.mon3tize.rewards

import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlin.time.Duration.Companion.days

class FreemiumRewardHandler() : RewardHandler {

    override val rewardType = RewardType.FreemiumExtension

    override suspend fun handleReward(onError: (Throwable) -> Unit) {
        try {
            Mon3tize.freemium.extendFreemiumBy(1.days)
        } catch (e: Exception) {
            onError(e)
        }
    }
}