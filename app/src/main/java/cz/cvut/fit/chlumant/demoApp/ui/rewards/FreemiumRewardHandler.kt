package cz.cvut.fit.chlumant.demoApp.ui.rewards

import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import kotlin.time.Duration.Companion.days

object FreemiumRewardHandler {

    val rewardType = RewardType.FreemiumExtension

    suspend fun handleReward(onError: (Throwable) -> Unit) {
    }
}