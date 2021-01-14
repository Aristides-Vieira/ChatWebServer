package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {


    private final int portNumber = 46667;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<ClientHandler> arrayList;
    private ExecutorService cachedPool = Executors.newCachedThreadPool();


    public Server() {
        arrayList = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUsername(String userName) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkPassword(String username, String password) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getUserName().equals(username)) {
                if (arrayList.get(i).getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }


    public void broadcast(String messageToBeBroadcast, ClientHandler messageFromThisClient) {
        for (ClientHandler arrayClient : arrayList) {
            if (arrayClient != messageFromThisClient) {
                arrayClient.recieveBroadcast(messageToBeBroadcast);
            }
        }
    }

    public void broadcast(String messageToBeBroadcast) {
        for (ClientHandler arrayClient : arrayList) {
            arrayClient.recieveBroadcast(messageToBeBroadcast);
        }
    }

    public void clientXRunning(ClientHandler clientX) {
        cachedPool.submit(clientX);
        System.out.println("Client connected");
    }

    public void addClientXtoArray(ClientHandler clientX) {
        arrayList.add(clientX);

    }


    public void start() {
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                ClientHandler clientX = new ClientHandler(clientSocket, this);

                clientX.init();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
