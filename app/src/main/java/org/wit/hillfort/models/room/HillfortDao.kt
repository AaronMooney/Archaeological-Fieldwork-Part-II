package org.wit.hillfort.models.room

import androidx.room.*
import org.wit.hillfort.models.HillfortModel

@Dao
interface HillfortDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(hillfortModel: HillfortModel)

    @Query("SELECT * FROM HillfortModel")
    fun findAll(): MutableList<HillfortModel>

    @Query("select * from HillfortModel where id = :id")
    fun findById(id: Long): HillfortModel

    @Update
    fun update(hillfort: HillfortModel)

    @Delete
    fun deletePlacemark(hillfort: HillfortModel)
}