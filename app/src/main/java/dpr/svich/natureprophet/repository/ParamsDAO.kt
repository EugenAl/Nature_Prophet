package dpr.svich.natureprophet.repository

import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ParamsDAO {

    @Query("SELECT * FROM params ORDER BY time DESC LIMIT 10")
    fun getLastList(): LiveData<List<Params>>

    @Query("SELECT * FROM params ORDER BY time DESC LIMIT 1")
    fun getCurrentParams(): LiveData<Params>

    @Insert
    fun insert(params: Params)

    @Delete
    fun delete(params: Params)

    @Query("DELETE FROM PARAMS")
    fun deleteAll()
}