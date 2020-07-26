package com.example.household;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InputReader implements Runnable {
    private ClientSocket clientSocket;
    private BufferedReader in;

    public InputReader(ClientSocket socket){
        clientSocket = socket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try{
           while(true) {
                char[] buffer = new char[4096];
                while(in.read(buffer, 0, 4096) > -1){
                    clientSocket.getClientCallbacks().receiveMessage(String.valueOf(buffer));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
