package com.sanguosha.record

import android.app.Application
import com.sanguosha.record.data.db.AppDatabase

class SanguoshaApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
