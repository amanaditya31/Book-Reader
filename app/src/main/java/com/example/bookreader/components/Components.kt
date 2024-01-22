@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookreader.components

import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookreader.R
import com.example.bookreader.model.MBook
import com.example.bookreader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ReaderLogo(modifier: Modifier = Modifier) {
    Text(
        text = "Book Reader", modifier = modifier.padding(16.dp),
        color = Color.Red.copy(alpha = 0.5f), style = MaterialTheme.typography.displayMedium
    )
}


@Composable
fun EmailInput(modifier: Modifier=Modifier,
               emailState: MutableState<String>,
               labelId: String="Email",
               enabled: Boolean=true,
               imeAction: ImeAction = ImeAction.Next,
               onAction: KeyboardActions = KeyboardActions.Default

){
    InputField(modifier=modifier,
        valueState = emailState,
        labelId=labelId,
        enabled=enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction
    )
}

@Composable
fun InputField(
    modifier: Modifier=Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean=true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default

) {
    OutlinedTextField(
        value=(valueState.value),
        onValueChange ={ valueState.value = it},
        label ={ Text(text=labelId)},
        singleLine = isSingleLine,
        textStyle = TextStyle(fontSize = 18.sp,
            color=MaterialTheme.colorScheme.onBackground),
        modifier= Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled=enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction,
    )
}


@Composable
fun PasswordInput(modifier: Modifier,
                  passwordState: MutableState<String>,
                  labelId: String,
                  enabled: Boolean,
                  passwordVisibility: MutableState<Boolean>,
                  onAction: KeyboardActions=KeyboardActions.Default,
                  imeAction: ImeAction=ImeAction.Done) {
    val visualTransformation=if(passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()

    OutlinedTextField(value = passwordState.value,
        onValueChange ={
            passwordState.value=it} ,
        label={Text(text=labelId)},
        singleLine=true,
        textStyle = TextStyle(fontSize=18.sp, color=MaterialTheme.colorScheme.onBackground),
        modifier= modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled=enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        visualTransformation = visualTransformation, trailingIcon = {PasswordVisibility(passwordVisibility)},
        keyboardActions = onAction
    )

}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible=passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value= !visible}) {
        Icons.Default.Close
    }
}

@Composable
fun TitleSection(modifier: Modifier=Modifier,label: String){
    Surface(modifier= modifier.padding(start=8.dp, top=1.dp)){
        Column{
            Text(text=label, fontSize = 22.sp,
                fontStyle= FontStyle.Normal ,
                textAlign= TextAlign.Left
            )
        }
    }

}

@Composable
fun ReaderAppBar(title: String, icon: ImageVector?=null,
                 showProfile:Boolean=true,
                 navController: NavController,
                 onBackArrowClicked: ()->Unit={}
){
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically){
            if(showProfile){
                Image(painter = painterResource(id = R.drawable.icons8_book_100), contentDescription ="icon")
            }

            if(icon!=null){
                Icon(imageVector = icon, contentDescription = "Arrow Back",
                    tint = Color.Red.copy(alpha=0.7f), modifier = Modifier.clickable { onBackArrowClicked.invoke() })
            }
            Text(text = title, modifier = Modifier.padding(start=20.dp), color = Color.Red.copy(alpha = 0.7f),
                style= TextStyle( fontWeight = FontWeight.Bold, fontSize = 25.sp))
            Spacer(modifier=Modifier.width(150.dp))

        }

    }, actions = { IconButton(onClick = {
        FirebaseAuth.getInstance().signOut().run {navController.navigate(ReaderScreens.LoginScreen.name)}
    }) {
        if(showProfile)Row() {
            Image(
                painter = painterResource(id = R.drawable.icons8_logout_96),
                contentDescription = "icon"
            )
        }else Box{}

    }},colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor = MaterialTheme.colorScheme.primary,
    )
    )
}

@Composable
fun FABContent(onTap: () -> Unit) {
    FloatingActionButton(onClick = {onTap() },
        shape= RoundedCornerShape(50.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Icon(painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription="Add a Book",
            tint=MaterialTheme.colorScheme.onPrimaryContainer)
    }
}


@Composable
fun BookRating(score: Double=4.5) {
    Surface(modifier = Modifier
        .height(70.dp)
        .padding(4.dp),
        shape = RoundedCornerShape(56.dp), shadowElevation = 6.dp, tonalElevation = 6.dp, color = Color.White
    ){
        Column(modifier=Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.StarBorder,
                contentDescription = "Start", modifier=Modifier.padding(3.dp))
            Text(text=score.toString(), style=MaterialTheme.typography.titleSmall)
        }
    }
}



@Composable
fun ListCard(book: MBook,
             onPressDetails: (String)-> Unit={}){

    val context= LocalContext.current
    val resources=context.resources
    val displayMetrics=resources.displayMetrics

    val screenWidth=displayMetrics.widthPixels/displayMetrics.density
    val spacing=10.dp

    ElevatedCard(shape= RoundedCornerShape(25.dp),colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ), elevation = CardDefaults.cardElevation(
        defaultElevation = 6.dp
    ),
        modifier= Modifier
            .padding(16.dp)
            .height(242.dp)
            .width(202.dp)
            .clickable { onPressDetails.invoke(book.title.toString()) }
    ){
        Column(modifier= Modifier.width(screenWidth.dp - (spacing*2)),
            horizontalAlignment = Alignment.Start){
            Row(horizontalArrangement = Arrangement.Center){
                Image(painter= rememberImagePainter(data=book.photoUrl.toString()), contentDescription = "Book Image",
                    modifier= Modifier
                        .height(140.dp)
                        .width(100.dp)
                        .padding(4.dp))
                Spacer(modifier=Modifier.width(50.dp))
                Column(modifier=Modifier.padding(top=25.dp), verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.FavoriteBorder, contentDescription = "Fav Icon", modifier=Modifier.padding(1.dp))

                    BookRating(score=book.rating!!)
                }
            }
            Text(text=book.title.toString(), modifier= Modifier.padding(6.dp),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium
            )
            Text(text=book.authors.toString(), modifier= Modifier.padding(start=6.dp),
                style = MaterialTheme.typography.titleSmall
            )

        }
        val isStartedReading=remember{
            mutableStateOf(false)
        }
        Row(modifier=Modifier.fillMaxSize(),horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom){
            isStartedReading.value=book.startedReading!=null
            RoundedButton(label=if(isStartedReading.value)"Reading" else "To Read", radius= 55 , onPress = {})
        }

    }
}

@Composable
fun RoundedButton(label: String="Reading", radius:Int=29,onPress:()-> Unit){
    Surface(modifier = Modifier.
    clip(RoundedCornerShape(bottomEndPercent = radius, topStartPercent = radius)),
        color = MaterialTheme.colorScheme.onPrimaryContainer) {
        Column(modifier= Modifier
            .width(90.dp)
            .heightIn(40.dp)
            .clickable { onPress.invoke() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = label, style = MaterialTheme.typography.titleSmall, color = Color.White)
        }
    }

}


//Rating Bar
@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onPressRating: (Int) -> Unit
) {
    var ratingState by remember {
        mutableStateOf(rating)
    }

    var selected by remember {
        mutableStateOf(false)
    }
    val size by animateDpAsState(
        targetValue = if (selected) 42.dp else 34.dp,
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier.width(280.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_star_24),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                onPressRating(i)
                                ratingState = i
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xFFFFD700) else Color(0xFFA2ADB1)
            )
        }
    }
}
