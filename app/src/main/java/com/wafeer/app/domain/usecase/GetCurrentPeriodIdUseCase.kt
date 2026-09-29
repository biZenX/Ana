package com.wafeer.app.domain.usecase

import com.wafeer.app.data.repository.SettingsRepository
import javax.inject.Inject

class GetCurrentPeriodIdUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): Long {
        return settingsRepository.getCurrentPeriodId()
    }
}
