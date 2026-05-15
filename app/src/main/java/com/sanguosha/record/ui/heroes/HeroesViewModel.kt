package com.sanguosha.record.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanguosha.record.SanguoshaApp
import com.sanguosha.record.data.entity.Hero
import com.sanguosha.record.data.repository.HeroRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HeroesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as SanguoshaApp).database
    private val repository = HeroRepository(database.heroDao())

    val heroes: StateFlow<List<Hero>> = repository.getAllHeroes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _newHeroName = MutableStateFlow("")
    val newHeroName: StateFlow<String> = _newHeroName.asStateFlow()

    fun updateNewHeroName(name: String) {
        _newHeroName.value = name
    }

    fun addHero(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertHero(Hero(name = name.trim()))
            _newHeroName.value = ""
        }
    }

    fun deleteHero(hero: Hero) {
        viewModelScope.launch {
            repository.deleteHero(hero)
        }
    }
}
