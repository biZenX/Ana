package com.wafeer.app.domain.usecase

import com.wafeer.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentPeriodBoundaryUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Pair<Long, Long>> {
        return settingsRepository.observeCurrentPeriodBoundary()
    }
}
