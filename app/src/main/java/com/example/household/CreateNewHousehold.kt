package com.example.household

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log.d
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_new_household.*
import org.json.JSONObject

class CreateNewHousehold: AppCompatActivity(), ServiceCallbacks {

    private var mBoundService: SocketService? = null
    private var isBound = false
    private val mConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBoundService = (service as SocketService.LocalBinder).service
            mBoundService?.setServiceCallbacks(this@CreateNewHousehold)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBoundService = null
            mBoundService?.setServiceCallbacks(null)
        }
    }

    private fun doBindService() {
        bindService(Intent(this@CreateNewHousehold, SocketService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        isBound = true
    }

    private fun doUnbindService() {
        if (isBound) {
            unbindService(mConnection)
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBindService()
        setContentView(R.layout.create_new_household)
        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    fun sendCreateNewHouseholdMessage(){
        val jsonObj = JSONObject()
        jsonObj.put("command", "createNewHousehold")
        mBoundService!!.sendMessage(jsonObj)
    }

    fun displayInputErrorForUsername(reason: String){
        usernameErrorCreateNewHousehold.text = reason
        usernameErrorCreateNewHousehold.setTextColor(Color.RED)
        usernameCreateNewHousehold.setBackgroundResource(R.drawable.error_outline);
    }

    fun clearInputErrors(){
        usernameErrorCreateNewHousehold.text = ""
        usernameCreateNewHousehold.setBackgroundResource(0)
    }

    fun saveMemberInformationInPreferences(user: String, groupID: String){
        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
        val myEdit = preferences.edit()
        myEdit.putString("currentUser", user)
        myEdit.putString("currentGroupID", groupID)
        myEdit.putBoolean("loggedIn", true)
        myEdit.commit()
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

    override fun receiveMessage(msg: String?) {
        val jsonObj = JSONObject(msg)
        val command: String = jsonObj.getString("command")
        if(command == "createNewHousehold"){
            val responseStatus = jsonObj.getString("status")
            if(responseStatus == "accepted"){
                val currentUser = usernameCreateNewHousehold.text.toString()
                val groupID = jsonObj.getString("groupID")
                saveMemberInformationInPreferences(currentUser, groupID)

                runOnUiThread {
                    createToastMessage("Successfully joined!")
                }
                startActivity(Intent(this, Home::class.java))
                finish()
            }
            else {
                val rejectionReason = jsonObj.getString("reason")
                runOnUiThread {
                    createToastMessage(rejectionReason)
                }
            }
        }
    }

    fun setupListeners(){
        createBtnJoinToHousehold.setOnClickListener {
            if(isBound) {
                val currentUser = usernameCreateNewHousehold.text.toString()
                if(currentUser == "") {
                    runOnUiThread {
                        displayInputErrorForUsername("This field can't be empty!")
                        createToastMessage("Couldn't create!")
                    }
                } else {
                    sendCreateNewHouseholdMessage()
                    clearInputErrors()
                }
            }
        }
    }
}