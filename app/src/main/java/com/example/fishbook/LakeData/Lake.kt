package com.example.fishbook.LakeData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lake_data_V2")
data class Lake(
    @PrimaryKey @ColumnInfo(name = "LAKE_ID") val lakeId: Int?,
    @ColumnInfo(name = "LAKE_NAME") val lakeName: String?,
    @ColumnInfo(name = "LAKE_NAME_ALT") val lakeNameAlt: String?,
    @ColumnInfo(name = "TOWN") val town: String?,
    @ColumnInfo(name = "COUNTY") val county: String?,
    @ColumnInfo(name = "REGION") val region: String?,
    @ColumnInfo(name = "ACRES") val acres: Double?,
    @ColumnInfo(name = "GPS_LAT") val gps_lat: Double?,
    @ColumnInfo(name = "GPS_LONG") val gps_long: Double?,
    @ColumnInfo(name = "FISHDEX_LIST") val fishdexList: String?
)