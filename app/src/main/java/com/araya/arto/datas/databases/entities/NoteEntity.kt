package com.araya.arto.datas.databases.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by el araya on 02/02/2020
 * adyranggahidayat@gmail.com
 * Copyright (c) 2020
 **/
@Entity(tableName = "tb_note")
data class NoteEntity  (
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var colNote: String = "",
        var colNoteCount: Int = 0,
        var colBuyStatus: Int = 0,
        var colCreatedAt: String = "",
        var colDeletedFlag: Int = 0,
        var colDeletedAt: String = ""
) {
    constructor() : this(
            0, "", 0, 0,
            "", 0, "")
}