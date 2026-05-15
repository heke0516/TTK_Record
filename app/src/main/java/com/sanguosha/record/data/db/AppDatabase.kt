package com.sanguosha.record.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sanguosha.record.data.dao.GameDao
import com.sanguosha.record.data.dao.GamePlayerDao
import com.sanguosha.record.data.dao.HeroDao
import com.sanguosha.record.data.dao.PlayerDao
import com.sanguosha.record.data.entity.Game
import com.sanguosha.record.data.entity.GamePlayer
import com.sanguosha.record.data.entity.Hero
import com.sanguosha.record.data.entity.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Player::class, Hero::class, Game::class, GamePlayer::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun heroDao(): HeroDao
    abstract fun gameDao(): GameDao
    abstract fun gamePlayerDao(): GamePlayerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val DEFAULT_HEROES = listOf(
            "曹操", "刘备", "孙权", "诸葛亮", "关羽", "张飞", "赵云", "吕布",
            "貂蝉", "司马懿", "周瑜", "陆逊", "甘宁", "黄盖", "华佗", "马超",
            "黄月英", "甄姬", "大乔", "小乔", "许褚", "典韦", "夏侯惇", "张辽",
            "郭嘉", "荀彧", "庞统", "魏延", "姜维", "邓艾", "孙尚香", "黄忠",
            "夏侯渊", "曹仁", "太史慈", "吕蒙", "贾诩", "张角", "袁绍", "颜良",
            "文丑", "马岱", "徐庶", "法正", "关兴", "张苞", "祝融", "孟获"
        )

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sanguosha_record.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.heroDao()?.let { dao ->
                                    val heroes = DEFAULT_HEROES.distinct().map { Hero(name = it) }
                                    dao.insertAll(heroes)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
