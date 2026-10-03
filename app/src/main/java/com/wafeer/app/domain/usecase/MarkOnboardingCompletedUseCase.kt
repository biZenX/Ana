package com.wafeer.app.domain.usecase

import com.wafeer.app.data.repository.SettingsRepository
import javax.inject.Inject

class MarkOnboardingCompletedUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() {
        settingsRepository.setOnboardingCompleted(true)
    }
}
