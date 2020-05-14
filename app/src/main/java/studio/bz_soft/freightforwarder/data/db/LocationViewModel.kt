package studio.bz_soft.freightforwarder.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.bz_soft.freightforwarder.data.models.db.Location

class LocationViewModel(
    private val locationDao: LocationDao
) : ViewModel(), LocationViewModelInterface {

    private val _locations = MediatorLiveData<List<Location>>()
    val locations: LiveData<List<Location>> = _locations

    override fun insert(location: Location) = viewModelScope.launch {
        locationDao.insert(location)
    }

    override fun delete(location: Location) = viewModelScope.launch {
        locationDao.delete(location)
    }

    override fun update(location: Location) = viewModelScope.launch {
        locationDao.update(location)
    }

    override fun getAllFromLocation() = viewModelScope.launch {
        _locations.postValue(locationDao.getAllFromLocation())
    }
}