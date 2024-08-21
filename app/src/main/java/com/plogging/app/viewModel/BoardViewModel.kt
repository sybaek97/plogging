package com.plogging.app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plogging.app.data.BoardData
import com.plogging.app.repository.BoardRepository
import kotlinx.coroutines.launch

class BoardViewModel (private val repository: BoardRepository) : ViewModel() {

    private val _boardData = MutableLiveData<List<BoardData>>()
    val boardData: LiveData<List<BoardData>>
        get() = _boardData

    fun fetchBoard() {
        viewModelScope.launch {
            val entries = repository.getGuestbookEntries()
            _boardData.postValue(entries)
        }
    }

    fun addGuestbookEntry(message: String, uid: String) {
        viewModelScope.launch {
            val entry = BoardData(uid, message, System.currentTimeMillis())
            repository.addGuestbookEntry(entry)
            fetchBoard() // 새 데이터를 추가한 후 다시 불러옵니다.
        }
    }
}