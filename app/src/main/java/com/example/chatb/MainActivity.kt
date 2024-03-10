package com.example.chatb

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.InsertPhoto
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.chatb.ui.theme.B00
import com.example.chatb.ui.theme.B01
import com.example.chatb.ui.theme.B10
import com.example.chatb.ui.theme.ChatBTheme
import com.example.chatb.ui.theme.W_main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {


    private val uriState = MutableStateFlow("")
    private val imgSelector =
        registerForActivityResult<PickVisualMediaRequest, Uri>(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                uriState.update { uri.toString() }
            }
        }



    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),

                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            TopBar(navController = navController)
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "chat"
                        ) {
                            composable("chat") {
                                MainScreen(
                                    navController = navController,
                                    paddingValues = paddingValues
                                )
                            }
                            composable("info") {
                                InfoScreen()
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun TopBar(navController: NavController) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(B10)
                .height(48.dp)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.popBackStack(
                                "chat",
                                inclusive = false
                            )
                        },
                    text = stringResource(id = R.string.app_name),
                    color = White,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 18.sp,
                )
                IconButton(
                    onClick = { navController.navigate("info") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "info",
                        tint = White
                    )
                }
            }
        }
    }






    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(paddingValues: PaddingValues, navController: NavHostController) {
        val chaViewModel = viewModel<ChatViewM>()
        val chatState = chaViewModel.chatState.collectAsState().value

        val bitmap = getBitmap()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = W_main)
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatlist) { index, chat ->
                    if (!chat.isFromBot) {
                        UserMsg(
                            prompt = chat.prompt,
                            bitmap = chat.bitmap
                        )
                    } else {
                        BotMsg(response = chat.prompt)
                    }
                }
            }

            ChatInput(
                prompt = chatState.prompt,
                onPromptChange = { chaViewModel.onEvent(ChatUIE.UpdatePrompt(it)) },
                onSendClick = { chaViewModel.onEvent(ChatUIE.SendPrompt(chatState.prompt, bitmap)) },
                bitmap = bitmap
            )
        }
    }


    @Composable
    fun ChatInput(
        prompt: String,
        onPromptChange: (String) -> Unit,
        onSendClick: () -> Unit,
        bitmap: Bitmap?
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageInput(bitmap = bitmap)
            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = prompt,
                onValueChange = onPromptChange,
                label = { Text(text = stringResource(R.string.send_msg)) },
            )

            SendButton(onSendClick)
        }
    }


    @Composable
    fun ImageInput(bitmap: Bitmap?) {
        Column {
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .size(36.dp)
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }

            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        imgSelector.launch(
                            PickVisualMediaRequest
                                .Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                .build()
                        )
                    },
                imageVector = Icons.Rounded.InsertPhoto,
                contentDescription = "",
                tint = B10
            )
        }
    }
    
    

    @Composable
    fun UserMsg(prompt: String, bitmap: Bitmap?) {

        Column(
            modifier = Modifier.padding(start = 84.dp, bottom = 16.dp)
        ) {

            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }


            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = B00)
                    .padding(12.dp),


                text = prompt,
                fontSize = 16.sp,

            )

        }
    }


    
    @Composable
    fun SendButton(onSendClicked: () -> Unit) {
        Icon(
            modifier = Modifier
                .size(36.dp)
                .clickable { onSendClicked() },
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = "",
            tint = B10
        )
    }



    @Composable
    fun BotMsg(response: String) {

        Column(
            modifier = Modifier.padding(end = 100.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(B01)
                    .padding(12.dp),


                text = response,
                fontSize = 16.sp,

            )

        }
    }

    

    @Composable
    private fun getBitmap(): Bitmap? {
        val uri = uriState.collectAsState().value

        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .build()
        ).state

        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }

        return null
    }


    

    
}

