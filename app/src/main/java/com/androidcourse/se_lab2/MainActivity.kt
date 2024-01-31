package com.androidcourse.se_lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.androidcourse.se_lab2.ui.theme.SElab2Theme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SElab2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    ScaffoldExample()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchWithImageExample(checkedImage: Int, uncheckedImage: Int) {
    var checked by remember { mutableStateOf(true) }
    Row(verticalAlignment = Alignment.CenterVertically) {
    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        },
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )

            }
        } else {
            null
        }
    )
    Image(
        painter = painterResource(id = if (checked) checkedImage else uncheckedImage),
        contentDescription = null, // Provide appropriate descriptions for accessibility
        modifier = Modifier.size(48.dp) // Adjust size as needed
    )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors( // Corrected line
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Speech to text app ")
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "footer ",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {  }) {
                Icon(Icons.Default.Face, contentDescription = "Speech")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text =
                """ speak to command the following buttons""".trimIndent(),
            )
            // door
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Door")
                // Use the new composable with image resources for the door
                SwitchWithImageExample(
                    checkedImage = R.drawable.door_open,
                    uncheckedImage = R.drawable.door_shut
                )
            }
            //window
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Window")
                SwitchWithImageExample(
                    checkedImage = R.drawable.window_open,
                    uncheckedImage =R.drawable.window_shut
                )
            }
            //light
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "light")
                SwitchWithImageExample(
                    checkedImage = R.drawable.light_on,
                    uncheckedImage = R.drawable.light_off
                )
            }
        }
    }
}



