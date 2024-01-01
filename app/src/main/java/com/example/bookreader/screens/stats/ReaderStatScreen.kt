package com.example.bookreader.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookreader.components.ReaderAppBar
import com.example.bookreader.model.ItemsItem
import com.example.bookreader.model.MBook
import com.example.bookreader.navigation.ReaderScreens
import com.example.bookreader.screens.home.HomeScreenViewModel
import com.example.bookreader.screens.search.BookRow
import com.example.bookreader.ui.theme.PinkInst
import com.example.bookreader.utils.formatDate
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun ReaderStatScreen(navController: NavController, viewModel: HomeScreenViewModel){
    var books: List<MBook>
    val currentUser=FirebaseAuth.getInstance().currentUser
    
    Scaffold(
        topBar = {
            ReaderAppBar(title = "Book Stats", 
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController){
                navController.popBackStack()
            }
        },
    ){
        Surface(modifier = Modifier.padding(it)) {
            books=if(!viewModel.data.value.data.isNullOrEmpty()){
                viewModel.data.value.data!!.filter { mBook: MBook -> 
                    mBook.userId==currentUser?.uid
                }
            }else{
                emptyList()
            }
            Column {
                Row {
                    Box(modifier= Modifier
                        .size(45.dp)
                        .padding(2.dp)){
                            Icon(imageVector=Icons.Sharp.Person,
                                contentDescription="icon")
                        }
                    Text(text = "Hi, ${currentUser?.email.toString().split("@")[0].uppercase(Locale.getDefault())}")
                }
                Card (
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape= CircleShape, elevation = CardDefaults.cardElevation(5.dp)){
                    val readBooksList: List<MBook> =if(!viewModel.data.value.data.isNullOrEmpty()){
                        books.filter{mBook: MBook ->  
                            mBook.userId==currentUser?.uid && mBook.finishedReading!=null
                        }
                    }else{
                        emptyList()
                    }
                    
                    
                    val readingBooks=books.filter{mBook: MBook ->  
                        mBook.startedReading != null && mBook.finishedReading==null
                    }
                    Column(modifier=Modifier.padding(start=25.dp, top=4.dp,bottom=4.dp),
                            horizontalAlignment = Alignment.Start) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.headlineSmall)
                        Divider()
                        Text(text="You're Reading: ${readingBooks.size} books")
                        Text(text="You've Read: ${readBooksList.size} books")
                    }
                }
                if(viewModel.data.value.loading==true){
                    LinearProgressIndicator()
                }else{
                    Divider()
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                                contentPadding = PaddingValues(16.dp)
                    ){//filter books by finished ones
                        val readBooks: List<MBook> =if(!viewModel.data.value.data.isNullOrEmpty()){
                            viewModel.data.value.data!!.filter{mBook: MBook ->
                                mBook.userId==currentUser?.uid && mBook.finishedReading !=null
                            }
                        }else{
                            emptyList()
                        }
                        items(items=readBooks) {book->
                            BookRowStats(book = book  )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun BookRowStats(book: MBook) {
    Card(
        modifier = Modifier
            .clickable {
//                navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
            }
            .fillMaxWidth()
            .height(100.dp)
            .padding(3.dp),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(7.dp),
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            val imageURL: String = if(book.photoUrl.toString().isEmpty()){
                "https://hips.hearstapps.com/hmg-prod/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg"}
            else{

                book.photoUrl.toString()
            }
            Image(
                painter = rememberImagePainter(data = imageURL),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )
            Column() {
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    if(book.rating!! >= 4){
                        Spacer(Modifier.fillMaxWidth(0.8f))
                        Icon(imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint= PinkInst,
                        )
                    }else{
                        Box {}
                    }
                }

                Text(
                    text = "Author: ${book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Started: ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Finished: ${formatDate(book.finishedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}