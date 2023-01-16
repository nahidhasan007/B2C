package net.sharetrip.flight.booking.view.searchairport.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AirportDao {
    @Insert
    fun insert(airport: Airport)

    @Query("DELETE FROM airports")
    fun deleteAllAirports()

    @Query("SELECT * FROM airports ")
    suspend fun getAirports(): List<Airport>
}
