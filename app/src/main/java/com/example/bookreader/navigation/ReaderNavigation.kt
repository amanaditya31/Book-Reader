package com.example.bookreader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookreader.screens.ReaderSplashScreen
import com.example.bookreader.screens.details.BookDetailsScreen
import com.example.bookreader.screens.home.Home
import com.example.bookreader.screens.home.HomeScreenViewModel
import com.example.bookreader.screens.login.ReaderLoginScreen
import com.example.bookreader.screens.search.BooksSearchViewModel
import com.example.bookreader.screens.search.SearchScreen
import com.example.bookreader.screens.stats.ReaderStatScreen
import com.example.bookreader.screens.update.BookUpdateScreen


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReaderNavigation() {
    val navController= rememberNavController()

    NavHost(navController =navController , startDestination=ReaderScreens.SplashScreen.name){
        composable(ReaderScreens.SplashScreen.name){
            ReaderSplashScreen(navController=navController)
        }

        composable(ReaderScreens.ReaderHomeScreen.name){
            val homeViewModel= hiltViewModel<HomeScreenViewModel>()
            Home(navController=navController, viewModel=homeViewModel)
        }

        val detailName=ReaderScreens.DetailScreen.name
        composable("$detailName/{bookId}", arguments = listOf(navArgument("bookId"){
            type= NavType.StringType
        })){backStackEntry->
            backStackEntry.arguments?.getString("bookId").let{
                BookDetailsScreen(navController = navController, bookId = it.toString())
            }

        }


        composable(ReaderScreens.LoginScreen.name){
            ReaderLoginScreen(navController = navController)
        }
        composable(ReaderScreens.SearchScreen.name){
            val searchViewModel= hiltViewModel<BooksSearchViewModel>()
            SearchScreen(navController = navController, viewModel = searchViewModel)
        }
        composable(ReaderScreens.ReaderStatsScreen.name){
            val homeViewModel= hiltViewModel<HomeScreenViewModel>()
            ReaderStatScreen(navController = navController, viewModel=homeViewModel)
        }

        val updateName=ReaderScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}", arguments= listOf(navArgument("bookItemId"){
            type= NavType.StringType
        })){backStackEntry->
            backStackEntry.arguments?.getString("bookItemId").let {
                BookUpdateScreen(navController = navController, bookItemId=it.toString())
            }
        }
    }

}