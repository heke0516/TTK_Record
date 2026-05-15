package com.sanguosha.record.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val datetime: Long = System.currentTimeMillis(),
    val location: String? = null,
    val playerCount: Int = 0,
    val winnerIdentity: String = "",
    val durationSeconds: Long = 0,
    val note: String? = null
)
