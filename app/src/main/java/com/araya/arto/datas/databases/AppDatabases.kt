package com.araya.arto.datas.databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.araya.arto.datas.databases.daos.ArtoDao
import com.araya.arto.datas.databases.daos.NoteDao
import com.araya.arto.datas.databases.entities.ArtoEntity

/**
 * Created by el araya on 02/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
@Database(
        entities = [ArtoEntity::class],
        version = 1,
        exportSchema = false
)
abstract class AppDatabases : RoomDatabase() {
    abstract fun artoDao(): ArtoDao
    abstract fun noteDao(): NoteDao

    companion object {
        private var db: AppDatabases? = null

        fun instanceDB(context: Context): AppDatabases {
            if (db == null) {
                synchronized(AppDatabases::class) {
                    db = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabases::class.java,
                            "db_artto"
                    ).build()
                }
            }
            return db!!
        }

        fun destroyDb() {
            db = null
        }
    }
}