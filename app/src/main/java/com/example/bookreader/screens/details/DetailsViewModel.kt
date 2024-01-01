package com.example.bookreader.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreader.data.Resource
import com.example.bookreader.model.ItemsItem
import com.example.bookreader.respository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: BooksRepository): ViewModel(){
    suspend fun getBooksInfo(booksId: String): Resource<ItemsItem> {

        return repository.getBookInfo(booksId)
    }

}