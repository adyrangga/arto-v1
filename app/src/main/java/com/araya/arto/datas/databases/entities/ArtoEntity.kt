package com.araya.arto.datas.databases.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by el araya on 02/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
@Entity(tableName = "tb_arto")
data class ArtoEntity (
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var colCategory: String = "",
        var colKind: String = "",
        var colCount: Int = 0,
        var colDescription: String = "",
        var colCreatedAt: String = "",
        var colDeletedFlag: Int = 0,
        var colDeletedAt: String = ""
) {
    constructor() : this(
            0, "", "", 0, "",
            "", 0, "")
}