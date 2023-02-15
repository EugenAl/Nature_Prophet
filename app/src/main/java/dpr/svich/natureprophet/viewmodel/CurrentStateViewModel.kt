package dpr.svich.natureprophet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dpr.svich.natureprophet.repository.Params
import dpr.svich.natureprophet.repository.ParamsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CurrentStateViewModel(private val repository: ParamsRepository): ViewModel() {

    val currentParams: LiveData<Params> = repository.currentParams

    fun insert(params: Params) {
        Thread{
            runBlocking { repository.insert(params) }
        }.start()
        Log.i("MyDataBase", "Data inserted. $params")
    }
}

class ParamsViewModelFactory(private val repository: ParamsRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentStateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrentStateViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(ChartsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ChartsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}