package dpr.svich.natureprophet

import android.app.Application
import dpr.svich.natureprophet.repository.AppDataBase
import dpr.svich.natureprophet.repository.ParamsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ThisApplication: Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDataBase.getDatabase(this, applicationScope) }
    val repository by lazy { ParamsRepository(database.paramsDAO()) }
}