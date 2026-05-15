package com.sanguosha.record.model

enum class Identity(val displayName: String, val camp: Camp) {
    LORD("主公", Camp.LORD_CAMP),
    LOYALIST("忠臣", Camp.LORD_CAMP),
    REBEL("反贼", Camp.REBEL_CAMP),
    SPY("内奸", Camp.SPY_CAMP);

    companion object {
        fun fromName(name: String): Identity =
            entries.first { it.name == name }
    }
}

enum class Camp(val displayName: String) {
    LORD_CAMP("主公阵营"),
    REBEL_CAMP("反贼阵营"),
    SPY_CAMP("内奸阵营")
}
