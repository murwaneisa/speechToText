package com.androidcourse.se_lab2

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import android.Manifest
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    private fun onStartRecording() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            speechRecognizer.startListening(speechRecognizerIntent)
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    private fun onStopRecording() {
        speechRecognizer.stopListening()
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 1
    }
    private fun handleSpokenText(text: String) {
        val lowercaseText= text.lowercase()
        when {
            "door" in lowercaseText && "open" in lowercaseText -> updateState("door", true)
            "door" in lowercaseText && ("close" in lowercaseText || "shut" in lowercaseText) -> updateState("door", false)

            "window" in lowercaseText && "open" in lowercaseText -> updateState("window", true)
            "window" in lowercaseText && ("close" in lowercaseText || "shut" in lowercaseText) -> updateState("window", false)

            "light" in lowercaseText && "on" in lowercaseText -> updateState("light", true)
            "light" in lowercaseText && ("close" in lowercaseText || "off" in lowercaseText) -> updateState("light", false)
        }
    }

    private fun updateState(node: String, state: Boolean) {
        database.child(node).setValue(state)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        Firebase.initialize(this)
        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {}
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        Log.d("SpeechRecognition", "Recognized Words: $text") // Log the recognized words
                        handleSpokenText(text)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        setContent {
            SElab2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isRecording = remember { mutableStateOf(false) }
                    ScaffoldExample(database,isRecording = isRecording,
                        onStartRecording = ::onStartRecording,
                        onStopRecording = ::onStopRecording)
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
fun ScaffoldExample(database: DatabaseReference, isRecording: MutableState<Boolean>,
                    onStartRecording: () -> Unit,
                    onStopRecording: () -> Unit) {
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
            BottomAppBar {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // Toggle the recording state
                        isRecording.value = !isRecording.value
                        if (isRecording.value) {
                            onStartRecording()
                        } else {
                            onStopRecording()
                        }
                    }
                ) {
                    Text(if (isRecording.value) "Tap Once More to Stop" else "Tap to Record")
                }
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



