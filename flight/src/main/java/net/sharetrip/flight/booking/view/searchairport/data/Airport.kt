package net.sharetrip.flight.booking.view.searchairport.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "airports", indices = [Index(value = ["name"], unique = true)])
data class Airport (
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
        @ColumnInfo(name = "iata") var iata: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "city") var city: String
)
