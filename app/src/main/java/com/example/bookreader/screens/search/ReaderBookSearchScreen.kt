package com.example.bookreader.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookreader.components.InputField
import com.example.bookreader.components.ListCard
import com.example.bookreader.components.ReaderAppBar
import com.example.bookreader.model.ItemsItem
import com.example.bookreader.model.MBook
import com.example.bookreader.navigation.ReaderScreens
import io.grpc.InternalChannelz.id
import org.checkerframework.checker.units.qual.s



@Composable
fun SearchScreen(navController: NavController,
                 viewModel: BooksSearchViewModel= hiltViewModel()
               ){
    Scaffold(topBar={
        ReaderAppBar(title = "Search Books",
            icon= Icons.Default.ArrowBack, showProfile = false, navController =navController ){
       // navController.popBackStack()
         navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }){
        Surface(modifier = Modifier.padding(it)){
            Column (){
                SearchForm(modifier= Modifier
                    .fillMaxWidth()
                    .padding(16.dp)){searchQuery->

                    viewModel.searchBooks(query = searchQuery)
                }
                Spacer(modifier=Modifier.padding(13.dp))

                BookList(navController)

            }
        }
    }
}

@Composable
fun BookList(navController: NavController,
             viewModel: BooksSearchViewModel= hiltViewModel()) {
            val listOfBooks= viewModel.list
        if(viewModel.isloading){
            Row(horizontalArrangement = Arrangement.SpaceBetween ) {
                Text(text = "Loading..")
                LinearProgressIndicator()
            }
        }else{
            LazyColumn(modifier= Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)){
                items(items=listOfBooks){book->
                    BookRow(book, navController)

                }
            }
        }

}

@Composable
fun BookRow(book: ItemsItem, navController: NavController) {
    Card(
        modifier = Modifier
            .clickable {

                navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
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
            val imageURL: String = if(book.volumeInfo.imageLinks.smallThumbnail.isEmpty()){
                "https://hips.hearstapps.com/hmg-prod/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg"}
            else{

               book.volumeInfo.imageLinks.smallThumbnail
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
                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Author: ${book.volumeInfo.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Date: ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Category: ${book.volumeInfo.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(modifier :Modifier= Modifier, loading: Boolean =false,
               hint: String= "Search",
               onSearch:(String)-> Unit={}){
    Column {
        val searchQueryState=rememberSaveable{mutableStateOf("")}
        val keyboardController=LocalSoftwareKeyboardController.current
        val valid= remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }
        InputField(valueState = searchQueryState, labelId ="Search" , enabled =true ,
            onAction = KeyboardActions{
                if(!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value=""
                keyboardController?.hide()
            }
        )

    }
}