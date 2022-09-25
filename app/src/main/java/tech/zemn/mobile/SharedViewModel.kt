package tech.zemn.mobile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.zemn.mobile.data.DataManager
import tech.zemn.mobile.data.music.Song
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager
): ViewModel() {

    private val _songs = MutableStateFlow(listOf<Song>())
    val songs = _songs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    init {
        viewModelScope.launch {
            manager.allSongs.collect {
                _songs.value = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
//            manager.scanForMusic()
        }
    }

    fun onSongClicked(song: Song){
        _currentSong.value = song
        manager.updateQueue(listOf(song))
    }
}