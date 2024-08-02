package com.example.bookapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookapp.database.BookEntity
import com.example.bookapp.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescViewModel(private val repository: Repository): ViewModel() {
    companion object {
        fun createFactory(repository: Repository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DescViewModel::class.java)) {
                        return DescViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
    val data:LiveData<List<BookEntity>> = repository.getAllBooks().asLiveData()

    fun checkFav(bookEntity: BookEntity): Boolean {
        return repository.checkFav(bookEntity)
    }

    fun insertBook(bookEntity: BookEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertBook(bookEntity)
        }
    }
    fun deleteBook(bookEntity: BookEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBook(bookEntity)
        }
    }
}