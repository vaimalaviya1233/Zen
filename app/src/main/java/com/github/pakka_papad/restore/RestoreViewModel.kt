package com.github.pakka_papad.restore

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.R
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
    private val context: Application,
    private val blacklistService: BlacklistService,
) : ViewModel() {

    private val _restoreList = mutableStateListOf<Boolean>()
    val restoreList : List<Boolean> = _restoreList

    val blackListedSongs = blacklistService.blacklistedSongs
        .onEach {
            while (_restoreList.size < it.size) _restoreList.add(false)
            while (_restoreList.size > it.size) _restoreList.removeLast()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun updateRestoreList(index: Int, isSelected: Boolean){
        if (restoreState.value !is Resource.Idle) return
        if (index >= _restoreList.size) return
        _restoreList[index] = isSelected
    }

    private val _restoreState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val restoreState = _restoreState.asStateFlow()

    fun restoreSongs(){
        viewModelScope.launch {
            _restoreState.update { Resource.Loading() }
            delay(5000)
            val blacklist = blackListedSongs.value
            val toRestore = _restoreList.indices
                .filter { _restoreList[it] }
                .map { blacklist[it] }
            try {
                blacklistService.whitelistSong(toRestore)
                showToast(context.getString(R.string.done_rescan_to_see_all_the_restored_songs))
                _restoreState.update { Resource.Success(Unit) }
            } catch (e: Exception){
                Timber.e(e)
                showToast(context.getString(R.string.some_error_occurred))
                _restoreState.update { Resource.Error("") }
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}