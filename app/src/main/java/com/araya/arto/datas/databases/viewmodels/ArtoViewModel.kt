package com.araya.arto.datas.databases.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.Context
import com.araya.arto.datas.databases.AppDatabases
import com.araya.arto.datas.databases.entities.ArtoEntity

/**
 * Created by el araya on 02/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
class ArtoViewModel(context: Context)
    : AndroidViewModel(context.applicationContext as Application) {

    private val dao by lazy { AppDatabases.instanceDB(context).artoDao() }

    fun insertData(artoEntity: ArtoEntity, onUpdatedListener: OnUpdatedListener) {
        Thread {
            val value = dao.insertData(artoEntity)
            onUpdatedListener.onUpdated(value.toInt())
        }.start()
    }

    fun readDatas(category: String): LiveData<List<ArtoEntity>> {
        return dao.getDatasByCategory(category)
    }

    fun readDatasBetweenCreatedAt(
            category: String,
            start: String,
            end: String
    ) : LiveData<List<ArtoEntity>> {
        return dao.getDatasByCategoryBetweenCreatedAt(category, start, end)
    }

    fun readDataById(id: Int, onReadDataByIdListener: OnReadDataByIdListener) {
        Thread {
            val data = dao.getDataById(id)
            onReadDataByIdListener.onRead(data)
        }.start()
    }

    fun readSumCountCategoryBetweenCreatedAt(category: String, start: String, end: String, onReadSumCount: OnReadSumCount) {
        Thread {
            val value = dao.sumCountCategoryBetweenCreatedAt(category, start, end)
            onReadSumCount.onReadSumCount(value)
        }.start()
    }

    fun readSumCountOutcomeAlongTime(category: String, onReadSumCount: OnReadSumCount) {
        Thread {
            val value = dao.sumCountOutcomeAlongTime(category)
            onReadSumCount.onReadSumCount(value)
        }.start()
    }

    fun updateData(artoEntity: ArtoEntity, onUpdatedListener: OnUpdatedListener) {
        Thread {
            val value = dao.updateData(artoEntity)
            onUpdatedListener.onUpdated(value)
        }.start()
    }

    fun deleteData(artoEntity: ArtoEntity, onUpdatedListener: OnUpdatedListener) {
        Thread {
            val value = dao.deleteData(artoEntity)
            onUpdatedListener.onUpdated(value)
        }.start()
    }

    interface OnUpdatedListener {
        fun onUpdated(value: Int)
    }

    interface OnReadDatasListener {
        fun onRead(artoEntity: LiveData<List<ArtoEntity>>)
    }

    interface OnReadDataByIdListener {
        fun onRead(artoEntity: LiveData<ArtoEntity>)
    }

    interface OnReadSumCount {
        fun onReadSumCount(sumCount: LiveData<Int>)
    }
}