package dpr.svich.natureprophet.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Params(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "humidity") val humidity: Int?,
    @ColumnInfo(name = "temp_air") val tempAir: Int?,
    @ColumnInfo(name = "temp_ground") val tempGround: Int?,
    @ColumnInfo(name = "time") val time: Long?
)