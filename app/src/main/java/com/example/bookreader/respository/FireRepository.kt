package com.example.bookreader.respository

import com.example.bookreader.data.DataOrException
import com.example.bookreader.model.MBook
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import retrofit2.http.Query
import javax.inject.Inject

class FireRepository @Inject constructor(
    private val queryBook: com.google.firebase.firestore.Query
    ){
    suspend fun getAllBookFromDatabase(): DataOrException<List<MBook>,Boolean,Exception>{
        val dataOrException=DataOrException<List<MBook>,Boolean,Exception>()
        try{
            dataOrException.loading=true
            dataOrException.data=queryBook.get().await().documents.map{documentSnapshot ->
                documentSnapshot.toObject(MBook::class.java)!!
            }
            if(dataOrException.data!!.isNullOrEmpty()){
                dataOrException.loading=false
            }

        }catch (exception: FirebaseFirestoreException){
            dataOrException.e=exception
        }
        return dataOrException
    }
}