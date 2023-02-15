package dpr.svich.natureprophet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dpr.svich.natureprophet.repository.Params
import dpr.svich.natureprophet.repository.ParamsRepository

class ChartsViewModel(private val repository: ParamsRepository): ViewModel() {
    val paramsList : LiveData<List<Params>> = repository.paramsList
}