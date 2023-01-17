package net.jeikobu.uplewd.model

data class UserSettings(
    var retentionPeriod: RetentionPeriod = RetentionPeriod.INF
)