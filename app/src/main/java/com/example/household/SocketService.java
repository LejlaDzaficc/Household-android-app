package com.example.household;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import org.json.JSONObject;

public class SocketService extends Service implements ClientCallbacks {

    ClientSocket clientSocket;
    private IBinder myBinder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;
    private String IPaddress = "10.0.2.2";
    private int port = 1500;

    @Override
    public IBinder onBind(Intent intent){
        return myBinder;
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    public void setServiceCallbacks(ServiceCallbacks serviceClbk) {
        serviceCallbacks = serviceClbk;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clientSocket.stopConnection();
        clientSocket.setClientCallbacks(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Thread connectToSocket = new Thread(new connectSocket());
        connectToSocket.start();
        return START_STICKY;
    }

    class connectSocket implements Runnable {
        @Override
        public void run() {
            try {
                clientSocket = new ClientSocket();
                clientSocket.startConnection(IPaddress, port);
                clientSocket.setClientCallbacks(SocketService.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(JSONObject jsonObj){
        clientSocket.sendMessage(jsonObj);
    }

    @Override
    public void receiveMessage(String msg) {
        if(serviceCallbacks != null)
            serviceCallbacks.receiveMessage(msg);
    }
}
