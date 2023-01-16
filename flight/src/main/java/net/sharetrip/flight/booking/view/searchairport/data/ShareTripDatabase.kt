package net.sharetrip.flight.booking.view.searchairport.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class], version = 1)
abstract class ShareTripDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao

    companion object {
        @Volatile private var databaseInstance: ShareTripDatabase? = null

        fun getInstance(context: Context): ShareTripDatabase? {

            return databaseInstance ?: synchronized(this) {
                databaseInstance ?: buildDataBase(context).also { databaseInstance = it }
            }
        }

        private fun buildDataBase(context: Context): ShareTripDatabase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    ShareTripDatabase::class.java, "sharetrip-db"
            ).build()
        }
    }
}
