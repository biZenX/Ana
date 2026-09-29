package com.wafeer.app.domain.model.changelog

sealed class ChangelogDecision {
    data object Skip : ChangelogDecision()
    data class Show(val release: VersionRelease) : ChangelogDecision()
}
