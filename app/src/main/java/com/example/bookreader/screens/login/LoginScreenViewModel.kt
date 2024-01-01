package com.example.bookreader.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreader.model.MUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
//    val loadingState= MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth= Firebase.auth

    private val _loading=MutableLiveData(false)

    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailANdPassword (email: String, password: String, home:()-> Unit)
    =viewModelScope.launch{

        try{
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        Log.d("FB","signInWithEmailANdPassword: YAY ${task.result.toString()}"  )
                        home()

                    }else{
                        Log.d("FB","signInWithEmailANdPassword: ${task.result.toString()}"  )
                        TODO()
                    }
                }

        }catch (ex: Exception){
            Log.d("FB","signInWithEmailANdPassword: ${ex.message}"  )
        }


    }

    fun createUserWithEmailANdPassword(email: String, password: String, home: () -> Unit){
        if(_loading.value==false){
            _loading.value=true
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        val displayName=task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName!!)
                        Log.d("FB","CreateWithEmailANdPassword: YAY ${task.result.toString()}"  )
                        home()

                    }else{
                        Log.d("FB","CreateUserWithEmailANdPassword: ${task.result.toString()}"  )
                        TODO()
                    }
                    _loading.value=false
                }
        }
    }

    private fun createUser(displayName: String) {
        val userId=auth.currentUser?.uid
        val user=MUser(userId=userId,
            displayName = displayName,
            avatarUrl = "",
            quote = "Life is Great",
            profession = "Android Developer",
            id=null ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

}