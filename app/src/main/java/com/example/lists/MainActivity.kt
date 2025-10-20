package com.example.lists

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lists.ComposeActivity.Companion.EXTRA_TEXT

class MainActivity : ComponentActivity() {
    
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val newText = data?.getStringExtra(EXTRA_TEXT)
                if (!newText.isNullOrEmpty()) {
                    addNewChiuit(newText)
                }
            }
        }

    private var onAddChiuit: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            HomeScreen(
                onComposeClick = { composeChiuit() },
                onShareClick = { shareChiuit(it) },
                onAddChiuitCallback = { callback -> onAddChiuit = callback }
            )
        }
    }

    @Composable
    private fun HomeScreen(
        onComposeClick: () -> Unit,
        onShareClick: (String) -> Unit,
        onAddChiuitCallback: ((String) -> Unit) -> Unit
    ) {
        var chiuitList by remember { mutableStateOf(ChiuitStore.getAllData()) }
        
        // Set up the callback for adding new chiuits
        onAddChiuitCallback { newText ->
            chiuitList = chiuitList + Chiuit(newText)
        }
        
        Surface(color = Color.White) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chiuitList) { chiuit ->
                        ChiuitListItem(
                            chiuit = chiuit,
                            onShareClick = onShareClick
                        )
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = onComposeClick,
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(R.string.edit_action_icon_content_description)
                    )
                }
            }
        }
    }

    @Composable
    private fun ChiuitListItem(
        chiuit: Chiuit,
        onShareClick: (String) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(8.dp),
                    text = chiuit.description,
                )
                Button(
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(8.dp),
                    onClick = { onShareClick(chiuit.description) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        stringResource(R.string.send_action_icon_content_description)
                    )
                }
            }
        }
    }

    /*
    Defines text sharing/sending *implicit* intent, opens the application chooser menu,
    and starts a new activity which supports sharing/sending text.
     */
    private fun shareChiuit(text: String) {
        val sendIntent = Intent().apply {
            // Configure intent for text sharing
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val intentChooser = Intent.createChooser(sendIntent, "")
        startActivity(intentChooser)
    }

    /*
    Defines an *explicit* intent which will be used to start ComposeActivity.
     */
    private fun composeChiuit() {
        val intent = Intent(this, ComposeActivity::class.java).apply {
            // Attach extra text data
            putExtra(Intent.EXTRA_TEXT, "")
            type = "text/plain"
        }

        resultLauncher.launch(intent)
    }

    private fun addNewChiuit(text: String) {
        onAddChiuit?.invoke(text)
    }

    @Preview(showBackground = true)
    @Composable
    private fun DefaultPreview() {
        HomeScreen(
            onComposeClick = {},
            onShareClick = {},
            onAddChiuitCallback = {}
        )
    }
}