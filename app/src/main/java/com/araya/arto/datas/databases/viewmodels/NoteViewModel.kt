package com.araya.arto.datas.databases.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.Context
import com.araya.arto.datas.databases.AppDatabases
import com.araya.arto.datas.databases.entities.NoteEntity

/**
 * Created by el araya on 02/02/2020
 * adyranggahidayat@gmail.com
 * Copyright (c) 2020
 **/
class NoteViewModel(context: Context)
    : AndroidViewModel(context.applicationContext as Application) {

    private val dao by lazy { AppDatabases.instanceDB(context).noteDao() }

    fun insertData(noteEntity: NoteEntity, onUpdatedListener: OnUpdatedListener) {
        Thread {
            val value = dao.insertNote(noteEntity)
            onUpdatedListener.onUpdatedListener(value.toInt())
        }.start()
    }

    fun readAllNote() : LiveData<List<NoteEntity>> {
        return dao.getAllNote()
    }

    interface OnUpdatedListener {
        fun onUpdatedListener(value: Int)
    }

    interface OnReadNoteByIdListener {
        fun onReadById(noteEntity: LiveData<NoteEntity>)
    }
}