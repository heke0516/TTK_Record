package com.sanguosha.record.data.repository

import com.sanguosha.record.data.dao.HeroDao
import com.sanguosha.record.data.entity.Hero
import kotlinx.coroutines.flow.Flow

class HeroRepository(private val heroDao: HeroDao) {

    fun getAllHeroes(): Flow<List<Hero>> = heroDao.getAllHeroes()

    suspend fun getHeroById(id: Long): Hero? = heroDao.getHeroById(id)

    suspend fun getHeroByName(name: String): Hero? = heroDao.getHeroByName(name)

    suspend fun insertHero(hero: Hero): Long = heroDao.insert(hero)

    suspend fun updateHero(hero: Hero) = heroDao.update(hero)

    suspend fun deleteHero(hero: Hero) = heroDao.delete(hero)
}
