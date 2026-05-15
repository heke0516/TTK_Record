package com.sanguosha.record.data.dao

import androidx.room.*
import com.sanguosha.record.data.entity.Hero
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {

    @Query("SELECT * FROM heroes ORDER BY name ASC")
    fun getAllHeroes(): Flow<List<Hero>>

    @Query("SELECT * FROM heroes WHERE id = :id")
    suspend fun getHeroById(id: Long): Hero?

    @Query("SELECT * FROM heroes WHERE name = :name LIMIT 1")
    suspend fun getHeroByName(name: String): Hero?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(hero: Hero): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(heroes: List<Hero>)

    @Update
    suspend fun update(hero: Hero)

    @Delete
    suspend fun delete(hero: Hero)

    @Query("SELECT COUNT(*) FROM heroes")
    suspend fun getCount(): Int
}
