package com.gabrielsantana.projects.controledegastos.data.db

import androidx.room.TypeConverter
import java.util.*

class TransactionDatabaseConverters {

    @TypeConverter
    fun fromTimestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}