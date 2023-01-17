package net.jeikobu.uplewd.model

enum class RetentionPeriod(val seconds: Long) {
    MINUTE15(60 * 10),
    MINUTE30(60 * 15),
    MINUTE45(60 * 45),
    HOUR1(60*60),
    HOUR2(60*60*2),
    HOUR4(60*60*4),
    HOUR6(60*60*6),
    HOUR12(60*60*12),
    HOUR24(60*60*24),
    INF(0)
}