@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.bookreader.screens.update

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookreader.R
import com.example.bookreader.components.InputField
import com.example.bookreader.components.RatingBar
import com.example.bookreader.components.ReaderAppBar
import com.example.bookreader.components.RoundedButton
import com.example.bookreader.data.DataOrException
import com.example.bookreader.model.MBook
import com.example.bookreader.navigation.ReaderScreens
import com.example.bookreader.screens.home.HomeScreenViewModel
import com.example.bookreader.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BookUpdateScreen(navController: NavController,
                     bookItemId: String,
                     viewModel: HomeScreenViewModel= hiltViewModel()){

    Scaffold(topBar = {
        ReaderAppBar(title = "Update Book", navController = navController
                        ,icon= Icons.Default.ArrowBack, showProfile = false
        ){
            navController.popBackStack()
        }
    }){
            val bookInfo= produceState<DataOrException<List<MBook>, Boolean
                    ,Exception>>(initialValue = DataOrException(data = emptyList(),
                        true, e = Exception("")
            )){
                        value=viewModel.data.value
            }.value
        Surface(modifier= Modifier
            .fillMaxSize()
            .padding(it)) {
            Column(modifier=Modifier.padding(top=3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Log.d("INFO", "BookUpdateScreen: ${viewModel.data.value.data.toString()}")
                if(bookInfo.loading==true){
                    LinearProgressIndicator()
                    bookInfo.loading=false
                }else{
                    Surface(modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                        shape=CircleShape,
                        shadowElevation = 4.dp, tonalElevation = 4.dp){
                        ShowBookUpdate(bookInfo=viewModel.data.value,
                            bookItemId=bookItemId)

                    }

                    ShowSimpleForm(book=viewModel.data.value.data?.first{mBook->
                        mBook.googleBookId == bookItemId
                    }!!, navController)
                }
            }
        }
    }

}

@Composable
fun ShowSimpleForm(book: MBook,
                   navController: NavController) {

    val context= LocalContext.current

    val notesText= remember {
        mutableStateOf("")
    }
    val isStartedReading= remember {
        mutableStateOf(false)
    }
    val isFinishedReading= remember {
        mutableStateOf(false)
    }
    SimpleForm(defaultValue = if(book.notes.toString().isNotEmpty()) book.notes.toString()
                                else "No thoughts Available"){note->
        notesText.value=note
    }

    val ratingVal=remember{
        mutableStateOf(0)
    }

    Row(modifier= Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start){
        TextButton(onClick = {isStartedReading.value = true }, enabled = book.startedReading==null) {
           if(book.startedReading==null){
               if (!isStartedReading.value) {
                   Text("Start Reading")
               }else{
                   Text("Started Reading", modifier = Modifier,
                      color= Color.Red.copy(alpha = 0.05f)
                   )
               }
           }else{
                Text("Started on ${formatDate(book.startedReading!!)}")
           }
        }
        Spacer(modifier=Modifier.height(4.dp))
        TextButton(onClick = { isFinishedReading.value=true},
                    enabled=book.finishedReading==null) {
            if(book.finishedReading==null){
                if(!isFinishedReading.value==null){
                    Text("Mark as Read")
                }else{
                    Text("Finished Reading")
                }
            }else{
                Text("Finished on ${
                    formatDate(book.finishedReading!!)}")
            }

        }

    }

    Text(text = "Rating", modifier= Modifier.padding(bottom=3.dp))
    book.rating?.toInt().let{
        RatingBar(rating = it!! ){rating->
            ratingVal.value=rating
        }
    }

    Spacer(modifier=Modifier.padding(bottom = 15.dp))
    Row(){

        val changedNotes=book.notes!=notesText.value
        val changedRating=book.rating?.toInt() !=ratingVal.value
        val isFinishedTimeStamp=if(isFinishedReading.value) Timestamp.now() else{
            book.finishedReading
        }

        val isStartedTimeStamp=if(isStartedReading.value) Timestamp.now()else{
            book.startedReading
        }
        val bookUpdate=changedNotes||changedRating ||isFinishedReading.value || isStartedReading.value

        val bookToUpdate= hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedReading,
            "rating" to ratingVal.value,
            "notes" to notesText.value).toMap()

        RoundedButton (label="Update"){
            if(bookUpdate){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener{task->
                        showToast(context, "Book Updated Succesfully")
                        navController.popBackStack()

                        Log.d("Success", "ShowSimpleForm: ${task.result.toString()}")


                    }.addOnFailureListener{
                        Log.w("Error", "Error updating Document", it)
                    }
            }
        }

        Spacer(Modifier.width(100.dp))

        val openDialog= remember {
            mutableStateOf(false)
        }
        if(openDialog.value){
            ShowAlertDialog(message = stringResource(id = R.string.sure) + "\n" +
                                    stringResource(id =R.string.action ), openDialog){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .delete()
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            openDialog.value=false

                            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                        }
                    }
            }
        }
        RoundedButton (label="Delete"){
            openDialog.value=true
        }
    }
}

@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit) {

    if (openDialog.value) {
        AlertDialog(
            icon = {
            },
            title = {
                Text(text ="Delete Book")
            },
            text = {
                Text(text = message)
            },
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onYesPressed.invoke()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}






fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG )
        .show()
}


@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading:Boolean= false,
    defaultValue: String= "Great Book!",
    onSearch:(String)-> Unit
){
    Column() {
        val textFieldValue= rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController=LocalSoftwareKeyboardController.current
        val valid=remember(textFieldValue){
            textFieldValue.value.trim().isNotEmpty()
        }
        InputField(
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue,
            labelId ="Enter your Thoughts" , enabled =true,
            onAction= KeyboardActions{
                if(!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            }
            )
    }
}


@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookItemId: String) {
    Row(modifier=Modifier){
        Spacer(modifier=Modifier.width(43.dp))
        if(bookInfo.data!=null){
            Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center)
            {
                CardListItem(book=bookInfo.data!!.first{mBook->
                    mBook.googleBookId==bookItemId

                }, onPressDetails={})
            }
        }
    }
}

@Composable
fun CardListItem(book: MBook,
                 onPressDetails: () -> Unit) {
    Card(modifier = Modifier
        .padding(
            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp
        )
        .clip(RoundedCornerShape(20.dp))
        .clickable { },
        elevation = CardDefaults.cardElevation(8.dp)) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(painter = rememberImagePainter(data = book.photoUrl.toString()),
                contentDescription = null ,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp
                        )
                    ))
            Column {
                Text(text = book.title.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)

                Text(text = book.authors.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp))

                Text(text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp))

            }

        }
    }

}


//@Composable
//fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
//    Card(modifier= Modifier
//        .padding(
//            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp
//        )
//        .clip(RoundedCornerShape(20.dp))
//        .clickable { },
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 8.dp
//        ) ) {
//        Row(modifier = Modifier, horizontalArrangement = Arrangement.Start){
//            Image(painter = rememberImagePainter(data = book.photoUrl.toString()),
//                contentDescription =null,
//                modifier = Modifier
//                    .height(100.dp)
//                    .width(120.dp)
//                    .padding(4.dp)
//                    .clip(
//                        RoundedCornerShape(
//                            topStart = 120.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp
//                        )
//                    ))
//            Column {
//                Text(text=book.title.toString(),
//                    style= MaterialTheme.typography.titleMedium,
//                    modifier= Modifier
//                        .padding(start = 8.dp, end = 8.dp)
//                        .width(120.dp),
//                    fontWeight = FontWeight.SemiBold,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Text(text =book.publishedDate.toString(),
//                    style=MaterialTheme.typography.bodySmall,
//                    modifier=Modifier.padding(start=8.dp,
//                        end=8.dp,top=0.dp, bottom=8.dp))
//            }
//        }
//
//    }
//}
