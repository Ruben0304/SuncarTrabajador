//package com.suncar.suncartrabajador.ui.old.pages
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import com.suncar.suncartrabajador.ui.old.components.LoadingScreen
//import com.suncar.suncartrabajador.ui.old.viewmodels.H1114ViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@Composable
//fun H1114FormScreen(
//    modifier: Modifier = Modifier,
//    viewModel: H1114ViewModel = viewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    Box(modifier = modifier.fillMaxSize()) {
//        // 1. Place your main content (LoadingScreen or H1114FormContent) first.
//        // This ensures the content fills the space and draws underneath the button.
//        if (uiState.isLoading) {
//            LoadingScreen()
//        } else {
//            // It's important to pass a modifier that allows the content to fill the space
//            // but also ensures it respects any padding needed for the close button,
//            // or that the content itself has proper padding.
//            // For now, let's assume H1114FormContent handles its own padding or scrolling properly.
//            H1114FormContent(
//                uiState = uiState,
//                viewModel = viewModel,
//                modifier = Modifier.fillMaxSize() // Make sure your content fills the box
//            )
//        }
//
//
//    }
//}