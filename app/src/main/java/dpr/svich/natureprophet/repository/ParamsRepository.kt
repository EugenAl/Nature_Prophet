package dpr.svich.natureprophet.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ParamsRepository(private val paramsDAO: ParamsDAO) {

    val currentParams: LiveData<Params> = paramsDAO.getCurrentParams()

    val paramsList: LiveData<List<Params>> = paramsDAO.getLastList()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(params: Params){
        paramsDAO.insert(params)
    }
}