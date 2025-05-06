package cz.cvut.fit.chlumant.mon3tize.rewards

interface RewardHandler {
    suspend fun handleReward(onError: (Throwable) -> Unit = {})
    val rewardType: RewardType
}