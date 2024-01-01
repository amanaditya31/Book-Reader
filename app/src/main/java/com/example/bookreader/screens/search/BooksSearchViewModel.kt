package com.example.bookreader.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreader.data.Resource
import com.example.bookreader.model.ItemsItem
import com.example.bookreader.respository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksSearchViewModel @Inject constructor(private val repository: BooksRepository)
    : ViewModel(){
    var list: List<ItemsItem> by mutableStateOf(listOf())
    var isloading:Boolean by mutableStateOf(true)
    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("Fiction")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()){
                return@launch
            }

            try{
                when(val response=repository.getBooks(query)){
                    is Resource.Success->{
                        list=response.data!!
                        if(list.isNotEmpty()) isloading=false
                    }
                    is Resource.Error->{
                        Log.e("Network", "searxhBooks: Failed getting books")
                    }else->{isloading=false}
                }
            }catch (exception: Exception){
                isloading=false
                Log.d("Network", "searchBooks: ${exception.message.toString()}")
            }
        }
    }
}