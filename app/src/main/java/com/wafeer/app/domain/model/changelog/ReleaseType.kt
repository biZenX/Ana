package com.wafeer.app.domain.model.changelog

import kotlinx.serialization.Serializable

enum class ReleaseType {
    @Serializable
    FEATURE,
    @Serializable
    IMPROVEMENT,
    @Serializable
    BUG_FIX,
    @Serializable
    REMOVED,
    @Serializable
    SECURITY,
}
