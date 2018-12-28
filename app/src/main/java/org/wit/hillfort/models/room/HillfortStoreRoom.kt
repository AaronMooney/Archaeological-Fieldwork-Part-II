package org.wit.hillfort.models.room

import android.content.Context
import androidx.room.Room
import org.wit.hillfort.models.HillfortModel
import org.jetbrains.anko.coroutines.experimental.bg

class UserStoreRoom(val context: Context) : UserStore {

    var dao: UserDao

    init {
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration()
            .build()
        dao = database.hillfortDao()
    }

    suspend override fun findAllHillforts(): ArrayList<HillfortModel> {
        val deferredHillforts = bg {
            dao.findAllHillforts()
        }
        val hillforts = deferredHillforts.await()
        return hillforts as ArrayList<HillfortModel>
    }

    suspend override fun getUsers(): List<UserModel> {
        val deferredUsers = bg {
            dao.getUsers()
        }
        val users = deferredUsers.await()
        return users
    }

    suspend override fun findById(user: UserModel,id: Long): HillfortModel? {
        val deferredHillfort = bg {
            dao.findById(id)
        }
        val hillfort = deferredHillfort.await()
        return hillfort
    }

    suspend override fun addUser(user: UserModel) {
        bg {
            dao.addUser(user)
        }
    }

    suspend override fun addUser(user: UserModel, toVisit: ArrayList<HillfortModel>) {
        bg {
            dao.addUser(user, toVisit)
        }
    }

    suspend override fun updateUser(user: UserModel) {
        bg {
            dao.updateUser(user)
        }
    }

    suspend override fun updateUser(user: UserModel, hillfort: HillfortModel) {
        bg {
            dao.updateUser(user, hillfort)
        }
    }

    suspend override fun deleteUser(user: UserModel) {
        bg {
            dao.deleteUser(user)
        }
    }

    suspend override fun deleteHillfort(user: UserModel, hillfort: HillfortModel) {
        bg {
            dao.deleteHillfort(user, hillfort)
        }
    }

    fun clear() {
    }
}