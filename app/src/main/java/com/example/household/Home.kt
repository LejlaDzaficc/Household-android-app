package com.example.household

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log.d
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.home.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Home: AppCompatActivity(), ServiceCallbacks {

    private var mBoundService: SocketService? = null
    private var isBound = false
    private var messageAdapter: MessageAdapter? = null
    private var messagesView: ListView? = null

    private val mConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBoundService = (service as SocketService.LocalBinder).service
            mBoundService?.setServiceCallbacks(this@Home)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBoundService = null
            mBoundService?.setServiceCallbacks(null)
        }
    }

    private fun doBindService() {
        bindService(Intent(this@Home, SocketService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        isBound = true
    }

    private fun doUnbindService() {
        if (isBound){
            unbindService(mConnection)
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBindService()
        setContentView(R.layout.home)
        messageAdapter = MessageAdapter(this)
        messagesView = findViewById(R.id.messages_view) as ListView
        messagesView?.setAdapter(messageAdapter)
        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
        mBoundService?.stopSelf()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun showInviteDialog(groupID: String){
        var dialog = InviteFamilyMemberDialog(groupID);
        dialog.show(supportFragmentManager, "dialog")
    }

    @SuppressLint("WrongConstant")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.inviteFamilyMember){
            val preferences = getSharedPreferences("database", Context.MODE_APPEND)
            val currentGroupID = preferences.getString("currentGroupID", null)
            if (currentGroupID != null) {
                showInviteDialog(currentGroupID)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupListeners(){
        sendMessageButton.setOnClickListener {
            val messageContent = sendMessageTextField.text.toString()
            if(isBound && messageContent != "") {
                val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
                val currentUser = preferences.getString("currentUser", "This value does not exist")
                val currentGroupID = preferences.getString("currentGroupID", "This value does not exist")
                val currentTime = Calendar.getInstance().getTime()
                val simpleDateFormat = SimpleDateFormat("dd-MM-YYYY HH:mm:ss")
                val timestamp  = simpleDateFormat.format(currentTime)

                val jsonObj = JSONObject()
                jsonObj.put("command", "chatMessage")
                jsonObj.put("groupID", currentGroupID)
                jsonObj.put("clientName", currentUser)
                jsonObj.put("timestamp", timestamp)
                jsonObj.put("messageContent", messageContent)
                mBoundService?.sendMessage(jsonObj)
                val message = Message(messageContent, "You", timestamp, true)
                showMessageOnUI(message)
                sendMessageTextField.text.clear()
            }
        }
    }

    fun showMessageOnUI(message: Message){
        runOnUiThread {
            messageAdapter?.add(message)
            // Scroll the ListView to the last added element
            messagesView?.setSelection(messagesView!!.count - 1)
        }
    }

    override fun receiveMessage(msg: String?) {
        val jsonObj = JSONObject(msg)
        val command = jsonObj.getString("command")
        if(command == "chatMessage")
        {
            val clientName = jsonObj.getString("clientName")
            val timestamp = jsonObj.getString("timestamp")
            val messageContent = jsonObj.getString("messageContent")
            val message = Message(messageContent, clientName, timestamp, false)
            showMessageOnUI(message)
        }
    }
}
