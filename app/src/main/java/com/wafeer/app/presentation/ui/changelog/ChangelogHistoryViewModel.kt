package com.wafeer.app.presentation.ui.changelog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafeer.app.data.repository.ChangelogRepository
import com.wafeer.app.domain.model.changelog.VersionRelease
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChangelogHistoryViewModel @Inject constructor(
    private val changelogRepository: ChangelogRepository,
) : ViewModel() {

    val releases: StateFlow<List<VersionRelease>> = flow {
        emit(changelogRepository.getAllReleases())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
}
