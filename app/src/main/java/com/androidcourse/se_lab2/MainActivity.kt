package com.androidcourse.se_lab2

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.initialize

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchWithImageExample(checked: Boolean, onCheckedChange: (Boolean) -> Unit,checkedImage: Int, uncheckedImage: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
    Image(
        painter = painterResource(id = if (checked) checkedImage else uncheckedImage),
        contentDescription = null, // Provide appropriate descriptions for accessibility
        modifier = Modifier.size(48.dp) // Adjust size as needed
    )
    }
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
            Log.d("FirebaseLab2", "Window value in the fetch: $doorValue")
            windowSwitchState = doorValue
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Handle any errors
        }
    })
    database.child("light").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val doorValue = dataSnapshot.getValue(Boolean::class.java) ?: false
            Log.d("FirebaseLab2", "Light value in the fetch: $doorValue")
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
                // Use the new composable with image resources for the door
                SwitchWithImageExample(
                    doorSwitchState,
                    { newChecked ->
                        doorSwitchState = newChecked
                        database.child("door").setValue(newChecked)
                    },
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
                    windowSwitchState,
                    { newChecked ->
                        windowSwitchState = newChecked
                        database.child("window").setValue(newChecked)
                    },
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
                    lightSwitchState,
                    { newChecked ->
                       lightSwitchState = newChecked
                        database.child("light").setValue(newChecked)
                    },
                    checkedImage = R.drawable.light_on,
                    uncheckedImage = R.drawable.light_off
                )
            }
        }
    }
}



