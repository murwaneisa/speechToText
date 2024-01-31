package com.androidcourse.se_lab2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        Firebase.initialize(this)

        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference

        setContent {
            SElab2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScaffoldExample(database)
                }
            }
        }
    }
}

@Composable
fun SwitchWithIconExample(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = { newChecked ->
            onCheckedChange(newChecked)
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample(database: DatabaseReference) {
    // Read and write values for "door," "window," and "light"
    var doorSwitchState by remember { mutableStateOf(false) }
    var windowSwitchState by remember { mutableStateOf(false) }
    var lightSwitchState by remember { mutableStateOf(false) }
    // Read initial values from Firebase
    database.child("door").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val doorValue = dataSnapshot.getValue(Boolean::class.java) ?: false
            Log.d("FirebaseLab2", "Door value in the fetch: $doorValue")
            doorSwitchState = doorValue
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Handle any errors
        }
    })
    database.child("window").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val doorValue = dataSnapshot.getValue(Boolean::class.java) ?: false
            Log.d("FirebaseLab2", "Door value in the fetch: $doorValue")
            windowSwitchState = doorValue
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Handle any errors
        }
    })
    database.child("light").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val doorValue = dataSnapshot.getValue(Boolean::class.java) ?: false
            Log.d("FirebaseLab2", "Door value in the fetch: $doorValue")
            lightSwitchState = doorValue
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Handle any errors
        }
    })

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
                SwitchWithIconExample(doorSwitchState) { newChecked ->
                    doorSwitchState = newChecked
                }
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
                SwitchWithIconExample(windowSwitchState) { newChecked ->
                    windowSwitchState = newChecked
                }
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
                SwitchWithIconExample(lightSwitchState) { newChecked ->
                    doorSwitchState = newChecked
                }
            }
        }
    }
}



