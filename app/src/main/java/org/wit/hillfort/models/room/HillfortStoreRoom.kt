package org.wit.hillfort.models.room

import android.content.Context
import androidx.room.Room
import org.jetbrains.anko.coroutines.experimental.bg
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.models.HillfortStore
import org.wit.hillfort.models.room.Database
import org.wit.hillfort.models.room.HillfortDao

class HillfortStoreRoom(val context: Context) : HillfortStore {

    var dao: HillfortDao

    init {
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration()
            .build()
        dao = database.hillfortDao()
    }

    suspend override fun findAll(): MutableList<HillfortModel> {
        val deferredPlacemarks = bg {
            dao.findAll()
        }
        val hillforts = deferredPlacemarks.await()
        return hillforts
    }

    suspend override fun findById(id: Long): HillfortModel? {
        val deferredPlacemark = bg {
            dao.findById(id)
        }
        val hillfort = deferredPlacemark.await()
        return hillfort
    }

    suspend override fun create(hillfort: HillfortModel) {
        bg {
            dao.create(hillfort)
        }
    }

    suspend override fun update(hillfort: HillfortModel) {
        bg {
            dao.update(hillfort)
        }
    }

    suspend override fun delete(hillfort: HillfortModel) {
        bg {
            dao.deletePlacemark(hillfort)
        }
    }

    override fun clear() {
    }
}