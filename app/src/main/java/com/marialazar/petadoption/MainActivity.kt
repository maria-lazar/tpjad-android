package com.marialazar.petadoption

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.marialazar.petadoption.api.Result
import com.marialazar.petadoption.api.Api
import com.marialazar.petadoption.api.MyApi
import com.marialazar.petadoption.core.TAG
import com.marialazar.petadoption.model.LoginResponse
import com.marialazar.petadoption.model.Team
import com.marialazar.petadoption.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class EventData(val payload: String)

class MainActivity : AppCompatActivity() {
    private var isActive = false;

    private suspend fun collectEvents() {
        Log.d("collectEvents", "start collecting events")
        while (isActive) {
            val event = RemoteDataSource.eventChannel.receive()
            val eventObject = Gson().fromJson(event, EventData::class.java)
            val message = eventObject.payload

            Log.d("WebSocket", "received $message")
        }
    }

    fun startListen() {
        RemoteDataSource.createWebSocket()
        isActive = true
        CoroutineScope(Dispatchers.Default).launch { collectEvents() }
    }

    fun stopListen() {
        Log.i(TAG, "stop listen")
        isActive = false;
        RemoteDataSource.destroyWebSocket()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
//        startListen()
        loginButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = MyApi.login(
                    User(
                        username_text.text.toString(),
                        password_text.text.toString()
                    )
                )
                if (result is Result.Success<LoginResponse>) {
                    Api.tokenInterceptor.token = result.data.token
                    Log.i(TAG, result.data.user.toString())
                    val teamsResult = MyApi.getAllTeams()
                    if (teamsResult is Result.Success<List<Team>>) {
                        teamsResult.data.forEach {
                            Log.i(TAG, it.toString())
                        }
                    }
                } else {
                    // bad credentials message
                    Log.i(TAG, "Invalid")
                }
            }
        }

        Log.i(TAG, "onCreate")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopListen()
    }
}