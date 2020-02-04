package com.araya.arto.datas.databases.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.araya.arto.datas.databases.entities.NoteEntity

/**
 * Created by el araya on 02/02/2020
 * adyranggahidayat@gmail.com
 * Copyright (c) 2020
 **/
@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(noteEntity: NoteEntity) : Long

    @Update
    fun updateNote(noteEntity: NoteEntity) : Int

    @Delete
    fun deleteNote(noteEntity: NoteEntity) : Int

    @Query("SELECT * FROM tb_note WHERE id = :id")
    fun getNoteById(id: Int): LiveData<NoteEntity>

    @Query("SELECT * FROM tb_note ORDER BY id DESC")
    fun getAllNote(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM tb_note WHERE colBuyStatus = :buyStatus ORDER BY id DESC")
    fun getNoteByStatus(buyStatus: String): LiveData<List<NoteEntity>>
}