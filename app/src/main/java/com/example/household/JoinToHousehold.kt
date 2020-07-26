package com.example.household

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.join_to_household.*
import org.json.JSONObject
import java.lang.Thread.sleep

class JoinToHousehold: AppCompatActivity(), ServiceCallbacks {

    private var mBoundService: SocketService? = null
    private var isBound = false

    private val mConnection: ServiceConnection = object : ServiceConnection {

        @SuppressLint("WrongConstant")
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBoundService = (service as SocketService.LocalBinder).service
            mBoundService?.setServiceCallbacks(this@JoinToHousehold)

            val preferences = getSharedPreferences("database", Context.MODE_APPEND)
            val status = preferences.getString("status", null)
            if(status != null && isBound) {
                val groupID = preferences.getString("currentGroupID", "This value does not exist")
                sendJoinToHouseHoldMessage(groupID.toString())
                sleep(1000) // used for nicer transition
            }
            else {
                setContentView(R.layout.join_to_household)
                setupListener()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBoundService = null
            mBoundService?.setServiceCallbacks(null)
        }
    }

    private fun doBindService() {
        bindService(Intent(this@JoinToHousehold, SocketService::class.java), mConnection, Context.BIND_AUTO_CREATE)
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
        doBindService()
        setContentView(R.layout.loading)
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    fun sendJoinToHouseHoldMessage(groupID: String){
        val jsonObj = JSONObject()
        jsonObj.put("command", "joinToHousehold");
        jsonObj.put("groupID", groupID);
        mBoundService!!.sendMessage(jsonObj)
    }

    fun displayInputErrorForGroupID(reason: String){
        groupIDError.text = reason
        groupIDError.setTextColor(Color.RED)
        groupIDJoinToHousehold.setBackgroundResource(R.drawable.error_outline);
    }

    fun displayInputErrorForUsername(reason: String){
        usernameError.text = reason
        usernameError.setTextColor(Color.RED)
        usernameJoinToHousehold.setBackgroundResource(R.drawable.error_outline);
    }

    fun clearInputErrors(){
        groupIDError.text = ""
        groupIDJoinToHousehold.setBackgroundResource(0)
        usernameError.text = ""
        usernameJoinToHousehold.setBackgroundResource(0)
    }

    fun saveStatusInPreferences(status: String){
        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
        val myEdit = preferences.edit()
        myEdit.putString("status", status)
        myEdit.commit()
    }

    fun saveUsernameAndGroupIDInPreferences(user: String, groupID: String){
        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
        val myEdit = preferences.edit()
        myEdit.putString("currentUser", user)
        myEdit.putString("currentGroupID", groupID)
        myEdit.commit()
    }

    override fun receiveMessage(msg: String?) {
        val jsonObj = JSONObject(msg)
        val command: String = jsonObj.getString("command")
        if(command == "joinToHousehold"){
            val responseStatus = jsonObj.getString("status")
            if(responseStatus == "accepted"){
                saveStatusInPreferences(responseStatus)
                runOnUiThread {
                    createToastMessage("Successfully joined!")
                }
                startActivity(Intent(this, Home::class.java))
                finish()
            }
            else {
                val rejectionReason = jsonObj.getString("reason")
                runOnUiThread {
                    displayInputErrorForGroupID(rejectionReason)
                    createToastMessage("Couldn't join!")
                }
            }
        }
    }

    fun createToastMessage(text: String) {
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
        toast.setMargin(0.004F,0.1F)
        val view = toast.view
        view.setBackgroundColor(Color.BLACK)
        val toastMessage = toast.view.findViewById<TextView>(android.R.id.message)
        toastMessage.setTextColor(Color.WHITE)
        toast.show()
    }

    fun setupListener(){
        joinBtnJoinToHousehold.setOnClickListener {
            if(isBound) {
                val currentUser = usernameJoinToHousehold.text.toString()
                val currentGroupID = groupIDJoinToHousehold.text.toString()
                if (currentUser == "") {
                    runOnUiThread {
                        displayInputErrorForUsername("This field can't be empty!")
                        createToastMessage("Couldn't join!")
                    }
                } else if (currentGroupID == "") {
                    runOnUiThread {
                        displayInputErrorForGroupID("This field can't be empty!")
                        createToastMessage("Couldn't join!")
                    }
                } else {
                    sendJoinToHouseHoldMessage(currentGroupID)
                    clearInputErrors()
                    saveUsernameAndGroupIDInPreferences(currentUser, currentGroupID)
                }
            }
        }
    }

}



