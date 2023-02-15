package dpr.svich.natureprophet.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Params::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract fun paramsDAO(): ParamsDAO

    private class AppDatabaseCallback(private val scope:CoroutineScope): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { dataBase ->
                scope.launch {
                    var paramsDAO = dataBase.paramsDAO()

                    paramsDAO.deleteAll()

                    var params = Params(1, 20, 31, 29,
                        System.currentTimeMillis())
                    paramsDAO.insert(params)
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "params_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}