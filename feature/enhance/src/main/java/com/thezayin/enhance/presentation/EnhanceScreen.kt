package com.thezayin.enhance.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.thezayin.enhance.presentation.event.EnhanceEvent
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhanceScreen(
    viewModel: EnhanceViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Enhancement") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Unknown Error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.enhancedImage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = state.enhancedImage!!.asImageBitmap(),
                            contentDescription = "Enhanced Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        EnhancementButtons(
                            onEnhancePlus = { viewModel.onEvent(EnhanceEvent.EnhancePlus) },
                            onEnhancePro = { viewModel.onEvent(EnhanceEvent.EnhancePro) }
                        )
                    }
                }

                state.enhancePlusImage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = state.enhancePlusImage!!.asImageBitmap(),
                            contentDescription = "Enhance Plus Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        EnhancementButtons(
                            onEnhancePlus = { /* Already in Plus, disable button or hide */ },
                            onEnhancePro = { viewModel.onEvent(EnhanceEvent.EnhancePro) }
                        )
                    }
                }

                state.enhanceProImage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = state.enhanceProImage!!.asImageBitmap(),
                            contentDescription = "Enhance Pro Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        EnhancementButtons(
                            onEnhancePlus = { viewModel.onEvent(EnhanceEvent.EnhancePlus) },
                            onEnhancePro = { /* Already in Pro, disable button or hide */ }
                        )
                    }
                }

                state.deblurImage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = state.deblurImage!!.asImageBitmap(),
                            contentDescription = "Deblur Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DeblurButton(
                            onDeblur = { viewModel.onEvent(EnhanceEvent.Deblur) }
                        )
                    }
                }

                else -> {
                    Text(
                        text = "No Image Loaded",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancementButtons(
    onEnhancePlus: () -> Unit,
    onEnhancePro: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onEnhancePlus,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("Enhance Plus")
        }
        Button(
            onClick = onEnhancePro,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("Enhance Pro")
        }
    }
}

@Composable
fun DeblurButton(
    onDeblur: () -> Unit
) {
    Button(
        onClick = onDeblur,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text("Deblur Image")
    }
}
