package org.wit.hillfort.models.room

import androidx.room.*
import org.wit.hillfort.models.HillfortModel

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user:UserModel, toVisit: ArrayList<HillfortModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: UserModel)

    @Query("SELECT * FROM UserModel")
    fun getUsers(): List<UserModel>

    @Query("select selectedHillfort from UserModel where id = :id")
    fun findById(id :Long) : HillfortModel?

    @Query("select hillforts from UserModel")
    fun findAllHillforts(): List<HillfortModel>

    @Update
    fun updateUser(user: UserModel, hillfort: HillfortModel)

    @Update
    fun updateUser(user: UserModel)

    @Delete
    fun deleteUser(user:UserModel)

    @Delete
    fun deleteHillfort(user: UserModel, hillfort: HillfortModel)
}