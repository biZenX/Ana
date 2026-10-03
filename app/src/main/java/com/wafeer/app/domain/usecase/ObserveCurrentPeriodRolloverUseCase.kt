package com.wafeer.app.domain.usecase

import com.wafeer.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class ObserveCurrentPeriodRolloverUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Pair<BigDecimal, Boolean>> {
        return settingsRepository.observeCurrentPeriodRollover()
    }
}
