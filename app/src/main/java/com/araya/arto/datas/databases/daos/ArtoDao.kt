package com.araya.arto.datas.databases.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.araya.arto.datas.databases.entities.ArtoEntity

/**
 * Created by el araya on 02/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
@Dao
interface ArtoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(artoEntity: ArtoEntity) : Long

    @Update
    fun updateData(artoEntity: ArtoEntity) : Int

    @Delete
    fun deleteData(artoEntity: ArtoEntity) : Int

    @Query("SELECT * FROM tb_arto WHERE id = :id")
    fun getDataById(id: Int): LiveData<ArtoEntity>

    @Query("SELECT * FROM tb_arto WHERE colCategory = :category ORDER BY id DESC")
    fun getDatasByCategory(category: String): LiveData<List<ArtoEntity>>

    @Query("SELECT * FROM tb_arto WHERE colCategory = :category AND colCreatedAt BETWEEN :start AND :end ORDER BY id DESC")
    fun getDatasByCategoryBetweenCreatedAt(category: String, start: String, end: String): LiveData<List<ArtoEntity>>

    @Query("SELECT SUM(colCount) as total FROM tb_arto WHERE colCategory = :category AND colCreatedAt BETWEEN :start AND :end")
    fun sumCountCategoryBetweenCreatedAt(category: String, start: String, end: String): LiveData<Int>

    @Query("SELECT SUM(colCount) as total FROM tb_arto WHERE colCategory = :category")
    fun sumCountOutcomeAlongTime(category: String): LiveData<Int>
}