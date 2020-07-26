package com.example.household;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.*;

public class ClientSocket {
    private Socket clientSocket = null;
    private PrintWriter out;
    private ClientCallbacks clientCallbacks;

    public ClientCallbacks getClientCallbacks() {
        return clientCallbacks;
    }

    public void setClientCallbacks(ClientCallbacks clientClbk) {
        clientCallbacks = clientClbk;
    }

    public InputStream getInputStream() {
        try {
            return clientSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            Thread listener = new Thread(new InputReader(ClientSocket.this));
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        out.close();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientSocket = null;
    }

    public void sendMessage(JSONObject jsonObj) {
        Thread sender = new Thread(new Send(jsonObj));
        sender.start();
    }

    class Send implements Runnable {
        private JSONObject jsonMessage;
        public Send(JSONObject jsonObj){
            jsonMessage = jsonObj;
        }

        @Override
        public void run() {
            if (out != null && !out.checkError()) {
                String message = jsonMessage.toString();
                out.println(message);
                out.flush();
            }
        }
    }
}


