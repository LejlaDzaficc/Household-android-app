package com.example.household

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private var mBoundService: SocketService? = null
    private var isBound = false
    private val mConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBoundService = (service as SocketService.LocalBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBoundService = null
        }
    }

    private fun doBindService() {
        bindService(Intent(this@MainActivity, SocketService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        isBound = true
    }

    private fun doUnbindService() {
        if (isBound) {
            unbindService(mConnection)
            isBound = false
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this@MainActivity, SocketService::class.java))
        doBindService()
        setContentView(R.layout.loading)
        val preferences = getSharedPreferences("database", Context.MODE_APPEND)
        val status = preferences.getString("status", null)
        if(status != null) {
            val intent = Intent(this, JoinToHousehold::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        else
            setContentView(R.layout.activity_main)

        setupListeners()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    fun setupListeners(){
        createNewHousehold.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        joinToHousehold.setOnClickListener {
            val intent = Intent(this, JoinToHousehold::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
    }
}