package com.example.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.WatchlistItem
import com.example.data.model.PriceAlert
import com.example.data.model.PortfolioHolding
import kotlinx.coroutines.flow.Flow

@Dao
interface CreatorDao {

    // --- Watchlist DAO ---
    @Query("SELECT * FROM watchlist ORDER BY timestamp DESC")
    fun getAllWatchlist(): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItem)

    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    suspend fun deleteWatchlistItem(symbol: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE symbol = :symbol)")
    fun isInWatchlist(symbol: String): Flow<Boolean>


    // --- Price Alerts DAO ---
    @Query("SELECT * FROM price_alerts ORDER BY timestamp DESC")
    fun getAllPriceAlerts(): Flow<List<PriceAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceAlert(alert: PriceAlert)

    @Query("DELETE FROM price_alerts WHERE id = :id")
    suspend fun deletePriceAlert(id: Int)

    @Update
    suspend fun updatePriceAlert(alert: PriceAlert)


    // --- Portfolio Holdings DAO ---
    @Query("SELECT * FROM portfolio_holdings ORDER BY timestamp DESC")
    fun getAllPortfolioHoldings(): Flow<List<PortfolioHolding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioHolding(holding: PortfolioHolding)

    @Query("DELETE FROM portfolio_holdings WHERE id = :id")
    suspend fun deletePortfolioHolding(id: Int)
}

@Database(entities = [WatchlistItem::class, PriceAlert::class, PortfolioHolding::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun creatorDao(): CreatorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "creator_toolkit_database" // Keep database name for consistency
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
