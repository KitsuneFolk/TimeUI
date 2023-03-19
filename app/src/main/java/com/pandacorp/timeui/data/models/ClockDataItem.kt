package com.pandacorp.timeui.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ClockDataItem(
        // Need id to be primary key instead of uuid because of undo possibility, room can insert item at specific position by it's position in the table, so this is very convenient.
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
        // Need uuid because room doesn't create id for items when create object, only when insert it to table.
    @ColumnInfo(name = "uuid") var uuid: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "timeZone") val timeZone: String,
    @ColumnInfo(name = "name") var name: String
)