package com.example.bookreader.respository


import com.example.bookreader.Network.BooksApi
import com.example.bookreader.data.DataOrException
import com.example.bookreader.data.Resource
import com.example.bookreader.model.ItemsItem
import com.example.bookreader.model.SearchInfo
import com.google.api.ResourceProto.resource
import javax.inject.Inject


class BooksRepository @Inject constructor(private val api: BooksApi) {
    suspend fun getBooks(searchQuery: String): Resource<List<ItemsItem>>
    {
       return try{
            Resource.Loading(data = true)
            val itemsList=api.getAllBooks(searchQuery).items
           if(itemsList!!.isNotEmpty()) Resource.Loading(data = false)
            Resource.Success(data=itemsList)
        }catch(exception: Exception){
            Resource.Error(message = exception.message.toString())
        }
    }

    suspend fun getBookInfo(bookId: String) :Resource<ItemsItem>{
        val response=try {
            Resource.Loading(data = true)
            api.getBookInfo(bookId)
        }catch (exception: Exception){
            return Resource.Error(message = "An error occured ${exception.message.toString()}")
        }
        Resource.Loading(data = false)
        return Resource.Success(data=response)
    }
}
















//class BooksRepository @Inject constructor(private val api: BooksApi) {
//
//    private val dataOrException= DataOrException<List<ItemsItem>, Boolean, Exception>()
//
//    private val bookInfoDataOrException= DataOrException<ItemsItem, Boolean, Exception>()
//
//    suspend fun getBooks(searchQuery: String) : DataOrException<List<ItemsItem>,
//            Boolean, Exception>{
//        try{
//            dataOrException.loading=true
//            dataOrException.data=api.getAllBooks(searchQuery).items
//            if(dataOrException.data!!.isNotEmpty()){
//                dataOrException.loading=false
//            }
//
//        }catch (e: Exception){
//            dataOrException.e=e
//        }
//        return dataOrException
//    }
//
//    suspend fun getBookInfo(bookId: String): DataOrException<ItemsItem, Boolean, Exception>{
//        val response= try{
//            bookInfoDataOrException.loading=true
//            bookInfoDataOrException.data=api.getBookInfo(bookId=bookId)
//
//            if(bookInfoDataOrException.data.toString()!!.isNotEmpty()){
//                bookInfoDataOrException.loading=false
//            }else{}
//
//        }catch(e: Exception){
//            bookInfoDataOrException.e=e
//        }
//        return bookInfoDataOrException
//    }
//}